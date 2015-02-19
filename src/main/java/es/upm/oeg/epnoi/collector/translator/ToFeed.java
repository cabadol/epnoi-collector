package es.upm.oeg.epnoi.collector.translator;

import es.upm.oeg.epnoi.collector.model.Feed;
import es.upm.oeg.epnoi.collector.parser.RSSFeedParser;
import es.upm.oeg.epnoi.collector.processor.DateStamp;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 19/02/15.
 */

@Component
public class ToFeed implements Processor {

    Logger log = LoggerFactory.getLogger(ToFeed.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        String body = exchange.getIn().getBody(String.class);

        RSSFeedParser parser = new RSSFeedParser();
        log.trace("ready to parse feed: {}", body);
        Feed feed = parser.readFeed(body);
        log.debug("Feed: {}", feed);

        if (feed.getPubDate() == "") {
            String date = exchange.getIn().getHeader(DateStamp.DATE, String.class);
            feed.setPubDate(date);
            log.debug("PubDate updated to: {}", date);
        }
        String uri = exchange.getIn().getHeader(Feed.HEADER.URI, String.class);
        feed.setURI(uri);
        log.debug("URI updated to: {}", uri);

        exchange.getIn().setBody(feed, Feed.class);
    }
}
