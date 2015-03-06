package es.upm.oeg.epnoi.collector.config;

import es.upm.oeg.epnoi.collector.model.Provider;
import org.apache.camel.builder.RouteBuilder;

public interface IRouteBuilder {

    void create(RouteBuilder builder);
}
