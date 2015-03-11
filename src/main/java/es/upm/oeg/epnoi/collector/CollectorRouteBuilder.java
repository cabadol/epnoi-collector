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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;

public class CollectorRouteBuilder extends RouteBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(CollectorRouteBuilder.class);

    public static final String INBOX_ROUTE = "seda:inbox";

    public static final String UIA = "seda:processing";

    public static final String DELETED_ROUTE = "seda:deleted";

    public static final String SOURCE_NAME              = CollectorProperty.SOURCE_NAME;
    public static final String SOURCE_URI               = CollectorProperty.SOURCE_URI;
    public static final String SOURCE_URL               = CollectorProperty.SOURCE_URL;
    public static final String SOURCE_PROTOCOL          = CollectorProperty.SOURCE_PROTOCOL;

    public static final String PUBLICATION_TITLE        = CollectorProperty.PUBLICATION_TITLE;
    public static final String PUBLICATION_DESCRIPTION  = CollectorProperty.PUBLICATION_DESCRIPTION;
    public static final String PUBLICATION_PUBLISHED    = CollectorProperty.PUBLICATION_PUBLISHED;
    public static final String PUBLICATION_URI          = CollectorProperty.PUBLICATION_URI;
    public static final String PUBLICATION_URL          = CollectorProperty.PUBLICATION_URL_REMOTE;
    public static final String PUBLICATION_LANGUAGE     = CollectorProperty.PUBLICATION_LANGUAGE;
    public static final String PUBLICATION_RIGHTS       = CollectorProperty.PUBLICATION_RIGHTS;
    public static final String PUBLICATION_CREATORS     = CollectorProperty.PUBLICATION_CREATORS;
    public static final String PUBLICATION_UUID         = CollectorProperty.PUBLICATION_UUID;
    public static final String PUBLICATION_PUBLISHED_DATE = CollectorProperty.PUBLICATION_PUBLISHED_DATE;
    public static final String PUBLICATION_FORMAT       = CollectorProperty.PUBLICATION_FORMAT;
    public static final String PUBLICATION_METADATA_FORMAT       = CollectorProperty.PUBLICATION_METADATA_FORMAT;


    public static final String ARGUMENT_NAME            = CollectorProperty.ARGUMENT_NAME;
    public static final String ARGUMENT_PATH            = CollectorProperty.ARGUMENT_PATH;



    @Autowired
    protected ErrorHandler errorHandler;

    @Autowired
    protected TimeClock timeClock;

    @Autowired
    protected ContextBuilder contextBuilder;

    @Autowired
    protected RemoveHandler removeHandler;

    @Autowired
    protected UUIDGenerator uuidGenerator;

    @Value("${camel.config.location}")
    protected File configurationFile;

    @Value("${storage.path}")
    protected String basedir;

    @Value("${uia.service.host}")
    protected String uiaServers;

    @Value("${uia.service.notification}")
    protected Boolean uiaNotificate;

    @Value("${camel.log.interval}")
    protected String logInterval;

    @Value("${camel.log.delay}")
    protected String logDelay;

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
                                "resource-${property." + CollectorProperty.PUBLICATION_UUID + "}.${property." + CollectorProperty.PUBLICATION_METADATA_FORMAT + "}")).
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
                to(UIA);

        String target = uiaNotificate? "euia:out?servers="+ uiaServers :
                "log:es.upm.oeg.epnoi.collector?level=INFO&groupInterval="+logInterval+"&groupDelay="+logDelay+"&groupActiveOnly=false";

        // Create a context message for Epnoi UIA
        from(UIA).
                process(contextBuilder).
                to(target);

        from(DELETED_ROUTE).
                process(removeHandler);

    }


//    protected String getProperty(String key){
//        return property(key).toString();
//    }
//
//    protected String getHeader(String key){
//        return header(key).toString();
//    }
}
