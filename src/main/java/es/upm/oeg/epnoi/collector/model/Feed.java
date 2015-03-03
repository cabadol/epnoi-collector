package es.upm.oeg.epnoi.collector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import java.util.ArrayList;
import java.util.List;

@ToString
@EqualsAndHashCode
public class Feed implements Resource{

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String link;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String language;

    @Getter
    @Setter
    private String copyright;

    @Getter
    @Setter
    private String pubDate;

    @Getter
    @Setter
    private String URI;

    @Getter
    @Setter
    private List<Item> items = new ArrayList<Item>();


    public Feed(String title, String link, String description, String language,
                String copyright, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        System.out.println("creation pubDate>  "+pubDate);
        this.pubDate = pubDate;

    }

}
