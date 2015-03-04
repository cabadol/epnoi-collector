package es.upm.oeg.epnoi.collector.xpath;

import es.upm.oeg.epnoi.collector.Header;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class RSSTest extends CamelTestSupport{

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    public void rssMessage() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<rss xmlns:dc=\"http://purl.org/dc/elements/1.1/\" version=\"2.0\">\n" +
                "  <channel>\n" +
                "    <title>Slashdot</title>\n" +
                "    <link>http://slashdot.org/</link>\n" +
                "    <description>News for nerds, stuff that matters</description>\n" +
                "    <language>en-us</language>\n" +
                "    <copyright>Copyright 1997-2015, Dice. All Rights Reserved. Slashdot is a Dice Holdings, Inc. service</copyright>\n" +
                "    <pubDate>Wed, 18 Feb 2015 13:23:11 GMT</pubDate>\n" +
                "    <category>Technology</category>\n" +
                "    <dc:creator>help@slashdot.org</dc:creator>\n" +
                "    <dc:subject>Technology</dc:subject>\n" +
                "    <dc:publisher>Dice</dc:publisher>\n" +
                "    <dc:date>2015-02-18T13:23:11Z</dc:date>\n" +
                "    <dc:language>en-us</dc:language>\n" +
                "    <dc:rights>Copyright 1997-2015, Dice. All Rights Reserved. Slashdot is a Dice Holdings, Inc. service</dc:rights>\n" +
                "    <image>\n" +
                "      <title>Slashdot</title>\n" +
                "      <url>http://a.fsdn.com/sd/topics/topicslashdot.gif</url>\n" +
                "      <link>http://slashdot.org/</link>\n" +
                "    </image>\n" +
                "    <item>\n" +
                "      <title>Interviews: Ask Stephen Wolfram a Question</title>\n" +
                "      <link>http://rss.slashdot.org/~r/Slashdot/slashdot/~3/L06b4tszG08/story01.htm</link>\n" +
                "      <slash:comments xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\">3</slash:comments>\n" +
                "      <slash:department xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\">go-ahead-and-ask</slash:department>\n" +
                "      <slash:section xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\">features</slash:section>\n" +
                "      <slash:hit_parade xmlns:slash=\"http://purl.org/rss/1.0/modules/slash/\">3,3,2,2,0,0,0</slash:hit_parade>\n" +
                "      <feedburner:origLink xmlns:feedburner=\"http://rssnamespace.org/feedburner/ext/1.0\">http://slashdot.feedsportal.com/c/35028/f/647410/s/437dfbbb/sc/1/l/0Lfeatures0Bslashdot0Borg0Cstory0C150C0A20C170C18382390Cinterviews0Eask0Estephen0Ewolfram0Ea0Equestion0Dutm0Isource0Frss10B0Amainlinkanon0Gutm0Imedium0Ffeed/story01.htm</feedburner:origLink>\n" +
                "      <description>Stephen Wolfram's accomplishments and contributions </description>\n" +
                "      <pubDate>Tue, 17 Feb 2015 19:23:00 GMT</pubDate>\n" +
                "      <guid isPermaLink=\"false\">http://slashdot.feedsportal.com/c/35028/f/647410/s/437dfbbb/sc/1/l/0Lfeatures0Bslashdot0Borg0Cstory0C150C0A20C170C18382390Cinterviews0Eask0Estephen0Ewolfram0Ea0Equestion0Dutm0Isource0Frss10B0Amainlinkanon0Gutm0Imedium0Ffeed/story01.htm</guid>\n" +
                "      <dc:creator>samzenpus</dc:creator>\n" +
                "      <dc:subject>math</dc:subject>\n" +
                "      <dc:date>2015-02-17T19:23:00Z</dc:date>\n" +
                "    </item>\n" +
                "  </channel>\n" +
                "</rss>\n" +
                "\n";

        resultEndpoint.expectedMessageCount(1);
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_TITLE,"Interviews: Ask Stephen Wolfram a Question");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_DESCRIPTION,"Stephen Wolfram's accomplishments and contributions ");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_PUBLISHED,"2015-02-17T19:23:00Z");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_URI,"http://rss.slashdot.org/~r/Slashdot/slashdot/~3/L06b4tszG08/story01.htm");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_URL_REMOTE,"http://rss.slashdot.org/~r/Slashdot/slashdot/~3/L06b4tszG08/story01.htm");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_LANGUAGE,"en-us");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_RIGHTS,"Copyright 1997-2015, Dice. All Rights Reserved. Slashdot is a Dice Holdings, Inc. service");
        resultEndpoint.expectedHeaderReceived(Header.PUBLICATION_CREATORS,"samzenpus");


        template.sendBody(xml);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                Namespaces ns = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/");
                ns.add("dc", "http://purl.org/dc/elements/1.1/");
                ns.add("provenance", "http://www.openarchives.org/OAI/2.0/provenance");
                ns.add("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");

                from("direct:start").
                        setHeader(Header.SOURCE_NAME,                   constant("slashdot")).
                        setHeader(Header.SOURCE_URI,                    constant("http://www.epnoi.org/feeds/slashdot")).
                        setHeader(Header.SOURCE_URL,                    constant("http://rss.slashdot.org/Slashdot/slashdot")).
                        setHeader(Header.SOURCE_PROTOCOL,               constant("rss")).
                        setHeader(Header.PUBLICATION_TITLE,             xpath("//item/title/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_DESCRIPTION,       xpath("//item/description/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_PUBLISHED,         xpath("//item/dc:date/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_URI,               xpath("//item/link/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_URL_REMOTE,        xpath("//item/link/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_LANGUAGE,          xpath("//channel/dc:language/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_RIGHTS,            xpath("//channel/dc:rights/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_CREATORS,          xpath("//item/dc:creator/text()", String.class).namespaces(ns)).
                        setHeader(Header.PUBLICATION_FORMAT,            constant("htm")).
                        setHeader(Header.PUBLICATION_REFERENCE_FORMAT,  constant("xml")).
                        to("mock:result");
            }
        };
    }

}
