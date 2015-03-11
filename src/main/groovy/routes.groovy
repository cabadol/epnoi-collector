import es.upm.oeg.epnoi.collector.CollectorRouteBuilder
import org.apache.camel.Exchange
import org.apache.camel.builder.xml.Namespaces

class routes extends CollectorRouteBuilder{

    def ns = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/")
            .add("dc", "http://purl.org/dc/elements/1.1/")
            .add("provenance", "http://www.openarchives.org/OAI/2.0/provenance")
            .add("oai_dc", "http://www.openarchives.org/OAI/2.0/oai_dc/")
            .add("rss","http://purl.org/rss/1.0/");

    @Override
    public void configure() throws Exception {


        /*********************************************************************************************************************************
         * ROUTE 1: Slashdot
         *********************************************************************************************************************************/
        from("rss:http://rss.slashdot.org/Slashdot/slashdot?splitEntries=true&consumer.initialDelay=1000&consumer.delay=1000&feedHeader=false&filter=true").marshal().rss().
                setProperty(SOURCE_NAME,        constant("slashdot")).
                setProperty(SOURCE_URL,         constant("http://rss.slashdot.org/Slashdot/slashdot")).
                to("direct:setRssXpathExpressions").
                process(timeClock).
                process(uuidGenerator).
                setHeader(ARGUMENT_NAME,        simple("\${property."+PUBLICATION_UUID+"}.xml")).
                to("direct:saveToFile").
                setHeader(ARGUMENT_PATH,        simple("\${property."+PUBLICATION_URL+"}")).
                to("direct:downloadByHttp").
                setHeader(ARGUMENT_NAME,        simple("\${property."+PUBLICATION_UUID+"}.htm")).
                to("direct:saveToFile")


        /*********************************************************************************************************************************
         * -> Set Rss Xpath Expressions
         *********************************************************************************************************************************/
        from("direct:setRssXpathExpressions").
                setProperty(SOURCE_PROTOCOL,            constant("rss")).
                setProperty(SOURCE_URI,                 simple("http://www.epnoi.org/rss/\${property."+SOURCE_NAME+"}")).
                setProperty(PUBLICATION_TITLE,          xpath("//rss:item/rss:title/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_DESCRIPTION,    xpath("//rss:item/rss:description/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_PUBLISHED,      xpath("//rss:item/dc:date/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URI,            xpath("//rss:item/rss:link/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URL,            xpath("//rss:item/rss:link/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_LANGUAGE,       xpath("//rss:channel/dc:language/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_RIGHTS,         xpath("//rss:channel/dc:rights/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_CREATORS,       xpath("string-join(//rss:channel/dc:creator/text(),\";\")",String.class).namespaces(ns))

        /*********************************************************************************************************************************
         * -> Save File
         *********************************************************************************************************************************/
        from("direct:saveToFile").
                setHeader(ARGUMENT_PATH,simple("\${property."+SOURCE_PROTOCOL+"}/\${property."+SOURCE_NAME+"}/\${property"+PUBLICATION_PUBLISHED_DATE+"}/\${header."+ARGUMENT_NAME+"}")).
                to("file:target/?fileName=\${header."+ARGUMENT_PATH+"}")


        /*********************************************************************************************************************************
         * -> Download Resource by Http
         *********************************************************************************************************************************/
        from("direct:downloadByHttp").
        // Filter resources with available url
                filter(header(ARGUMENT_PATH).isNotEqualTo("")).
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI,    simple("\${header."+ARGUMENT_PATH+"}")).
                to("http://dummyhost?throwExceptionOnFailure=false")

    }

}
