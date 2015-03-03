package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.camel.oaipmh.model.ElementType;
import es.upm.oeg.camel.oaipmh.model.OAIPMHtype;
import es.upm.oeg.camel.oaipmh.model.RecordType;
import es.upm.oeg.epnoi.collector.model.Header;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.util.List;


@Component
public class OAIRecordRetriever implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(OAIRecordRetriever.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        OAIPMHtype message = exchange.getIn().getHeader("OAIPMH.Message", OAIPMHtype.class);

        //TODO Create a processor for each data provider because the referenced document is different.

        // One record by message
        RecordType record = message.getListRecords().getRecord().get(0);

        List<JAXBElement<ElementType>> metadata = record.getMetadata().getDc().getTitleOrCreatorOrSubject();

        String url = "";
        for (JAXBElement<ElementType> element: metadata){

            String parameter = element.getName().getLocalPart();
            if (parameter.equals("identifier")) {
                url = element.getValue().getValue();
            }

        }

        exchange.getIn().setHeader(Header.RESOURCE.PATH,url);


        LOG.info("Added Header property: {}={}", Header.RESOURCE.PATH,url);

    }
}
