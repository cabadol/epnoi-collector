package es.upm.oeg.epnoi.collector.time;

import es.upm.oeg.epnoi.collector.Header;
import es.upm.oeg.epnoi.collector.processor.TimeClock;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class ParserTest extends CamelTestSupport {

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void validTime() throws Exception {
        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_PUBLISHED_DATE,"2015-3-3");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_PUBLISHED_MILLIS,"1425389934000");

        template.sendBodyAndHeader("message", Header.PUBLICATION_PUBLISHED, "2015-03-03T13:38:54Z");

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                TimeClock timeClock = new TimeClock();

                from("direct:start")
                        .process(timeClock)
                        .to("mock:result");
            }
        };
    }

}