package es.upm.oeg.epnoi.collector.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 18/02/15.
 */

@Component
public class RssProcessor implements Processor{

    Logger log = LoggerFactory.getLogger(RssProcessor.class);


    @Override
    public void process(Exchange exchange) throws Exception {
        log.debug("processing exchange: {}", exchange);


    }
}
