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

        log.debug("Creating Routes");

        onException(MalformedURLException.class)
                .process(errorHandler).stop();

        onException(IOException.class)
                .maximumRedeliveries(3)
                .process(errorHandler).stop();

        // Slashdot RSS
//        from("rss:http://rss.slashdot.org/Slashdot/slashdot?" +
//                "splitEntries=true&consumer.delay=5000&consumer.initialDelay=1000&feedHeader=true$filter=true").
//                setHeader(Header.PROVIDER.NAME, simple("slashdot")).
//                setHeader(Header.PROVIDER.URI, simple("http://www.epnoi.org/feeds/slashdot")).
//                setHeader(Header.PROVIDER.URL, simple("http://rss.slashdot.org/Slashdot/slashdot")).
//                process(dateStamp).
//                marshal().rss().
//                to("seda:inputFeed");
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
        Namespaces ns = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/");
        ns.add("dc", "http://purl.org/dc/elements/1.1/");
        ns.add("provenance", "http://www.openarchives.org/OAI/2.0/provenance");
        ns.add("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");


        // UCM
        from("oaipmh://eprints.ucm.es/cgi/oai2?" +
                "delay=60000&initialDelay=1000&from=2015-03-03T05:00:00Z").
                process(dateStamp).
                setHeader(Header.PROVIDER.PROTOCOL, simple("oaipmh")).
                setHeader(Header.PROVIDER.NAME, simple("ucm")).
                setHeader(Header.PROVIDER.URI, simple("http://www.epnoi.org/oai-providers/ucm")).
                setHeader(Header.PROVIDER.URL, simple("http://eprints.ucm.es/cgi/oai2")).
                setHeader(Header.RESOURCE.FORMAT, simple("pdf")).
                setHeader(Header.RESOURCE.PATH, xpath("//oai:metadata/oai:dc/dc:identifier/text()", String.class).namespaces(ns)).
                to("seda:inputResource");


        /************************************************************************************************************
         * Common retriever and processor
         ************************************************************************************************************/
        from("seda:inputResource").
                to("file:target/?fileName=${in.header.EPNOI.PROVIDER.PROTOCOL}/${in.header.EPNOI.PROVIDER.NAME}/${in.header.EPNOI.DATE}/resource-${in.header.EPNOI.TIME}.xml").
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI, simple("${header.EPNOI.RESOURCE.PATH}")).
                to("http://dummyhost").
                to("file:target/?fileName=${in.header.EPNOI.PROVIDER.PROTOCOL}/${in.header.EPNOI.PROVIDER.NAME}/${in.header.EPNOI.DATE}/resource-${in.header.EPNOI.TIME}.${in.header.EPNOI.RESOURCE.FORMAT}").
                to("stream:out");


    }
}
