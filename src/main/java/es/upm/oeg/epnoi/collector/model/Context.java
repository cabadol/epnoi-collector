package es.upm.oeg.epnoi.collector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
public class Context {

    static {
        emptyContext = new Context();
    }

    @Getter
    private static final Context emptyContext;

    public static final String ANNOTATED_CONTENT = "ANNOTATED_CONTENT";
    public static final String INFORMATION_SOURCE_URI = "INFORMATION_SOURCE_URI";
    public static final String INFORMATION_SOURCE_NAME = "INFORMATION_SOURCE_NAME";

    @Getter
    @Setter
    Map<String, String> parameters = new HashMap<String, String>();

    @Getter
    @Setter
    Map<String, Object> elements = new HashMap<String, Object>();


    public void clear() {
        this.elements.clear();
    }

}
