package es.upm.oeg.epnoi.collector.processor;


import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import es.upm.oeg.epnoi.collector.Header;
import es.upm.oeg.epnoi.collector.model.Context;
import es.upm.oeg.epnoi.collector.model.Publication;
import es.upm.oeg.epnoi.collector.model.Reference;
import es.upm.oeg.epnoi.collector.model.Source;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class ContextBuilder implements Processor {

    private static final Logger LOG = LoggerFactory.getLogger(ContextBuilder.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        Context context = new Context();

        Source source = new Source();
        source.setName(exchange.getIn().getHeader(Header.SOURCE_NAME,String.class));
        source.setUri(exchange.getIn().getHeader(Header.SOURCE_URI,String.class));
        source.setUrl(exchange.getIn().getHeader(Header.SOURCE_URL,String.class));
        source.setProtocol(exchange.getIn().getHeader(Header.SOURCE_PROTOCOL,String.class));
        context.setSource(source);

        Publication publication = new Publication();
        publication.setTitle(exchange.getIn().getHeader(Header.PUBLICATION_TITLE,String.class));
        publication.setUri(exchange.getIn().getHeader(Header.PUBLICATION_URI, String.class));
        publication.setUrl("file://" + exchange.getIn().getHeader(Header.PUBLICATION_URL_LOCAL, String.class));
        publication.setFormat(exchange.getIn().getHeader(Header.PUBLICATION_FORMAT, String.class));
        publication.setLanguage(exchange.getIn().getHeader(Header.PUBLICATION_LANGUAGE,String.class));
        publication.setPublished(exchange.getIn().getHeader(Header.PUBLICATION_PUBLISHED,String.class));
        publication.setRights(exchange.getIn().getHeader(Header.PUBLICATION_RIGHTS,String.class));

        publication.setDescription(exchange.getIn().getHeader(Header.PUBLICATION_DESCRIPTION,String.class));


        Iterable<String> iterator = Splitter.on(';').trimResults().omitEmptyStrings().split(exchange.getIn().getHeader(Header.PUBLICATION_CREATORS, String.class));
        ArrayList<String> creators = Lists.newArrayList(iterator);
        publication.setCreators(creators);


        Reference reference = new Reference();
        reference.setFormat(exchange.getIn().getHeader(Header.PUBLICATION_REFERENCE_FORMAT,String.class));
        reference.setUrl("file://"+exchange.getIn().getHeader(Header.PUBLICATION_REFERENCE_URL,String.class));

        publication.setReference(reference);
        context.add(publication);


        Gson gson = new Gson();
        String json = gson.toJson(context);


        exchange.getIn().setBody(json,String.class);
        LOG.debug("Exchange IN body updated to: {}", json);

    }
}

