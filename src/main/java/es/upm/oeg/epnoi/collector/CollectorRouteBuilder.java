package es.upm.oeg.epnoi.collector;

import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by cbadenes on 18/02/15.
 */
public class CollectorRouteBuilder extends RouteBuilder {

    Logger log = LoggerFactory.getLogger(CollectorRouteBuilder.class);

    @Override
    public void configure() throws Exception {

        log.debug("Creating Routes");

        errorHandler(deadLetterChannel("mock:error"));

        // RSS Hoarder
        from("rss:http://rss.slashdot.org/Slashdot/slashdot?" +
                "splitEntries=true&consumer.delay=5000&consumer.initialDelay=1000&feedHeader=true$filter=true").
                marshal().
                rss().
                multicast().parallelProcessing().
                    to("file:target/slashdot?fileName=item-${date:now:yyyyMMdd_HHmmss}.xml",
                        "seda:inputSlashdot");


        // RSS Harvester
        from("seda:inputSlashdot").
                to("stream:out");


    }
}
