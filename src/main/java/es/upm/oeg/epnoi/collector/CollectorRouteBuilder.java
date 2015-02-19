package es.upm.oeg.epnoi.collector;

import es.upm.oeg.epnoi.collector.model.Feed;
import es.upm.oeg.epnoi.collector.model.Item;
import es.upm.oeg.epnoi.collector.processor.DateStamp;
import es.upm.oeg.epnoi.collector.processor.ErrorHandler;
import es.upm.oeg.epnoi.collector.processor.RSSContentRetriever;
import es.upm.oeg.epnoi.collector.processor.RSSContentProcessor;
import es.upm.oeg.epnoi.collector.translator.ToContent;
import es.upm.oeg.epnoi.collector.translator.ToFeed;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Created by cbadenes on 18/02/15.
 */
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
                .to("bean:errorHandler?method=handleMalformedURL(${exchange}, true)");

        onException(IOException.class)
                .maximumRedeliveries(2)
                .to("bean:errorHandler?method=handleIO(${exchange}, true)");

        // Slashdot RSS
        from("rss:http://rss.slashdot.org/Slashdot/slashdot?" +
                "splitEntries=true&consumer.delay=60000&consumer.initialDelay=2000&feedHeader=true$filter=true").
                setHeader(Feed.HEADER.NAME, simple("slashdot")).
                setHeader(Feed.HEADER.URI, simple("http://www.epnoi.org/feeds/slashdot")).
                setHeader(Feed.HEADER.URL, simple("http://rss.slashdot.org/Slashdot/slashdot")).
                process(dateStamp).
                marshal().rss().
                to("seda:inputFeed");


        // RSS Feed process
        from("seda:inputFeed").
                to("file:target/?fileName=${in.header.Feed.Name}/${in.header.date}/item-${in.header.time}.xml").
                process(toFeed).
                process(rssContentRetriever).
                to("seda:inputItemContent");


        // RSS Item Content process
        from("seda:inputItemContent").
                to("file:target/?fileName=${in.header.Feed.Name}/${in.header.date}/item-${in.header.time}-content.txt").
                setHeader(Item.HEADER.URI, simple("${in.header.Feed.Name}/${in.header.date}/item-${in.header.time}-content.txt")).
                process(toContent).
                process(rssContentProcessor).
                to("stream:out");



    }
}
