package es.upm.oeg.epnoi.collector.processor;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 19/02/15.
 */
@Component
public class ErrorHandler {

    Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    public void handleMalformedURL(Exchange exchange) {

        log.error("Error processing message: {}", exchange.getIn());

    }
}
