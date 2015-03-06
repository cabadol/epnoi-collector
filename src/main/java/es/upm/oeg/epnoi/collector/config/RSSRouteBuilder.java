package es.upm.oeg.epnoi.collector.config;


import es.upm.oeg.epnoi.collector.model.Provider;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSSRouteBuilder extends AbstractRouteBuilder{

    public static final String PROTOCOL = "rss";

    private final Provider provider;

    private static final Logger LOG = LoggerFactory.getLogger(RSSRouteBuilder.class);

    public RSSRouteBuilder(Provider provider){
        // default Delay value
        if (provider.getDelay() == null){
            provider.setDelay(5000);
        }
        // default Initial Delay value
        if (provider.getInitialDelay() == null){
            provider.setInitialDelay(1000);
        }
        // Default Title xpath expression
        if (provider.getPublication().getTitle() == null){
            provider.getPublication().setTitle(XPATH+"//rss:item/rss:title/text()");
        }
        // Default Description xpath expression
        if (provider.getPublication().getDescription() == null){
            provider.getPublication().setDescription(XPATH+"//rss:item/rss:description/text()");
        }
        // Default Published xpath expression
        if (provider.getPublication().getPublished() == null){
            provider.getPublication().setPublished(XPATH+"//rss:item/dc:date/text()");
        }
        // Default URI xpath expression
        if (provider.getPublication().getUri() == null){
            provider.getPublication().setUri(XPATH+"//rss:item/rss:link/text()");
        }
        // Default URL xpath expression
        if (provider.getPublication().getUrl() == null){
            provider.getPublication().setUrl(XPATH+"//rss:item/rss:link/text()");
        }
        // Default Language xpath expression
        if (provider.getPublication().getLanguage() == null){
            provider.getPublication().setLanguage(XPATH+"//rss:channel/dc:language/text()");
        }
        // Default Rights xpath expression
        if (provider.getPublication().getRights() == null){
            provider.getPublication().setRights(XPATH+"//rss:channel/dc:rights/text()");
        }
        // Default Creators xpath expression
        if (provider.getPublication().getCreators() == null){
            provider.getPublication().setCreators(XPATH+"string-join(//rss:channel/dc:creator/text(),\";\")");
        }
        // Default constant Format
        if (provider.getPublication().getFormat() == null){
            provider.getPublication().setFormat("htm");
        }
        provider.validate();
        this.provider = provider;
        LOG.debug("RSS Provider: {}",provider);
    }

    @Override
    public void create(RouteBuilder builder) {
        // http://camel.apache.org/rss.html
        StringBuilder route =  new StringBuilder().append("rss:").append(provider.getUrl()).
                append("?splitEntries=true&").
                append("consumer.delay=").append(provider.getDelay()).
                append("&consumer.initialDelay=").append(provider.getInitialDelay()).
                append("&feedHeader=false&filter=true");
        if (provider.getFrom() != null){
            route.append("&lastUpdate=").append(provider.getFrom());
        }

        ProcessorDefinition<RouteDefinition> definition = builder.from(route.toString()).marshal().rss();
        addProperties(builder, definition, provider);
    }

}
