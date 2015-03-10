@Grab(group='xalan', module='xalan', version='2.6.0')
import org.apache.xpath.XPathAPI
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.*
import javax.xml.namespace.*



def read(record){
	println "Record: $record"
	
	def title	= XPathAPI.eval(record,'//oai:metadata/oai:dc/dc:title/text()').str()
	println "Title: $title"
}


def check(address)
{
	//def request = address+"?verb=ListRecords&metadataPrefix=oai_dc"
	//URI uri 	= new URI(address)
	//def path 	= uri.host+".xml"
	//println "> $request"
	//def file 	= new FileOutputStream(path)
    //def out 	= new BufferedOutputStream(file)
    //out << new URL(request).openStream()
    //out.close()
    //file.close()


    def xmlFile = new File("www.informatik.uni-stuttgart.de.xml")
    def exists = xmlFile.exists()
    println "$exists downloaded"

    def xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile)
	println "Xml: $xml"

	def ns = ["oai":"http://www.openarchives.org/OAI/2.0/",
	    "dc": "http://purl.org/dc/elements/1.1/",
        "provenance": "http://www.openarchives.org/OAI/2.0/provenance",
        "oai_dc":"http://www.openarchives.org/OAI/2.0/oai_dc/",
        "rss":"http://purl.org/rss/1.0/"
	]

	def xpath = XPathFactory.newInstance().newXPath()
	xpath.setNamespaceContext([getNamespaceURI: { 
            prefix ->
            ns.get(prefix)
        }, 
        getPrefix: { namespaceURI ->
            ns.each { key, value ->
                if (namespaceURI == value) {
                    return key
                }
            }
        }, 
        getPrefixes: { namespaceURI ->
            return [getPrefix(namespaceURI)]
        }] as NamespaceContext)


	String expression = "//oai:metadata/oai:dc/dc:title";
 
	//read a string value
	String email = xpath.compile(expression).evaluate(xml);
	println "Title $email"

	def title = xpath.evaluate('//oai:metadata/oai:dc/dc:title/text()',xml.getDocumentElement())
	println "Title $title"
	
	//def records = xpath.evaluate('//oai:record',xml.getDocumentElement())

	//println "Records $records"
	//def item = records.item(0)
	//println "Items $item"
	//xpath.evaluate('//oai:record',xml.getDocumentElement(), XPathConstants.NODESET).each{ read(it) }
}

check("http://www.informatik.uni-stuttgart.de/cgi-bin/OAI/OAI.pl")

