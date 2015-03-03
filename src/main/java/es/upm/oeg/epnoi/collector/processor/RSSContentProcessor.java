package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.model.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class RSSContentProcessor implements Processor{


    Logger log = LoggerFactory.getLogger(RSSContentProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        String content = exchange.getIn().getBody(String.class);

        log.info("Processing content: {}", content);

        Context feedContext = new Context();

        String itemURI = exchange.getIn().getHeader(Item.ID.URI, String.class);

        feedContext.getElements().put(itemURI, content);

        String feedURI = exchange.getIn().getHeader(Header.PROVIDER.URI, String.class);


//        InformationSource informationSource = (InformationSource) this.harvester
//                .getCore()
//                .getInformationHandler()
//                .get(feedURI,
//                        InformationSourceRDFHelper.INFORMATION_SOURCE_CLASS);
//
//        feedContext.getParameters().put(
//                Context.INFORMATION_SOURCE_NAME,
//                informationSource.getName());

        feedContext.getParameters().put(Context.INFORMATION_SOURCE_URI, feedURI);

        // Send context to Core UIA

        // change the existing message to send the feed context
        exchange.getIn().setBody(feedContext, Context.class);

        // this.harvester.getCore().getInformationHandler().put(feed, feedContext);
    }



}
