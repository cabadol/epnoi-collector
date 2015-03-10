package es.upm.oeg.epnoi.collector;

import com.google.gson.Gson;
import es.upm.oeg.epnoi.collector.processor.*;
import es.upm.oeg.epnoi.collector.routes.RouteBuilderFactory;
import es.upm.oeg.epnoi.collector.model.Configuration;
import es.upm.oeg.epnoi.collector.model.Provider;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

@Component
public class CollectorRouteBuilder extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(CollectorRouteBuilder.class);

    public static final String INBOX_ROUTE = "seda:inbox";

    public static final String PROCESS_ROUTE = "seda:processing";

    public static final String DELETED_ROUTE = "seda:deleted";

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    TimeClock timeClock;

    @Autowired
    ContextBuilder contextBuilder;

    @Autowired
    RemoveHandler removeHandler;

    @Autowired
    UUIDGenerator uuidGenerator;

    @Value("${camel.config.location}")
    File configurationFile;

    @Value("${storage.path}")
    String basedir;

    @Value("${uia.service.host}")
    String uiaServers;

    @Value("${uia.service.notification}")
    Boolean uiaNotificate;

    @Value("${camel.log.interval}")
    String logInterval;

    @Value("${camel.log.delay}")
    String logDelay;

    @Override
    public void configure() throws Exception {


        onException(MalformedURLException.class)
                .process(errorHandler).stop();

        onException(IOException.class)
                .maximumRedeliveries(3)
                .process(errorHandler).stop();

        Gson gson = new Gson();
        Configuration configuration = gson.fromJson(new FileReader(configurationFile), Configuration.class);
        log.info("Initializing camel context for Epnoi Collector from config file: {} with settings: {}", configurationFile.getAbsolutePath(), configuration);

        /************************************************************************************************************
         * Data Provider routes
         ************************************************************************************************************/

        for(Provider provider: configuration.getProviders()){
            // Create Camel Route
            RouteBuilderFactory.newBuilder(provider).create(this);
        }

        /************************************************************************************************************
         * Internal routes
         ************************************************************************************************************/

        from(INBOX_ROUTE).
                // Set collection timestamp
                process(timeClock).
                // Set UUID
                process(uuidGenerator).
                // Save xml file
                setProperty(CollectorProperty.PUBLICATION_REFERENCE_URL,
                        simple("${property." + CollectorProperty.SOURCE_PROTOCOL + "}/" +
                                "${property." + CollectorProperty.SOURCE_NAME + "}/" +
                                "${property." + CollectorProperty.PUBLICATION_PUBLISHED_DATE + "}/" +
                                "resource-${property." + CollectorProperty.PUBLICATION_UUID + "}.${property." + CollectorProperty.PUBLICATION_REFERENCE_FORMAT + "}")).
                to("file:"+basedir+"/?fileName=${property." + CollectorProperty.PUBLICATION_REFERENCE_URL + "}").
                // Filter resources with available url
                filter(property(CollectorProperty.PUBLICATION_URL_REMOTE).isNotEqualTo("")).
                // Retrieve publication using Http Component
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI,    simple("${property." + CollectorProperty.PUBLICATION_URL_REMOTE + "}")).
                to("http://dummyhost?throwExceptionOnFailure=false").
                setProperty(CollectorProperty.PUBLICATION_URL_LOCAL,
                        simple("${property." + CollectorProperty.SOURCE_PROTOCOL + "}/" +
                                "${property." + CollectorProperty.SOURCE_NAME + "}/" +
                                "${property." + CollectorProperty.PUBLICATION_PUBLISHED_DATE + "}/" +
                                "resource-${property." + CollectorProperty.PUBLICATION_UUID + "}.${property." + CollectorProperty.PUBLICATION_FORMAT + "}")).
                // Save publication file
                to("file:" + basedir + "/?fileName=${property." + CollectorProperty.PUBLICATION_URL_LOCAL + "}").
                to(PROCESS_ROUTE);

        String target = uiaNotificate? "euia:out?servers="+ uiaServers :
                "log:es.upm.oeg.epnoi.collector?level=INFO&groupInterval="+logInterval+"&groupDelay="+logDelay+"&groupActiveOnly=false";

        // Create a context message for Epnoi UIA
        from(PROCESS_ROUTE).
                process(contextBuilder).
                to(target);

        from(DELETED_ROUTE).
                process(removeHandler);

    }
}
