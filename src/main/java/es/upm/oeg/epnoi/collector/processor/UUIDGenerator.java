package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.CollectorProperty;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDGenerator implements Processor {


    @Override
    public void process(Exchange exchange) throws Exception {
        String uuid = UUID.randomUUID().toString();
        exchange.setProperty(CollectorProperty.PUBLICATION_UUID,uuid);
    }
}
