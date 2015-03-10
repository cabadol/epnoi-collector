package es.upm.oeg.epnoi.collector.source;


import es.upm.oeg.epnoi.collector.CollectorProperty;
import lombok.Data;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class ValidateExpressions implements Processor{

    private static final Logger LOG = LoggerFactory.getLogger(ValidateExpressions.class);

    @Data
    public static class Report{

        Boolean isEmpty  = false;
        String value    = "<empty>";

        public String toString(){
            return new StringBuilder().append("[").append(isEmpty).
                    append("|").append(value).append("]").toString();
        }
    }

    private static ConcurrentHashMap<String,Report> report = new ConcurrentHashMap<>();
    private static String source;

    static{
        report.put(CollectorProperty.PUBLICATION_TITLE, new Report());
        report.put(CollectorProperty.PUBLICATION_DESCRIPTION, new Report());
        report.put(CollectorProperty.PUBLICATION_PUBLISHED, new Report());
        report.put(CollectorProperty.PUBLICATION_URI, new Report());
        report.put(CollectorProperty.PUBLICATION_URL_REMOTE, new Report());
        report.put(CollectorProperty.PUBLICATION_LANGUAGE, new Report());
        report.put(CollectorProperty.PUBLICATION_RIGHTS, new Report());
        report.put(CollectorProperty.PUBLICATION_CREATORS, new Report());
        report.put(CollectorProperty.PUBLICATION_FORMAT, new Report());
    }


    @Override
    public void process(Exchange exchange) throws Exception {
        this.source = exchange.getProperty(CollectorProperty.SOURCE_NAME,String.class);
        Enumeration<String> keys = report.keys();

        while(keys.hasMoreElements()){
            String key = keys.nextElement();
            String value = exchange.getProperty(key, String.class);
            if (value == null || value.trim().equals("")){
                report.get(key).setIsEmpty(true);
            }else{
                report.get(key).setValue(value);
            }
        }
    }


    public void report(){

        Enumeration<String> keys = report.keys();
        LOG.info("SOURCE: "+ source);
        while(keys.hasMoreElements()){
            String key = keys.nextElement();
            LOG.info(key + " -> " + report.get(key));
        }
    }
}
