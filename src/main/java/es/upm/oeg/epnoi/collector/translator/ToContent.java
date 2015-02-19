package es.upm.oeg.epnoi.collector.translator;

import es.upm.oeg.epnoi.collector.model.Item;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.ContentHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by cbadenes on 19/02/15.
 */
@Component
public class ToContent implements Processor{

    @Override
    public void process(Exchange exchange) throws Exception {

        String raw = exchange.getIn().getBody(String.class);

        String resourceURI = exchange.getIn().getHeader(Item.ID.URI, String.class);

        Metadata metadata = new Metadata();
        metadata.set(Metadata.RESOURCE_NAME_KEY, resourceURI);
        InputStream is = null;
        ContentHandler handler = null;
        try {

            is = new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8));

            Parser parser = new AutoDetectParser();
            handler = new BodyContentHandler(-1);

            ParseContext context = new ParseContext();
            context.set(Parser.class, parser);

            parser.parse(is, handler, metadata, new ParseContext());
        } finally {
            is.close();
        }

        String formatted =  handler.toString();

        // Update exchange body
        exchange.getIn().setBody(formatted, String.class);
    }
}
