package es.upm.oeg.epnoi.collector.model;

import lombok.Data;

import java.util.List;

@Data
public class Publication {

    private String title;
    private String description;
    private String published;
    private String uri;
    private String url;
    private String language;
    private String rights;
    private List<String> creators;
    private String format;
    private Reference reference;
}
