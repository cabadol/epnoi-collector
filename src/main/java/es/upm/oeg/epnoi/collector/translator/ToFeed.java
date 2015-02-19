package es.upm.oeg.epnoi.collector.translator;

import es.upm.oeg.epnoi.collector.model.Feed;
import es.upm.oeg.epnoi.collector.model.Item;
import es.upm.oeg.epnoi.collector.processor.DateStamp;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class ToFeed implements Processor {

    static final String TITLE = "title";
    static final String DESCRIPTION = "description";
    static final String CHANNEL = "channel";
    static final String LANGUAGE = "language";
    static final String COPYRIGHT = "copyright";
    static final String LINK = "link";
    static final String AUTHOR = "author";
    static final String ITEM = "item";
    static final String PUB_DATE = "pubDate";
    static final String GUID = "guid";

    Logger log = LoggerFactory.getLogger(ToFeed.class);

    @Override
    public void process(Exchange exchange) throws Exception {

        String body = exchange.getIn().getBody(String.class);

        log.trace("ready to parse feed: {}", body);
        Feed feed = parse(body);
        log.debug("Feed: {}", feed);

        if (feed.getPubDate() == "") {
            String date = exchange.getIn().getHeader(DateStamp.DATE, String.class);
            feed.setPubDate(date);
            log.debug("PubDate updated to: {}", date);
        }
        String uri = exchange.getIn().getHeader(Feed.ID.URI, String.class);
        feed.setURI(uri);
        log.debug("URI updated to: {}", uri);

        exchange.getIn().setBody(feed, Feed.class);
    }

    private Feed parse(String input) {
        Feed feed = null;
        try {
            boolean isFeedHeader = true;
            // Set header values intial to the empty string
            String description = "";
            String title = "";
            String link = "";
            String language = "";
            String copyright = "";
            String author = "";
            String pubdate = "";
            String guid = "";

            // First create a new XMLInputFactory
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            // Setup a new eventReader
            InputStream in = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document
            while (eventReader.hasNext()) {
                XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()) {
                    // Start
                    // element--------------------------------------------------------------------
                    String localPart = event.asStartElement().getName()
                            .getLocalPart();
                    if (ITEM.equals(localPart)) {

                        if (isFeedHeader) {
                            isFeedHeader = false;

                            feed = new Feed(title, link, description, language,
                                    copyright, pubdate);
                        }
                        event = eventReader.nextEvent();
                    } else if (TITLE.equals(localPart)) {

                        title = getCharacterData(event, eventReader);

                    } else if (DESCRIPTION.equals(localPart)) {
                        description = getCharacterData(event, eventReader);
                    } else if (LINK.equals(event.asStartElement().getName()
                            .toString())) {
                        link = getCharacterData(event, eventReader);

                    } else if (GUID.equals(localPart)) {
                        guid = getCharacterData(event, eventReader);
                    } else if (LANGUAGE.equals(localPart)) {
                        language = getCharacterData(event, eventReader);
                    } else if (AUTHOR.equals(localPart)) {
                        author = getCharacterData(event, eventReader);
                    } else if (PUB_DATE.equals(localPart)) {

                        pubdate = getCharacterData(event, eventReader);
                    } else if (COPYRIGHT.equals(localPart)) {
                        copyright = getCharacterData(event, eventReader);
                    }

                } else if (event.isEndElement()) {
                    // End
                    // element--------------------------------------------------------------------
                    if (event.asEndElement().getName().getLocalPart() == (ITEM)) {
                        Item message = new Item();
                        message.setAuthor(author);
                        message.setDescription(description);
                        message.setGuid(guid);
                        message.setLink(link);
                        message.setURI(link);
                        message.setTitle(title);

                        message.setPubDate(pubdate);
                        feed.getItems().add(message);
                        event = eventReader.nextEvent();
                        continue;
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
        return feed;
    }

    private String getCharacterData(XMLEvent event, XMLEventReader eventReader)
            throws XMLStreamException {
        String result = "";
        event = eventReader.nextEvent();
        if (event instanceof Characters) {
            result = event.asCharacters().getData();
        }
        return result;
    }
}
