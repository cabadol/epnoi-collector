package es.upm.oeg.epnoi.collector;

import com.google.gson.Gson;
import es.upm.oeg.epnoi.collector.routes.RouteBuilderFactory;
import es.upm.oeg.epnoi.collector.model.Configuration;
import es.upm.oeg.epnoi.collector.model.Provider;
import es.upm.oeg.epnoi.collector.processor.ContextBuilder;
import es.upm.oeg.epnoi.collector.processor.ErrorHandler;
import es.upm.oeg.epnoi.collector.processor.TimeClock;
import es.upm.oeg.epnoi.collector.processor.UUIDGenerator;
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

    @Autowired
    ErrorHandler errorHandler;

    @Autowired
    TimeClock timeClock;

    @Autowired
    ContextBuilder contextBuilder;

    @Autowired
    UUIDGenerator uuidGenerator;

    @Value("${camel.config.location}")
    File configurationFile;

    @Value("${storage.path}")
    String basedir;

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
                setProperty(Header.PUBLICATION_REFERENCE_URL,
                        simple("${property." + Header.SOURCE_PROTOCOL + "}/" +
                                "${property." + Header.SOURCE_NAME + "}/" +
                                "${property." + Header.PUBLICATION_PUBLISHED_DATE + "}/" +
                                "resource-${property." + Header.PUBLICATION_UUID + "}.${property." + Header.PUBLICATION_REFERENCE_FORMAT + "}")).
                to("file:"+basedir+"/?fileName=${property." + Header.PUBLICATION_REFERENCE_URL + "}").
                // Retrieve publication using Http Component
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI,    simple("${property." + Header.PUBLICATION_URL_REMOTE + "}")).
                to("http://dummyhost?throwExceptionOnFailure=false").
                setProperty(Header.PUBLICATION_URL_LOCAL,
                        simple("${property." + Header.SOURCE_PROTOCOL + "}/" +
                                "${property." + Header.SOURCE_NAME + "}/" +
                                "${property." + Header.PUBLICATION_PUBLISHED_DATE + "}/" +
                                "resource-${property." + Header.PUBLICATION_UUID + "}.${property." + Header.PUBLICATION_FORMAT + "}")).
                // Save publication file
                to("file:" + basedir + "/?fileName=${property." + Header.PUBLICATION_URL_LOCAL + "}").
                to("seda:processing");

        from("seda:processing").
                // Create a context message for Epnoi UIA
                process(contextBuilder).
                to("euia:out");

    }
}
