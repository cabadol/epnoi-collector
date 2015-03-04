package es.upm.oeg.epnoi.collector.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
@ToString
public class Context {


    @Getter
    private List<Publication> publications = new ArrayList<Publication>();

    @Getter
    @Setter
    private Source source;

    public void add(Publication publication){
        publications.add(publication);
    }

}
