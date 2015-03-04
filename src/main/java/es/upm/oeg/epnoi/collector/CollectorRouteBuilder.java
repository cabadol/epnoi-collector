package es.upm.oeg.epnoi.collector;

import es.upm.oeg.epnoi.collector.processor.ContextBuilder;
import es.upm.oeg.epnoi.collector.processor.ErrorHandler;
import es.upm.oeg.epnoi.collector.processor.TimeClock;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;

@Component
public class CollectorRouteBuilder extends RouteBuilder {

    Logger log = LoggerFactory.getLogger(CollectorRouteBuilder.class);

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    TimeClock timeClock;

    @Autowired
    ContextBuilder contextBuilder;

    @Override
    public void configure() throws Exception {

        log.info("Initializing camel context for Epnoi Collector");

        onException(MalformedURLException.class)
                .process(errorHandler).stop();

        onException(IOException.class)
                .maximumRedeliveries(3)
                .process(errorHandler).stop();

        Namespaces ns = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/")
                .add("dc", "http://purl.org/dc/elements/1.1/")
                .add("provenance", "http://www.openarchives.org/OAI/2.0/provenance")
                .add("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/");

        /************************************************************************************************************
         * RSS Feeds
         ************************************************************************************************************/

        // Slashdot
        from("rss:http://rss.slashdot.org/Slashdot/slashdot?" +
                "splitEntries=true&consumer.delay=5000&consumer.initialDelay=1000&feedHeader=true$filter=true").
                marshal().rss().
                setHeader(Header.SOURCE_NAME,                   constant("slashdot")).
                setHeader(Header.SOURCE_URI,                    constant("http://www.epnoi.org/feeds/slashdot")).
                setHeader(Header.SOURCE_URL,                    constant("http://rss.slashdot.org/Slashdot/slashdot")).
                setHeader(Header.SOURCE_PROTOCOL,               constant("rss")).
                setHeader(Header.PUBLICATION_TITLE,             xpath("//item/title/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_DESCRIPTION,       xpath("//item/description/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_PUBLISHED,         xpath("//item/dc:date/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_URI,               xpath("//item/link/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_URL_REMOTE,        xpath("//item/link/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_LANGUAGE,          xpath("//channel/dc:language/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_RIGHTS,            xpath("//channel/dc:rights/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_CREATORS,          xpath("string-join(//oai:metadata/oai:dc/dc:creator/text(),\";\")", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_FORMAT,            constant("htm")).
                setHeader(Header.PUBLICATION_REFERENCE_FORMAT,  constant("xml")).
                to("seda:inbox");


        /************************************************************************************************************
         * OAI-PMH Data Providers
         ************************************************************************************************************/

        // UCM
        from("oaipmh://eprints.ucm.es/cgi/oai2?" +
                "delay=60000&initialDelay=1000&from=2015-03-03T13:00:00Z").
                setHeader(Header.SOURCE_NAME, constant("ucm")).
                setHeader(Header.SOURCE_URI,                    constant("http://www.epnoi.org/oai-providers/ucm")).
                setHeader(Header.SOURCE_URL, constant("http://eprints.ucm.es/cgi/oai2")).
                setHeader(Header.SOURCE_PROTOCOL,               constant("oaipmh")).
                setHeader(Header.PUBLICATION_TITLE,             xpath("//oai:metadata/oai:dc/dc:title/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_DESCRIPTION,       xpath("//oai:metadata/oai:dc/dc:description/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_PUBLISHED,         xpath("//oai:header/oai:datestamp/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_URI,               xpath("//oai:metadata/oai:dc/dc:identifier/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_URL_REMOTE,        xpath("//oai:metadata/oai:dc/dc:identifier/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_LANGUAGE,          xpath("//oai:metadata/oai:dc/dc:language/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_RIGHTS,            xpath("//oai:metadata/oai:dc/dc:rights/text()", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_CREATORS,          xpath("string-join(//oai:metadata/oai:dc/dc:creator/text(),\";\")", String.class).namespaces(ns)).
                setHeader(Header.PUBLICATION_FORMAT,            constant("pdf")).
                setHeader(Header.PUBLICATION_REFERENCE_FORMAT,  constant("xml")).
                to("seda:inbox");


        /************************************************************************************************************
         * Internal routes
         ************************************************************************************************************/

        // Retrieves publication content and saves related files
        from("seda:inbox").
                process(timeClock).
                setHeader(Header.PUBLICATION_REFERENCE_URL,
                        simple("${in.header." + Header.SOURCE_PROTOCOL + "}/" +
                                "${in.header." + Header.SOURCE_NAME + "}/" +
                                "${in.header." + Header.PUBLICATION_PUBLISHED_DATE + "}/" +
                                "resource-${in.header." + Header.PUBLICATION_PUBLISHED_MILLIS + "}.${in.header." + Header.PUBLICATION_REFERENCE_FORMAT + "}")).
                to("file:target/?fileName=${in.header." + Header.PUBLICATION_REFERENCE_URL + "}").
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI,    simple("${header." + Header.PUBLICATION_URL_REMOTE + "}")).
                to("http://dummyhost?throwExceptionOnFailure=false").
                setHeader(Header.PUBLICATION_URL_LOCAL,
                        simple("${in.header." + Header.SOURCE_PROTOCOL + "}/" +
                                "${in.header." + Header.SOURCE_NAME + "}/" +
                                "${in.header." + Header.PUBLICATION_PUBLISHED_DATE + "}/" +
                                "resource-${in.header." + Header.PUBLICATION_PUBLISHED_MILLIS + "}.${in.header." + Header.PUBLICATION_FORMAT + "}")).
                to("file:target/?fileName=${in.header." + Header.PUBLICATION_URL_LOCAL + "}").
                to("seda:processing");

        // Creates a json context message for UIA
        from("seda:processing").
                process(contextBuilder).
                to("stream:out");

    }
}
