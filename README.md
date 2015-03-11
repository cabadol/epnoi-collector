For more details about Epnoi see: https://github.com/fitash/epnoi/wiki

# Epnoi-Collector

This tool allows you harvest scientific publications from [RSS](http://www.rssboard.org/rss-specification) servers and/or [OAI-PMH](http://www.openarchives.org) data providers.  

## New Routes
We use [Camel](http://camel.apache.org) to set our harvesting workflows. All these routes are defined in `config/routes.groovy`. It is a [Groovy](http://groovy.codehaus.org) route builder that allows, in a easy way, create/modify/delete collection flows.  

For a RSS source, for instance [Slashdot](http://rss.slashdot.org/Slashdot/slashdot), you can define the following route:  
```groovy
from("rss:http://rss.slashdot.org/Slashdot/slashdot?" +
                "splitEntries=true&consumer.initialDelay=1000&consumer.delay=2000" +
                "&feedHeader=false&filter=true").marshal().rss().
                setProperty(SOURCE_NAME, constant("slashdot")).
                setProperty(SOURCE_URL,  constant("http://rss.slashdot.org/Slashdot/slashdot")).
                to("direct:setCommonRssXpathExpressions").
                to("direct:retrieveByHttpAndSave").
                to("direct:notifyUIA")
```
For an OAI-PMH source, for instance [UPM](http://oa.upm.es/perl/oai2), the route will be: 
```groovy
from("oaipmh://oa.upm.es/perl/oai2?initialDelay=1000&delay=60000").
                setProperty(SOURCE_NAME,        constant("upm")).
                setProperty(SOURCE_URL,         constant("http://oa.upm.es/perl/oai2")).
                to("direct:setCommonOaipmhXpathExpressions").
                setProperty(PUBLICATION_URL,    xpath("//oai:metadata/oai:dc/dc:relation/text()",String.class).namespaces(ns)).
                to("direct:avoidDeleted").
                to("direct:retrieveByHttpAndSave").
                to("direct:notifyUIA")
```
## Common Routes

As you have seen before, exist some routes that are used but are not defined in `routes.groovy`. They contain common actions and can be used from any new route. 

### direct:setCommonRssXpathExpressions
```groovy
from("direct:setCommonRssXpathExpressions").
                setProperty(SOURCE_PROTOCOL,            constant("rss")).
                setProperty(SOURCE_URI,                 simple("http://www.epnoi.org/rss/${property."+SOURCE_NAME+"}")).
                setProperty(PUBLICATION_TITLE,          xpath("//rss:item/rss:title/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_DESCRIPTION,    xpath("//rss:item/rss:description/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_PUBLISHED,      xpath("//rss:item/dc:date/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_URI,            xpath("//rss:item/rss:link/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_URL,            xpath("//rss:item/rss:link/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_LANGUAGE,       xpath("//rss:channel/dc:language/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_RIGHTS,         xpath("//rss:channel/dc:rights/text()", String.class).namespaces(ns)).
                setProperty(PUBLICATION_CREATORS,       xpath("string-join(//rss:channel/dc:creator/text(),\";\")", String.class).namespaces(ns)).
                setProperty(PUBLICATION_FORMAT,         constant("htm")).
                setProperty(PUBLICATION_METADATA_FORMAT,constant("xml"));
```

### direct:setCommonOaipmhXpathExpressions
```groovy
from("direct:setCommonOaipmhXpathExpressions").
                setProperty(SOURCE_PROTOCOL, constant("oaipmh")).
                setProperty(SOURCE_URI,                 simple("http://www.epnoi.org/oaipmh/${property." + SOURCE_NAME + "}")).
                setProperty(PUBLICATION_TITLE,          xpath("//oai:metadata/oai:dc/dc:title/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_DESCRIPTION,    xpath("//oai:metadata/oai:dc/dc:description/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_PUBLISHED,      xpath("//oai:header/oai:datestamp/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URI,            xpath("//oai:header/oai:identifier/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_URL,            xpath("//oai:metadata/oai:dc/dc:identifier/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_LANGUAGE,       xpath("//oai:metadata/oai:dc/dc:language/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_RIGHTS,         xpath("//oai:metadata/oai:dc/dc:rights/text()",String.class).namespaces(ns)).
                setProperty(PUBLICATION_CREATORS,       xpath("string-join(//oai:metadata/oai:dc/dc:creator/text(),\";\")",String.class).namespaces(ns)).
                setProperty(PUBLICATION_FORMAT, xpath("substring-after(//oai:metadata/oai:dc/dc:format/text(),\"/\")", String.class).namespaces(ns)).
                setProperty(PUBLICATION_METADATA_FORMAT, constant("xml"));
```

### direct:avoidDeleted
```groovy
from("direct:avoidDeleted").
                choice().
                when().xpath("//oai:header[@status=\"deleted\"]", String.class, ns).stop().
                end();
```

### direct:saveToFile
```groovy
from("direct:saveToFile").
                setHeader(ARGUMENT_PATH, simple("${property." + SOURCE_PROTOCOL + "}/${property." + SOURCE_NAME + "}/${property" + PUBLICATION_PUBLISHED_DATE + "}/${header." + ARGUMENT_NAME + "}")).
                to("file:"+basedir+"/?fileName=${header."+ARGUMENT_PATH+"}");
```

### direct:downloadByHttp
```groovy
from("direct:downloadByHttp").
                // Filter resources with available url
                filter(header(ARGUMENT_PATH).isNotEqualTo("")).
                setHeader(Exchange.HTTP_METHOD, constant("GET")).
                setHeader(Exchange.HTTP_URI, simple("${header." + ARGUMENT_PATH + "}")).
                to("http://dummyhost?throwExceptionOnFailure=false");
```

### direct:retrieveByHttpAndSave
```groovy
rom("direct:retrieveByHttpAndSave").
                process(timeClock).
                process(uuidGenerator).
                setHeader(ARGUMENT_NAME,        simple("${property."+PUBLICATION_UUID+"}."+"${property."+PUBLICATION_METADATA_FORMAT+"}")).
                to("direct:saveToFile").
                setHeader(ARGUMENT_PATH,        simple("${property."+PUBLICATION_URL+"}")).
                to("direct:downloadByHttp").
                setHeader(ARGUMENT_NAME,        simple("${property."+PUBLICATION_UUID+"}."+"${property."+PUBLICATION_FORMAT+"}")).
                to("direct:saveToFile").
                setProperty(PUBLICATION_URL_LOCAL, simple("${header." + ARGUMENT_PATH + "}"));
```


## Publication Info
Because each server can provide information differently, we need to know how these attributes are distributed:  

| Attribute | Description |
| :--- |:---|
| [title](http://dublincore.org/documents/dcmi-terms/#elements-title)    | A name given to the resource. | 
| [description](http://dublincore.org/documents/dcmi-terms/#elements-description)    | An account of the resource. Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource. | 
| [published](http://dublincore.org/documents/dcmi-terms/#terms-dateSubmitted)    | Date of submission of the resource.  | 
| [uri](http://dublincore.org/documents/dcmi-terms/#URI)    | Identifier constructed according to the generic syntax for Uniform Resource Identifiers as specified by the Internet Engineering Task Force. | 
| [url](http://dublincore.org/documents/dcmi-terms/#terms-identifier)    | An unambiguous reference to the resource file. | 
| [language](http://dublincore.org/documents/dcmi-terms/#elements-language)    | A language of the resource. Recommended best practice is to use a controlled vocabulary such as RFC 4646 [RFC4646]. | 
| [rights](http://dublincore.org/documents/dcmi-terms/#terms-rights)    | Information about rights held in and over the resource. Typically, rights information includes a statement about various property rights associated with the resource, including intellectual property rights. | 
| [creators](http://dublincore.org/documents/dcmi-terms/#terms-creator)    | List of entities, separated by `;`, primarily responsible for making the resource. Examples of a Creator include a person, an organization, or a service. | 
| [format](http://dublincore.org/documents/dcmi-terms/#terms-format)    | The file format, physical medium, or dimensions of the resource. | 

Using [XPath](http://www.w3.org/TR/xpath/) expressions or constant values, you can define how to obtain the attributes from the response received by the server in your route.  
The list of namespaces available to be used in *xpath* expressions are the following:  

| Namespace | Code | 
| :------- |:-----| 
| http://www.openarchives.org/OAI/2.0/    | `oai`| 
| http://purl.org/dc/elements/1.1/    | `dc` | 
| http://www.openarchives.org/OAI/2.0/provenance    | `provenance`    | 
| http://www.openarchives.org/OAI/2.0/oai_dc/    | `oai_dc`    | 
| http://purl.org/rss/1.0/    | `rss`    | 

# Download

Download the binary distribution:

| Version | Link |
| :------- |:-----|
| 1.0.2    | [tar.gz](http://github.com/cabadol/epnoi-collector/raw/mvn-repo/es/upm/oeg/epnoi-collector/1.0.2/epnoi-collector-1.0.2.tar.gz)|

This work is funded by the EC-funded project DrInventor ([www.drinventor.eu](www.drinventor.eu)).
