package es.upm.oeg.epnoi.collector.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Component
public class DateStamp implements Processor{

    public static final String TIME_MILLIS  = "time";
    public static final String DATE         = "date";

    Logger log = LoggerFactory.getLogger(DateStamp.class);

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    public void process(Exchange exchange) throws Exception {
        Date date = new Date();

        exchange.getIn().setHeader(TIME_MILLIS, date.getTime());
        log.debug("Added Header property: {}={}", TIME_MILLIS, date.getTime());

        exchange.getIn().setHeader(DATE, simpleDateFormat.format(date));
        log.debug("Added Header property: {}={}",DATE, simpleDateFormat.format(date));
    }
}
