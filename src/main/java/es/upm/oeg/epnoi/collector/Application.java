package es.upm.oeg.epnoi.collector;

import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class Application {

    @Autowired
    CollectorRouteBuilder routeBuilder;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {
        SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
        camelContext.addRoutes(routeBuilder);
        return camelContext;
    }

}
