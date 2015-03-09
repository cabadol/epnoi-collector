package es.upm.oeg.epnoi.collector.routes;

import es.upm.oeg.epnoi.collector.CollectorRouteBuilder;
import es.upm.oeg.epnoi.collector.model.Provider;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.model.RouteDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAIPMHRouteBuilder extends AbstractRouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(OAIPMHRouteBuilder.class);

    public static final String PROTOCOL = "oaipmh";

    private final Provider provider;

    public OAIPMHRouteBuilder(Provider provider){
        // default Delay value
        if (provider.getDelay() == null){
            provider.setDelay(60000);
        }
        // default Initial Delay value
        if (provider.getInitialDelay() == null){
            provider.setInitialDelay(1000);
        }
        // Default Title xpath expression
        if (provider.getPublication().getTitle() == null){
            provider.getPublication().setTitle(XPATH+"//oai:metadata/oai:dc/dc:title/text()");
        }
        // Default Description xpath expression
        if (provider.getPublication().getDescription() == null){
            provider.getPublication().setDescription(XPATH+"//oai:metadata/oai:dc/dc:description/text()");
        }
        // Default Published xpath expression
        if (provider.getPublication().getPublished() == null){
            provider.getPublication().setPublished(XPATH+"//oai:header/oai:datestamp/text()");
        }
        // Default URI xpath expression
        if (provider.getPublication().getUri() == null){
            provider.getPublication().setUri(XPATH+"//oai:header/oai:identifier/text()");
        }
        // Default URL xpath expression
        if (provider.getPublication().getUrl() == null){
            provider.getPublication().setUrl(XPATH+"//oai:metadata/oai:dc/dc:identifier/text()");
        }
        // Default Language xpath expression
        if (provider.getPublication().getLanguage() == null){
            provider.getPublication().setLanguage(XPATH+"//oai:metadata/oai:dc/dc:language/text()");
        }
        // Default Rights xpath expression
        if (provider.getPublication().getRights() == null){
            provider.getPublication().setRights(XPATH+"//oai:metadata/oai:dc/dc:rights/text()");
        }
        // Default Creators xpath expression
        if (provider.getPublication().getCreators() == null){
            provider.getPublication().setCreators(XPATH+"string-join(//oai:metadata/oai:dc/dc:creator/text(),\";\")");
        }
        // Default Format
        if (provider.getPublication().getFormat() == null){
            provider.getPublication().setFormat("pdf");
        }
        // default From value
        if (provider.getFrom() == null){
            provider.setFrom("1970-01-01T00:00:00Z");
        }
        provider.validate();
        this.provider = provider;
        LOG.debug("OAIPMH Provider: {}",provider);
    }

    @Override
    public void create(RouteBuilder builder) {

        // Create OAI-PMH URI https://github.com/cabadol/camel-oaipmh
        StringBuilder route =  new StringBuilder().append(provider.getUrl().replace("http", "oaipmh")).
                append("?delay=").append(provider.getDelay()).
                append("&initialDelay=").append(provider.getInitialDelay());

        // Add attributes
        if (provider.getFrom() != null){
            route.append("&from=").append(provider.getFrom());
        }

        // Initialize route definition
        ProcessorDefinition<RouteDefinition> definition = builder.from(route.toString());

        // Add xpath and constant properties
        addProperties(builder, definition, provider);


        // Filter 'deleted' resources
        definition = (ProcessorDefinition<RouteDefinition>) definition.
                choice().
                    when().xpath("//oai:header[@status=\"deleted\"]", String.class, ns).to(CollectorRouteBuilder.DELETED_ROUTE).stop().
                end();

        // Send to next stage
        nextStage(definition);
    }
}
