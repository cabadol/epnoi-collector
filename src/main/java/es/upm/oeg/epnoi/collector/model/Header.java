package es.upm.oeg.epnoi.collector.model;

import com.google.common.base.Joiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Header {

    private static Joiner joiner                                = Joiner.on(".");
    private static final String EPNOI                           = "EPNOI";

    private static final String TIME                            = joiner.join(EPNOI,"TIME");
    public static final String TIME_DATE                        = joiner.join(TIME,"DATE");
    public static final String TIME_MILLIS                      = joiner.join(TIME,"MILLIS");

    private static final String PROVIDER                        = joiner.join(EPNOI,"PROVIDER");
    public static final String PROVIDER_NAME                    = joiner.join(PROVIDER,"NAME");
    public static final String PROVIDER_URI                     = joiner.join(PROVIDER, "URI");
    public static final String PROVIDER_URL                     = joiner.join(PROVIDER, "URL");
    public static final String PROVIDER_PROTOCOL                = joiner.join(PROVIDER,"PROTOCOL");

    private static final String RESOURCE                        = joiner.join(EPNOI,"RESOURCE");

    private static final String RESOURCE_DESCRIPTOR             = joiner.join(RESOURCE,"DESCRIPTOR");
    public static final String RESOURCE_DESCRIPTOR_FORMAT       = joiner.join(RESOURCE_DESCRIPTOR,"FORMAT");
    public static final String RESOURCE_DESCRIPTOR_LOCAL        = joiner.join(RESOURCE_DESCRIPTOR,"LOCAL");
    public static final String RESOURCE_DESCRIPTOR_LOCAL_PATH   = joiner.join(RESOURCE_DESCRIPTOR_LOCAL,"PATH");

    private static final String RESOURCE_CONTENT                = joiner.join(RESOURCE,"CONTENT");
    public static final String RESOURCE_CONTENT_FORMAT          = joiner.join(RESOURCE_CONTENT,"FORMAT");
    private static final String RESOURCE_CONTENT_REMOTE         = joiner.join(RESOURCE_CONTENT,"REMOTE");
    public static final String RESOURCE_CONTENT_REMOTE_PATH     = joiner.join(RESOURCE_CONTENT_REMOTE,"PATH");
    private static final String RESOURCE_CONTENT_LOCAL          = joiner.join(RESOURCE_CONTENT,"LOCAL");
    public static final String RESOURCE_CONTENT_LOCAL_PATH      = joiner.join(RESOURCE_CONTENT_REMOTE,"PATH");

}
