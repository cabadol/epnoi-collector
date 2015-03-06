package es.upm.oeg.epnoi.collector.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Configuration {

    List<Provider> providers = new ArrayList<Provider>();

    public Configuration add(Provider provider){
        providers.add(provider);
        return this;
    }

}
