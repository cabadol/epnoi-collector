package routes

import es.upm.oeg.epnoi.collector.CollectorRouteBuilder
import org.springframework.stereotype.Component


@Component
class routes extends CollectorRouteBuilder{

    @Override
    public void configure() throws Exception {

        from("direct:start").to("mock:out")

    }

}
