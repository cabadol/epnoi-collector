package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.model.Feed;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by cbadenes on 18/02/15.
 *
 * Tests for {@link RSSContentProcessor}
 */
public class RssProcessorTest extends CamelTestSupport{

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    @Ignore
    public void testSendMatchingMessage() throws Exception {
        String expectedBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
                "      <description>Stephen Wolfram's accomplishments and contributions to science and computing are numerous. He earned a PhD in particle physics from Caltech at 20, and has been cited by over 30,000 research publications. Wolfram is the the author of A New Kind of Science, creator of Mathematica, the creator of Wolfram Alpha, and the founder and CEO of Wolfram Research. He developed Wolfram Language, a general multi-paradigm programming language, in 2014. Stephen has graciously agreed to answer any questions you may have for him. As usual, ask as many as you'd like, but please, one per post.&lt;p&gt;&lt;div class=\"share_submission\" style=\"position:relative;\"&gt; &lt;a class=\"slashpop\" href=\"http://twitter.com/home?status=Interviews%3A+Ask+Stephen+Wolfram+a+Question%3A+http%3A%2F%2Fbit.ly%2F1ySFsaF\"&gt;&lt;img src=\"http://a.fsdn.com/sd/twitter_icon_large.png\"&gt;&lt;/a&gt; &lt;a class=\"slashpop\" href=\"http://www.facebook.com/sharer.php?u=http%3A%2F%2Ffeatures.slashdot.org%2Fstory%2F15%2F02%2F17%2F1838239%2Finterviews-ask-stephen-wolfram-a-question%3Futm_source%3Dslashdot%26utm_medium%3Dfacebook\"&gt;&lt;img src=\"http://a.fsdn.com/sd/facebook_icon_large.png\"&gt;&lt;/a&gt; &lt;a class=\"nobg\" href=\"http://plus.google.com/share?url=http://features.slashdot.org/story/15/02/17/1838239/interviews-ask-stephen-wolfram-a-question?utm_source=slashdot&amp;amp;utm_medium=googleplus\" onclick=\"javascript:window.open(this.href,'', 'menubar=no,toolbar=no,resizable=yes,scrollbars=yes,height=600,width=600');return false;\"&gt;&lt;img src=\"http://www.gstatic.com/images/icons/gplus-16.png\" alt=\"Share on Google+\"/&gt;&lt;/a&gt; &lt;/div&gt;&lt;/p&gt;&lt;p&gt;&lt;a href=\"http://features.slashdot.org/story/15/02/17/1838239/interviews-ask-stephen-wolfram-a-question?utm_source=rss1.0moreanon&amp;amp;utm_medium=feed\"&gt;Read more of this story&lt;/a&gt; at Slashdot.&lt;/p&gt;&lt;iframe src=\"http://slashdot.org/slashdot-it.pl?op=discuss&amp;amp;id=6981033&amp;amp;smallembed=1\" style=\"height: 300px; width: 100%; border: none;\"&gt;&lt;/iframe&gt;&lt;img width='1' height='1' src='http://slashdot.feedsportal.com/c/35028/f/647410/s/437dfbbb/sc/1/mf.gif' border='0'/&gt;&lt;br clear='all'/&gt;&lt;br/&gt;&lt;br/&gt;&lt;a href=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/rc/1/rc.htm\" rel=\"nofollow\"&gt;&lt;img src=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/rc/1/rc.img\" border=\"0\"/&gt;&lt;/a&gt;&lt;br/&gt;&lt;a href=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/rc/2/rc.htm\" rel=\"nofollow\"&gt;&lt;img src=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/rc/2/rc.img\" border=\"0\"/&gt;&lt;/a&gt;&lt;br/&gt;&lt;a href=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/rc/3/rc.htm\" rel=\"nofollow\"&gt;&lt;img src=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/rc/3/rc.img\" border=\"0\"/&gt;&lt;/a&gt;&lt;br/&gt;&lt;br/&gt;&lt;a href=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/a2.htm\"&gt;&lt;img src=\"http://da.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/a2.img\" border=\"0\"/&gt;&lt;/a&gt;&lt;img width=\"1\" height=\"1\" src=\"http://pi.feedsportal.com/r/218611948978/u/49/f/647410/c/35028/s/437dfbbb/sc/1/a2t.img\" border=\"0\"/&gt;&lt;img src=\"//feeds.feedburner.com/~r/Slashdot/slashdot/~4/L06b4tszG08\" height=\"1\" width=\"1\" alt=\"\"/&gt;</description>\n" +
                "      <pubDate>Tue, 17 Feb 2015 19:23:00 GMT</pubDate>\n" +
                "      <guid isPermaLink=\"false\">http://slashdot.feedsportal.com/c/35028/f/647410/s/437dfbbb/sc/1/l/0Lfeatures0Bslashdot0Borg0Cstory0C150C0A20C170C18382390Cinterviews0Eask0Estephen0Ewolfram0Ea0Equestion0Dutm0Isource0Frss10B0Amainlinkanon0Gutm0Imedium0Ffeed/story01.htm</guid>\n" +
                "      <dc:creator>samzenpus</dc:creator>\n" +
                "      <dc:subject>math</dc:subject>\n" +
                "      <dc:date>2015-02-17T19:23:00Z</dc:date>\n" +
                "    </item>\n" +
                "  </channel>\n" +
                "</rss>\n" +
                "\n";

        resultEndpoint.expectedBodiesReceived(expectedBody);

        template.sendBody(expectedBody);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                RSSContentProcessor rssProcessor = new RSSContentProcessor();

                DateStamp dateProcessor = new DateStamp();

                  from("direct:start").
                          setHeader(Feed.ID.NAME, simple("slashdot")).
                          setHeader(Feed.ID.URI, simple("http://www.epnoi.org/feeds/slashdot")).
                          setHeader(Feed.ID.URL, simple("http://rss.slashdot.org/Slashdot/slashdot")).
                          process(dateProcessor).
                          process(rssProcessor).
                          to("mock:result");
            }
        };
    }

}
