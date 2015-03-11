package es.upm.oeg.epnoi.collector.routes;


import es.upm.oeg.epnoi.collector.CollectorRouteBuilder;
import es.upm.oeg.epnoi.collector.CollectorProperty;
import es.upm.oeg.epnoi.collector.model.Provider;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRouteBuilder implements IRouteBuilder{

    private static final Logger LOG = LoggerFactory.getLogger(AbstractRouteBuilder.class);

    public static final String XPATH = "$";

    protected final Namespaces ns;

    public AbstractRouteBuilder(){
        this.ns = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/")
                .add("dc", "http://purl.org/dc/elements/1.1/")
                .add("provenance", "http://www.openarchives.org/OAI/2.0/provenance")
                .add("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/")
                .add("rss","http://purl.org/rss/1.0/");
    }


    protected void addProperties(RouteBuilder builder, ProcessorDefinition<RouteDefinition> def, Provider provider){
        add(CollectorProperty.SOURCE_NAME, provider.getName(), def, builder);
        add(CollectorProperty.SOURCE_URI, provider.getUri(), def, builder);
        add(CollectorProperty.SOURCE_URL, provider.getUrl(), def, builder);
        add(CollectorProperty.SOURCE_PROTOCOL, provider.getProtocol(), def, builder);
        add(CollectorProperty.PUBLICATION_TITLE, provider.getPublication().getTitle(), def, builder);
        add(CollectorProperty.PUBLICATION_DESCRIPTION, provider.getPublication().getDescription(), def, builder);
        add(CollectorProperty.PUBLICATION_PUBLISHED, provider.getPublication().getPublished(), def, builder);
        add(CollectorProperty.PUBLICATION_URI, provider.getPublication().getUri(), def, builder);
        add(CollectorProperty.PUBLICATION_URL_REMOTE, provider.getPublication().getUrl(), def, builder);
        add(CollectorProperty.PUBLICATION_LANGUAGE, provider.getPublication().getLanguage(), def, builder);
        add(CollectorProperty.PUBLICATION_RIGHTS, provider.getPublication().getRights(), def, builder);
        add(CollectorProperty.PUBLICATION_CREATORS, provider.getPublication().getCreators(), def, builder);
        add(CollectorProperty.PUBLICATION_FORMAT, provider.getPublication().getFormat(), def, builder);
        add(CollectorProperty.PUBLICATION_METADATA_FORMAT, "xml", def, builder);
        LOG.debug("Route definition: {}", def);
    }

    private void add(String property, String expression, ProcessorDefinition<RouteDefinition> def, RouteBuilder builder ){
        if (expression.startsWith(XPATH)){
            def.setProperty(property, builder.xpath(expression.replace(XPATH,""),String.class).namespaces(ns));
        }else{
            def.setProperty(property,builder.constant(expression));
        }
    }

    protected void nextStage(ProcessorDefinition<RouteDefinition> def){
        def.to(CollectorRouteBuilder.INBOX_ROUTE);
    }
}
