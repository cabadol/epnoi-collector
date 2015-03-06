package es.upm.oeg.epnoi.collector.model;

import com.google.common.base.Joiner;
import lombok.Data;

import java.net.URI;

@Data
public class Provider {

    @Data
    public static class Publication {
        private String title;
        private String description;
        private String published;
        private String uri;
        private String url;
        private String language;
        private String rights;
        private String creators;
        private String format;
    }

    private String protocol;
    private String url;
    private String uri;
    private String name;
    private String from ;
    private Integer delay;
    private Integer initialDelay;
    private Publication publication;


    public Provider(){
        this.publication = new Publication();
    }

    public void validate() {
        if ((this.protocol == null) || (this.url == null)){
            throw new RuntimeException("Protocol or Url is null");
        }
        this.protocol = this.protocol.toLowerCase();
        if (this.name == null){
            this.name   = URI.create(url).getHost();
        }
        if (this.uri == null){
            this.uri    = Joiner.on("/").join("http://www.epnoi.org",protocol,name);
        }
    }

}
