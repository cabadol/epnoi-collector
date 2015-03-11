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
        super.configure()

        /*********************************************************************************************************************************
         * ROUTE 1: Slashdot
         *********************************************************************************************************************************/
        from("rss:http://rss.slashdot.org/Slashdot/slashdot?splitEntries=true&consumer.initialDelay=1000&consumer.delay=2000&feedHeader=false&filter=true").marshal().rss().
                setProperty(SOURCE_NAME,        constant("slashdot")).
                setProperty(SOURCE_URL,         constant("http://rss.slashdot.org/Slashdot/slashdot")).
                to("direct:setCommonRssXpathExpressions").
                to("direct:retrieveByHttpAndSave")

        /*********************************************************************************************************************************
         * ROUTE 2: UPM
         *********************************************************************************************************************************/
        from("oaipmh://oa.upm.es/perl/oai2?initialDelay=1000&delay=60000").
                setProperty(SOURCE_NAME,        constant("upm")).
                setProperty(SOURCE_URL,         constant("http://oa.upm.es/perl/oai2")).
                to("direct:setCommonOaipmhXpathExpressions").
                setProperty(PUBLICATION_URL,    xpath("//oai:metadata/oai:dc/dc:relation/text()",String.class).namespaces(ns)).
                to("direct:avoidDeleted").
                to("direct:retrieveByHttpAndSave")

        /*********************************************************************************************************************************
         * ROUTE PARTIAL 1:  Save metadata and retrieve resource by Http
         *********************************************************************************************************************************/
        from("direct:retrieveByHttpAndSave").
                process(timeClock).
                process(uuidGenerator).
                setHeader(ARGUMENT_NAME,        simple("\${property."+PUBLICATION_UUID+"}."+"\${property."+PUBLICATION_METADATA_FORMAT+"}")).
                to("direct:saveToFile").
                setHeader(ARGUMENT_PATH,        simple("\${property."+PUBLICATION_URL+"}")).
                to("direct:downloadByHttp").
                setHeader(ARGUMENT_NAME,        simple("\${property."+PUBLICATION_UUID+"}."+"\${property."+PUBLICATION_FORMAT+"}")).
                to("direct:saveToFile")

        /*********************************************************************************************************************************
         * -> Set Common Rss Xpath Expressions
         *********************************************************************************************************************************/
        from("direct:setCommonRssXpathExpressions").
                setProperty(SOURCE_PROTOCOL,            constant("rss")).
                setProperty(SOURCE_URI,                 simple("http://www.epnoi.org/rss/\${property."+SOURCE_NAME+"}")).
                setProperty(PUBLICATION_TITLE,          xpath("//rss:item/rss:title/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_DESCRIPTION,    xpath("//rss:item/rss:description/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_PUBLISHED,      xpath("//rss:item/dc:date/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URI,            xpath("//rss:item/rss:link/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URL,            xpath("//rss:item/rss:link/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_LANGUAGE,       xpath("//rss:channel/dc:language/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_RIGHTS,         xpath("//rss:channel/dc:rights/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_CREATORS,       xpath("string-join(//rss:channel/dc:creator/text(),\";\")",String.class).namespaces(ns)).
                setProperty(PUBLICATION_FORMAT,         constant("htm")).
                setProperty(PUBLICATION_METADATA_FORMAT,constant("xml"))

        /*********************************************************************************************************************************
         * -> Set Common OAI-PMH Xpath Expressions
         *********************************************************************************************************************************/
        from("direct:setCommonOaipmhXpathExpressions").
                setProperty(SOURCE_PROTOCOL,            constant("oaipmh")).
                setProperty(SOURCE_URI,                 simple("http://www.epnoi.org/oaipmh/\${property."+SOURCE_NAME+"}")).
                setProperty(PUBLICATION_TITLE,          xpath("//oai:metadata/oai:dc/dc:title/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_DESCRIPTION,    xpath("//oai:metadata/oai:dc/dc:description/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_PUBLISHED,      xpath("//oai:header/oai:datestamp/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URI,            xpath("//oai:header/oai:identifier/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URL,            xpath("//oai:metadata/oai:dc/dc:identifier/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_LANGUAGE,       xpath("//oai:metadata/oai:dc/dc:language/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_RIGHTS,         xpath("//oai:metadata/oai:dc/dc:rights/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_CREATORS,       xpath("string-join(//oai:metadata/oai:dc/dc:creator/text(),\";\")",String.class).namespaces(ns)).
                setProperty(PUBLICATION_FORMAT,         constant("pdf")).
                setProperty(PUBLICATION_METADATA_FORMAT,constant("xml"))


        /*********************************************************************************************************************************
         * -> Avoid OAI-PMH Deleted Resources
         *********************************************************************************************************************************/
        from("direct:avoidDeleted").
                choice().
                when().xpath("//oai:header[@status=\"deleted\"]", String.class, ns).stop().
                end()

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
