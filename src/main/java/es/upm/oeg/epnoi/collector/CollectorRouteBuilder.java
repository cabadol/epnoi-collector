package es.upm.oeg.epnoi.collector;

import es.upm.oeg.epnoi.collector.model.Header;
import es.upm.oeg.epnoi.collector.processor.DateStamp;
import es.upm.oeg.epnoi.collector.processor.ErrorHandler;
import es.upm.oeg.epnoi.collector.processor.RSSContentProcessor;
import es.upm.oeg.epnoi.collector.processor.RSSContentRetriever;
import es.upm.oeg.epnoi.collector.translator.ToContent;
import es.upm.oeg.epnoi.collector.translator.ToFeed;
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
    RSSContentProcessor rssContentProcessor;

    @Autowired
    RSSContentRetriever rssContentRetriever;

    @Autowired
    ToFeed toFeed;

    @Autowired
    ToContent toContent;

    @Autowired
    DateStamp dateStamp;


    @Override
    public void configure() throws Exception {

        log.info("Initializing camel context for Epnoi Collector");

        onException(MalformedURLException.class)
                .process(errorHandler).stop();

        onException(IOException.class)
                .maximumRedeliveries(3)
                .process(errorHandler).stop();

        /************************************************************************************************************
         * RSS Feeds
         ************************************************************************************************************/

        // RSS Namespaces
        Namespaces nsRSS = new Namespaces("dc", "http://purl.org/dc/elements/1.1/");

        from("rss:http://rss.slashdot.org/Slashdot/slashdot?" +
                "splitEntries=true&consumer.delay=5000&consumer.initialDelay=1000&feedHeader=true$filter=true").
                process(dateStamp).
                setHeader(Header.PROVIDER_PROTOCOL, constant("rss")).
                setHeader(Header.PROVIDER_NAME, constant("slashdot")).
                setHeader(Header.PROVIDER_URI, constant("http://www.epnoi.org/feeds/slashdot")).
                setHeader(Header.PROVIDER_URL, constant("http://rss.slashdot.org/Slashdot/slashdot")).
                marshal().rss().
                setHeader(Header.RESOURCE_DESCRIPTOR_FORMAT, constant("xml")).
                setHeader(Header.RESOURCE_CONTENT_FORMAT, constant("htm")).
                setHeader(Header.RESOURCE_CONTENT_REMOTE_PATH, xpath("//item/link/text()", String.class).namespaces(nsRSS)).
                to("seda:inputResource");
//
//
//        // RSS Feed process
//        from("seda:inputFeed").
//                to("file:target/?fileName=rss/${in.header.EPNOI.PROVIDER.NAME}/${in.header.EPNOI.DATE}/item-${in.header.EPNOI.TIME}.xml").
//                process(toFeed).
//                process(rssContentRetriever).
//                to("seda:inputItemContent");
//
//
//        // RSS Item Content process
//        from("seda:inputItemContent").
//                to("file:target/?fileName=rss/${in.header.EPNOI.PROVIDER.NAME}/${in.header.EPNOI.DATE}/item-${in.header.EPNOI.TIME}-content.txt").
//                setHeader(Item.ID.URI, simple("${in.header.EPNOI.PROVIDER.NAME}/${in.header.EPNOI.DATE}/item-${in.header.EPNOI.TIME}-content.txt")).
//                process(toContent).
//                process(rssContentProcessor).
//                to("stream:out");


        /************************************************************************************************************
         * OAI-PMH Data Providers
         ************************************************************************************************************/
        // OAI-PMH Namespaces
        Namespaces nsOAI = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/");
        nsOAI.add("dc", "http://purl.org/dc/elements/1.1/");
        nsOAI.add("provenance", "http://www.openarchives.org/OAI/2.0/provenance");
        nsOAI.add("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");


        // UCM
        from("oaipmh://eprints.ucm.es/cgi/oai2?" +
                "delay=60000&initialDelay=1000&from=2015-03-03T13:00:00Z").
                process(dateStamp).
                setHeader(Header.PROVIDER_PROTOCOL, constant("oaipmh")).
                setHeader(Header.PROVIDER_NAME, constant("ucm")).
                setHeader(Header.PROVIDER_URI, constant("http://www.epnoi.org/oai-providers/ucm")).
                setHeader(Header.PROVIDER_URL, constant("http://eprints.ucm.es/cgi/oai2")).
                setHeader(Header.RESOURCE_DESCRIPTOR_FORMAT, constant("xml")).
                setHeader(Header.RESOURCE_CONTENT_FORMAT, constant("pdf")).
                setHeader(Header.RESOURCE_CONTENT_REMOTE_PATH, xpath("//oai:metadata/oai:dc/dc:identifier/text()", String.class).namespaces(nsOAI)).
                to("seda:inputResource");


        /************************************************************************************************************
         * Common retriever and storer
         ************************************************************************************************************/
        from("seda:inputResource").
                setHeader(Header.RESOURCE_DESCRIPTOR_LOCAL_PATH,
                        simple("${in.header." + Header.PROVIDER_PROTOCOL + "}/" +
                                "${in.header." + Header.PROVIDER_NAME + "}/" +
                                "${in.header." + Header.TIME_DATE + "}/" +
                                "resource-${in.header." + Header.TIME_MILLIS + "}.${in.header." + Header.RESOURCE_DESCRIPTOR_FORMAT + "}")).
                to("file:target/?fileName=${in.header." + Header.RESOURCE_DESCRIPTOR_LOCAL_PATH + "}").
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI, simple("${header." + Header.RESOURCE_CONTENT_REMOTE_PATH + "}")).
                to("http://dummyhost?throwExceptionOnFailure=false").
                setHeader(Header.RESOURCE_CONTENT_LOCAL_PATH,
                        simple("${in.header." + Header.PROVIDER_PROTOCOL + "}/" +
                                "${in.header." + Header.PROVIDER_NAME + "}/" +
                                "${in.header." + Header.TIME_DATE + "}/" +
                                "resource-${in.header." + Header.TIME_MILLIS + "}.${in.header." + Header.RESOURCE_CONTENT_FORMAT + "}")).
                to("file:target/?fileName=${in.header." + Header.RESOURCE_CONTENT_LOCAL_PATH + "}").
                to("stream:out");


    }
}
