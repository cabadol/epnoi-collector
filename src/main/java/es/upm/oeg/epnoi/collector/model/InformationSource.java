package es.upm.oeg.epnoi.collector.model;

import lombok.Data;

@Data
public class InformationSource implements Resource {

    String URI;

    String URL;

    String name;

    String type;

    String informationUnitType;

}
