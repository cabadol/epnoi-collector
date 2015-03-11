package es.upm.oeg.epnoi.collector;

import groovy.lang.GroovyClassLoader;
import org.apache.camel.spring.SpringCamelContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.File;


//@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan({ "routes", "es.upm.oeg.epnoi.collector" })
public class Application {

    @Value("${camel.config.groovy}")
    File groovyFile;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public SpringCamelContext camelContext(ApplicationContext applicationContext) throws Exception {

        // Loading groovy class
        GroovyClassLoader gcl = new GroovyClassLoader();
        Class clazz = gcl.parseClass(groovyFile);
        CollectorRouteBuilder routeBuilder = (CollectorRouteBuilder) clazz.newInstance();



        SpringCamelContext camelContext = new SpringCamelContext(applicationContext);
        camelContext.addRoutes(routeBuilder);
        return camelContext;
    }



}
