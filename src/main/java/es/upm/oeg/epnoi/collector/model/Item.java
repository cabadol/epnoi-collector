package es.upm.oeg.epnoi.collector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class Item implements Resource {

    public class ID {
        public final static String URI = "Item.URI";
    }

    @Getter
    @Setter
    private String title = "";

    @Getter
    @Setter
    private String description = "";

    @Getter
    @Setter
    private String link;

    @Getter
    @Setter
    private String author = "";

    @Getter
    @Setter
    private String guid = "";

    @Getter
    @Setter
    private String URI;

    @Getter
    @Setter
    private String pubDate = "";

    @Getter
    @Setter
    private String content = "";

}