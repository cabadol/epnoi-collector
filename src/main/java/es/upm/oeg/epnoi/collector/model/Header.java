package es.upm.oeg.epnoi.collector.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Header {

    public class PROVIDER {
        public static final String NAME     = "EPNOI.PROVIDER.NAME";
        public static final String URI      = "EPNOI.PROVIDER.URI";
        public static final String URL      = "EPNOI.PROVIDER.URL";
        public static final String PROTOCOL = "EPNOI.PROVIDER.PROTOCOL";
    }

    public class RESOURCE {
        public static final String PATH     = "EPNOI.RESOURCE.PATH";
        public static final String FORMAT   = "EPNOI.RESOURCE.FORMAT";
    }
}
