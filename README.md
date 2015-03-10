For more details about Epnoi see: https://github.com/fitash/epnoi/wiki

# Epnoi-Collector

This tool allows you harvest scientific publications from [RSS](http://www.rssboard.org/rss-specification) servers and/or [OAI-PMH](http://www.openarchives.org) data providers.  

## Provider Info
| Property | Default  | Description |
| :------- |:--------:| :---------- |
| *protocol*\*    |     | `rss` or `oaipmh` |
| *url*\*    |     | The base URL for making protocol requests to the repository |
| name    | *url domain name*    | A human readable name for the repository |
| uri    | `http://www.epnoi.org/'protocol'/'name'`    | A uniform resource identifier used to identify the repository |
| from    | `1970-01-01T00:00:00Z`    | Specifies a lower bound for datestamp-based selective harvesting. It is a valid [ISO-8601](http://www.iso.org/iso/catalogue_detail?csnumber=40874) value. After first request, this value is updated to current time |
| delay    | `60000 `   | Delay in milliseconds between each poll |
| initialDelay    | `1000`    | Milliseconds before polling starts |

\* *these attributes are mandatory*

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

Using [XPath](http://www.w3.org/TR/xpath/) expressions (prefix `$`) or constant values, you can define how to obtain the attributes from the response received by the server: 

| Element | RSS | OAI-PMH |
| :---: |:---| :--- | 
| title    | `$//rss:item/rss:title/text()` | `$//oai:metadata/oai:dc/dc:title/text()` | 
| description    | `$//rss:item/rss:description/text()` | `$//oai:metadata/oai:dc/dc:description/text()` | 
| published    | `$//rss:item/dc:date/text()`    | `$//oai:header/oai:datestamp/text()` | 
| uri    | `$//rss:item/rss:link/text()`    | `$//oai:header/oai:identifier/text()` |
| url    | `$//rss:item/rss:link/text()`    | `$//oai:metadata/oai:dc/dc:identifier/text()` | 
| language    | `$//rss:channel/dc:language/text()`    | `$/oai:metadata/oai:dc/dc:language/text()` | 
| rights    | `$//rss:channel/dc:rights/text()`    | `$//oai:metadata/oai:dc/dc:rights/text()` | 
| creators    | `$string-join(//rss:channel/dc:creator/text(),";")`    | `$string-join(//oai:metadata/oai:dc/dc:creator/text(),";")` | 
| format    | `htm`    | `pdf` | 

The list of namespaces available to be used in *xpath* expressions are the following:  

| Namespace | Code | 
| :------- |:-----| 
| http://www.openarchives.org/OAI/2.0/    | `oai`| 
| http://purl.org/dc/elements/1.1/    | `dc` | 
| http://www.openarchives.org/OAI/2.0/provenance    | `provenance`    | 
| http://www.openarchives.org/OAI/2.0/oai_dc/    | `oai_dc`    | 
| http://purl.org/rss/1.0/    | `rss`    | 

## Config Format
All providers are listed in the `providers.json` file:

```json
{
  "providers": [
    {
      "name":"slashdot",
      "protocol": "rss",
      "url": "http://rss.slashdot.org/Slashdot/slashdot"

    },
    {
      "name": "upm",
      "protocol": "oaipmh",
      "url": "http://oa.upm.es/perl/oai2"
    },
    {
      "name": "ucm",
      "protocol": "oaipmh",
      "url": "http://eprints.ucm.es/cgi/oai2",
      "from": "2014-01-01T00:00:00Z"
    }
  ]
}
```

# Download

Download the binary distribution:  [here](https://github.com/cabadol/epnoi-collector/blob/mvn-repo/es/upm/oeg/epnoi-collector/1.0.0/epnoi-collector-1.0.0.tar.gz)

| Version | Link |
| :------- |:-----|
| 1.0.0    | [tar.gz](http://github.com/cabadol/epnoi-collector/raw/mvn-repo/es/upm/oeg/epnoi-collector/1.0.0/epnoi-collector-1.0.0.tar.gz)|

This work is funded by the EC-funded project DrInventor ([www.drinventor.eu](www.drinventor.eu)).
