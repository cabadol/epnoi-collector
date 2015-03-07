# research-collector

This tool allows you harvest scientific publications from [RSS](http://www.rssboard.org/rss-specification) servers and/or [OAI-PMH](http://www.openarchives.org) data providers.  

## Provider Info
| Property | Default  | Description |
| :------- |:--------:| :---------- |
| protocol\*    |     | `rss` or `oaipmh` |
| url\*    |     | The base URL for making protocol requests to the repository |
| name    | *url domain name*    | A human readable name for the repository |
| uri    | `http://www.epnoi.org/'protocol'/'name'`    | A uniform resource identifier used to identify the repository |
| from    | `1970-01-01T00:00:00Z`    | Specifies a lower bound for datestamp-based selective harvesting. It is a valid [ISO-8601](http://www.iso.org/iso/catalogue_detail?csnumber=40874) value. After first request, this value is updated to current time |
| delay    | `60000 `   | Delay in milliseconds between each poll |
| initialDelay    | `1000`    | Milliseconds before polling starts |

\* These attributes are mandatory

## Publication Info
Because each server can provide information differently, we need to know how these attributes are distributed.  
Using [XPATH](http://www.w3.org/TR/xpath/) expressions or constant values, you can define how to obtain them from the response received by the server.  

The list of namespaces loaded in the system to be used by XPATH expressions are the following:  

| Namespace | Code | 
| :------- |:-----| 
| http://www.openarchives.org/OAI/2.0/    | `oai`| 
| http://purl.org/dc/elements/1.1/    | `dc` | 
| http://www.openarchives.org/OAI/2.0/provenance    | `provenance`    | 
| http://www.openarchives.org/OAI/2.0/oai_dc/    | `oai_dc`    | 
| http://purl.org/rss/1.0/    | `rss`    | 


All XPath expressions must include at first the `$` character:  

| Element | RSS | OAI-PMH |
| :---: |:---| :--- | 
| title    | `$//rss:item/rss:title/text()` | `$//oai:metadata/oai:dc/dc:title/text()` | 
| description    | `$//rss:item/rss:description/text()` | `$//oai:metadata/oai:dc/dc:description/text()` | 
| published    | `$//rss:item/dc:date/text()`    | `$//oai:header/oai:datestamp/text()` | 
| uri    | `$//rss:item/rss:link/text()`    | `$//oai:metadata/oai:dc/dc:identifier/text()` | 
| url    | `$//rss:item/rss:link/text()`    | `$//oai:metadata/oai:dc/dc:identifier/text()` | 
| language    | `$//rss:channel/dc:language/text()`    | `$/oai:metadata/oai:dc/dc:language/text()` | 
| rights    | `$//rss:channel/dc:rights/text()`    | `$//oai:metadata/oai:dc/dc:rights/text()` | 
| creators    | `$string-join(//rss:channel/dc:creator/text(),";")`    | `$string-join(//oai:metadata/oai:dc/dc:creator/text(),";")` | 
| format    | `htm`    | `pdf` | 


## Config Format
System is configured using a *json* file like this:

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
      "url": "http://oa.upm.es/perl/oai2",
      "from": "2015-01-01T00:00:00Z"
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

This work is funded by the EC-funded project DrInventor ([www.drinventor.eu](www.drinventor.eu)).
