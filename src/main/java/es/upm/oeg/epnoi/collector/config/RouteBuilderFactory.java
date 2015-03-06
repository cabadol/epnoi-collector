package es.upm.oeg.epnoi.collector.config;


import es.upm.oeg.epnoi.collector.model.Provider;

public class RouteBuilderFactory {

    public static final IRouteBuilder newBuilder(Provider provider){
        switch(provider.getProtocol().toLowerCase()){
            case RSSRouteBuilder.PROTOCOL:
                return new RSSRouteBuilder(provider);
            case OAIPMHRouteBuilder.PROTOCOL:
                return new OAIPMHRouteBuilder(provider);
            default: throw new RuntimeException("Protocol not supported");
        }
    }

}
