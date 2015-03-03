package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.model.Header;
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


    Logger log = LoggerFactory.getLogger(DateStamp.class);

    DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    @Override
    public void process(Exchange exchange) throws Exception {
        Date date = new Date();

        exchange.getIn().setHeader(Header.TIME_MILLIS, date.getTime());
        log.debug("Added Header property: {}={}", Header.TIME_MILLIS, date.getTime());

        exchange.getIn().setHeader(Header.TIME_DATE, simpleDateFormat.format(date));
        log.debug("Added Header property: {}={}",Header.TIME_DATE, simpleDateFormat.format(date));
    }
}
