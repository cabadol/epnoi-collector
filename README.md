# research-collector

This tool allows you harvest scientific publications from RSS servers and/or OAI-PMH data providers.  

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

## Publication Info
Because each server can provide information differently, we need to know how these attributes are distributed.
Using [XPATH](http://www.w3.org/TR/xpath/) expressions or constant values, you can define how to obtain them from the response received by the server.   
*To indicate that it is a XPATH expression you must include at first the character `$`*

| Element | RSS | OAI-PMH | Description |
| :------- |:--------:| :----------: | :------- |
| title    | `$//rss:item/rss:title/text()`| `$//oai:metadata/oai:dc/dc:title/text()` | asasasas |
| description    | `$//rss:item/rss:description/text()` | `$//oai:metadata/oai:dc/dc:description/text()` | An account of the resource. Description may include but is not limited to: an abstract, a table of contents, a graphical representation, or a free-text account of the resource. |
| published    | `$//rss:item/dc:date/text()`    | `$//oai:header/oai:datestamp/text()` | Date of submission of the resource |
| uri    | `$//rss:item/rss:link/text()`    | `$//oai:metadata/oai:dc/dc:identifier/text()` | asasasas |
| url    | `$//rss:item/rss:link/text()`    | `$//oai:metadata/oai:dc/dc:identifier/text()` | asasasas |
| language    | `$//rss:channel/dc:language/text()`    | `$/oai:metadata/oai:dc/dc:language/text()` | asasasas |
| rights    | `$//rss:channel/dc:rights/text()`    | `$//oai:metadata/oai:dc/dc:rights/text()` | asasasas |
| creators    | `$string-join(//rss:channel/dc:creator/text(),";")`    | `$string-join(//oai:metadata/oai:dc/dc:creator/text(),";")` | List of entities primarily responsible for making the resource |
| format    | `htm`    | `pdf` | asasasas |

Namespaces included to be used by XPATH expressions are:  
| Namespace | Code | 
| :------- |:--------:| 
| http://www.openarchives.org/OAI/2.0/    | `oai`| 
| http://purl.org/dc/elements/1.1/    | `dc` | 
| http://www.openarchives.org/OAI/2.0/provenance    | `provenance`    | 
| http://www.openarchives.org/OAI/2.0/oai_dc/    | `oai_dc`    | 
| http://purl.org/rss/1.0/    | `rss`    | 

## Format
We used *json* to 



This work is funded by the EC-funded project DrInventor ([www.drinventor.eu](www.drinventor.eu)).
