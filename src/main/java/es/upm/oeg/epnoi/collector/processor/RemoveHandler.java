package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.Header;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RemoveHandler implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(RemoveHandler.class);

    @Override
    public void process(Exchange exchange) throws Exception {


        LOG.info("Resource deleted: {}", exchange.getProperty(Header.PUBLICATION_URI));
    }
}
