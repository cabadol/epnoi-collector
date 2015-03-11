@Grab(group='org.codehaus.gpars', module='gpars', version='1.2.1')
import groovyx.gpars.*

def download(file,address)
{
	println "downloading '$file' from $address .."
	def fileOut 	= new FileOutputStream(file)
    def out 	= new BufferedOutputStream(fileOut)
    out << new URL(address).openStream()
    out.close()
    fileOut.close()
}

def checkServers(servers,directory) {

    GParsPool.withPool {
        servers.eachParallel { baseURL ->
            def identifier  = baseURL.'@id'
            def url 		= baseURL.text()

            if ( identifier == null){
                URI uri = new URI(url)
                identifier = uri.host
            }
            def xmlFile = new File(identifier+".xml",directory)
            try{
                download(xmlFile,url+"?verb=ListRecords&metadataPrefix=oai_dc")

                def provider = "" +
                        "         /*********************************************************************************************************************************\n" +
                        "         * ROUTE xxx: $identifier\n" +
                        "         *********************************************************************************************************************************/\n" +
                        "         from(\"oaipmh://$url?initialDelay=1000&delay=60000\").\n" +
                        "                setProperty(SOURCE_NAME,        constant(\"$identifier\")).\n" +
                        "                setProperty(SOURCE_URL,         constant(\"$url\")).\n" +
                        "                to(\"direct:setCommonOaipmhXpathExpressions\").\n" +
                        "                setProperty(PUBLICATION_URL,    xpath(\"//oai:metadata/oai:dc/dc:relation/text()\",String.class).namespaces(ns)).\n" +
                        "                to(\"direct:avoidDeleted\").\n" +
                        "                to(\"direct:retrieveByHttpAndSave\").\n" +
                        "                to(\"direct:notifyUIA\")"
                def outFile = new File(identifier+".groovy",directory)
                outFile << provider
            } catch (Exception e){
                println "Error downloading from $baseURL: $e"
                xmlFile.delete()
            }
        }
    }

}

// Download the list of providers
def listOfProviders = new File("listOfProviders.xml")
download(listOfProviders,"http://www.openarchives.org/pmh/registry/ListFriends")

// Read base URLs
def baseURLs = new XmlParser().parseText(listOfProviders.text)
def allURLs = baseURLs.baseURL.size()
println " $allURLs urls read"

// Download xml for each data provider
def directory = new File("data-providers")
directory.mkdir()

checkServers(baseURLs.baseURL,directory)
