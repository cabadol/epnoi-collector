package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.model.Context;
import es.upm.oeg.epnoi.collector.model.Item;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by cbadenes on 18/02/15.
 */

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

        // Send context to Core UIA

        // change the existing message to say Hello
        exchange.getIn().setBody(feedContext, Context.class);
    }


//    private void toCore(){
//        //
////        if (this.harvester.getCore() != null) {
////
////            InformationSource informationSource = (InformationSource) this.harvester
////                    .getCore()
////                    .getInformationHandler()
////                    .get(this.manifest.getURI(),
////                            InformationSourceRDFHelper.INFORMATION_SOURCE_CLASS);
////            feedContext.getParameters().put(
////                    Context.INFORMATION_SOURCE_NAME,
////                    informationSource.getName());
////            feedContext.getParameters().put(
////                    Context.INFORMATION_SOURCE_URI, manifest.getURI());
////
////            this.harvester.getCore().getInformationHandler()
////                    .put(feed, feedContext);
////        } else {
////
////            System.out.println("Result: Feed> " + feed);
////            for (Item item : feed.getItems()) {
////                System.out.println("		 Item> " + item);
////            }
////
////            System.out.println("Result: Context> " + feedContext);
////        }
//    }


}
