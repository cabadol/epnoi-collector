package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.model.Feed;
import es.upm.oeg.epnoi.collector.model.Item;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;

@Component
public class RSSContentRetriever implements Processor {

    Logger log = LoggerFactory.getLogger(RSSContentRetriever.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Feed feed = exchange.getIn().getBody(Feed.class);

        URL url;
        InputStream is = null;
        BufferedReader br;
        String line;
        String content = "fail";
        StringWriter pw = null;

        if ((feed.getItems() == null) || (feed.getItems().size() <= 0))
                return;

        Item item = feed.getItems().get(0); // Feed only has one item because RSS component split it

        log.info("Retrieving content from {} ", item.getLink());

        try {
            url = new URL(item.getLink());
            is = url.openStream(); // throws an IOException
            br = new BufferedReader(new InputStreamReader(is));

            pw = new StringWriter();
            while ((line = br.readLine()) != null) {
                pw.write(line);
                pw.write(System.getProperty("line.separator"));

            }
        } finally {
            if (is != null)
                is.close();
            if (pw != null)
                content = pw.toString();
            pw.close();
        }

        // Update exchange body from Feed to Content String
        exchange.getIn().setBody(content, String.class);
    }


}
