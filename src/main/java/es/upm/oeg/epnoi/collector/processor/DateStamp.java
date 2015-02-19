package es.upm.oeg.epnoi.collector.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by cbadenes on 18/02/15.
 */
@Component
public class DateStamp implements Processor{

    public static final String TIME_MILLIS  = "time";
    public static final String DATE         = "date";

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    public void process(Exchange exchange) throws Exception {
        Date date = new Date();
        exchange.getIn().setHeader(TIME_MILLIS, date.getTime());
        exchange.getIn().setHeader(DATE, simpleDateFormat.format(date));
    }
}
