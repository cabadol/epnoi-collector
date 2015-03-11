import es.upm.oeg.epnoi.collector.AbstractRouteBuilder

class routes extends AbstractRouteBuilder{

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
                to("direct:retrieveByHttpAndSave").
                to("direct:notifyUIA")

        /*********************************************************************************************************************************
         * ROUTE 2: UPM
         *********************************************************************************************************************************/
        from("oaipmh://oa.upm.es/perl/oai2?initialDelay=1000&delay=60000").
                setProperty(SOURCE_NAME,        constant("upm")).
                setProperty(SOURCE_URL,         constant("http://oa.upm.es/perl/oai2")).
                to("direct:setCommonOaipmhXpathExpressions").
                setProperty(PUBLICATION_URL,    xpath("//oai:metadata/oai:dc/dc:relation/text()",String.class).namespaces(ns)).
                to("direct:avoidDeleted").
                to("direct:retrieveByHttpAndSave").
                to("direct:notifyUIA")

        /*********************************************************************************************************************************
         * ROUTE 3: UCM
         *********************************************************************************************************************************/
        from("oaipmh://eprints.ucm.es/cgi/oai2?initialDelay=1000&delay=60000").
                setProperty(SOURCE_NAME,        constant("ucm")).
                setProperty(SOURCE_URL,         constant("http://eprints.ucm.es/cgi/oai2")).
                to("direct:setCommonOaipmhXpathExpressions").
                setProperty(PUBLICATION_URL,    xpath("//oai:metadata/oai:dc/dc:identifier/text()",String.class).namespaces(ns)).
                to("direct:avoidDeleted").
                to("direct:retrieveByHttpAndSave").
                to("direct:notifyUIA")

        /*********************************************************************************************************************************
         * ROUTE 4: SciencePubCo
         *********************************************************************************************************************************/
        from("oaipmh://www.sciencepubco.com/index.php/IJAA/oai?initialDelay=1000&delay=60000").
                setProperty(SOURCE_NAME,        constant("sciencepubco")).
                setProperty(SOURCE_URL,         constant("http://www.sciencepubco.com/index.php/IJAA/oai")).
                to("direct:setCommonOaipmhXpathExpressions").
                setProperty(PUBLICATION_URL,    xpath("replace(substring-before(concat(string-join(//oai:metadata/oai:dc/dc:relation/text(),\";\"),\";\"),\";\"),\"view\",\"download\")",String.class).namespaces(ns)).
                to("direct:avoidDeleted").
                to("direct:retrieveByHttpAndSave").
                to("direct:notifyUIA")

        /*********************************************************************************************************************************
         * -> To UIA
         *********************************************************************************************************************************/
        from("direct:notifyUIA").
                process(contextBuilder).
                //to("euia:out?servers="+ uiaServers)
                to("stream:out")


    }

}
