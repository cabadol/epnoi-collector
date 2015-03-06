package es.upm.oeg.epnoi.collector;

import com.google.common.base.Joiner;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Header {

    private static Joiner joiner                                = Joiner.on(".");
    private static final String EPNOI                           = "epnoi";

    // Time
    public static final String TIME                             = joiner.join(EPNOI,"time");

    // Source
    private static final String SOURCE                          = joiner.join(EPNOI,"source");
    public static final String SOURCE_NAME                      = joiner.join(SOURCE,"name");
    public static final String SOURCE_URI                       = joiner.join(SOURCE, "uri");
    public static final String SOURCE_URL                       = joiner.join(SOURCE, "url");
    public static final String SOURCE_PROTOCOL                  = joiner.join(SOURCE,"protocol");

    // Publication
    private static final String PUBLICATION                     = joiner.join(EPNOI,"publication");
    public static final String PUBLICATION_UUID                = joiner.join(PUBLICATION,"uuid");
    public static final String PUBLICATION_TITLE                = joiner.join(PUBLICATION,"title");
    public static final String PUBLICATION_DESCRIPTION          = joiner.join(PUBLICATION,"description");
    public static final String PUBLICATION_PUBLISHED            = joiner.join(PUBLICATION,"published");
    public static final String PUBLICATION_PUBLISHED_DATE       = joiner.join(PUBLICATION_PUBLISHED,"date");
    public static final String PUBLICATION_PUBLISHED_MILLIS     = joiner.join(PUBLICATION_PUBLISHED,"millis");
    public static final String PUBLICATION_URI                  = joiner.join(PUBLICATION,"uri");
    public static final String PUBLICATION_LANGUAGE             = joiner.join(PUBLICATION,"lang");
    public static final String PUBLICATION_RIGHTS               = joiner.join(PUBLICATION,"rights");
    public static final String PUBLICATION_COPYRIGHT            = joiner.join(PUBLICATION,"copyright");
    public static final String PUBLICATION_FORMAT               = joiner.join(PUBLICATION,"format");
    //  -> urls
    private static final String PUBLICATION_URL                 = joiner.join(PUBLICATION,"url");
    public static final String PUBLICATION_URL_LOCAL            = joiner.join(PUBLICATION_URL,"local");
    public static final String PUBLICATION_URL_REMOTE           = joiner.join(PUBLICATION_URL,"remote");
    //  -> reference
    private static final String PUBLICATION_REFERENCE           = joiner.join(PUBLICATION,"reference");
    public static final String PUBLICATION_REFERENCE_FORMAT     = joiner.join(PUBLICATION_REFERENCE,"format");
    public static final String PUBLICATION_REFERENCE_URL        = joiner.join(PUBLICATION_REFERENCE,"url");
    //  -> creators
    public static final String PUBLICATION_CREATORS             = joiner.join(PUBLICATION,"creators"); //CSV

}
