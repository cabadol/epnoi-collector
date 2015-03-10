package es.upm.oeg.epnoi.collector.source;

import es.upm.oeg.epnoi.collector.CollectorRouteBuilder;
import es.upm.oeg.epnoi.collector.model.Provider;
import es.upm.oeg.epnoi.collector.routes.OAIPMHRouteBuilder;
import es.upm.oeg.epnoi.collector.routes.RouteBuilderFactory;
import es.upm.oeg.epnoi.collector.utils.FileServer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;


public class CheckSource extends CamelTestSupport{

    ValidateExpressions validateExpressions = new ValidateExpressions();

    @Test
    public void harvest() throws Exception {

        Thread.currentThread().sleep(5000);
        validateExpressions.report();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() throws URISyntaxException {



                List<String> urls = Arrays.asList(new String[]{"http://www.informatik.uni-stuttgart.de/cgi-bin/OAI/OAI.pl"});

                for(String url: urls){
                    // Create Camel Route
                    Provider provider = new Provider();
                    URI uri = new URI(url);
                    provider.setName(uri.getHost());
                    provider.setProtocol(OAIPMHRouteBuilder.PROTOCOL);
                    provider.setUrl(url);

                    RouteBuilderFactory.newBuilder(provider).create(this);
                }

                from(CollectorRouteBuilder.INBOX_ROUTE).
                        process(validateExpressions);



            }
        };
    }

}
