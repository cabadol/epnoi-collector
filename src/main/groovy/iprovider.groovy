//@Grab(group='xalan', module='xalan', version='2.6.0')
//import org.apache.xpath.XPathAPI
//import javax.xml.parsers.DocumentBuilderFactory
//import javax.xml.xpath.*
//import javax.xml.namespace.*
import groovy.json.*

def download(file,address)
{
	println "downloading '$file' from $address .."
	def fileOut 	= new FileOutputStream(file)
    def out 	= new BufferedOutputStream(fileOut)
    out << new URL(address).openStream()
    out.close()
    fileOut.close()
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
// Prepare json provider file
def listProviders = []
def tmpJsonFile = new File("providers-tmp.json")

baseURLs.baseURL.each{ baseURL ->
	def identifier  = baseURL.'@id'
	def url 		= baseURL.text()
	
	if ( identifier == null){
		URI uri = new URI(url)
		identifier = uri.host
	}
	def xmlFile = new File(identifier+".xml",directory)
	try{
		download(xmlFile,url+"?verb=ListRecords&metadataPrefix=oai_dc")
		def provider = [
			"name":identifier,
			"protocol": "oaipmh",
			"url":url
			]
		listProviders.add(provider)
		def json = JsonOutput.toJson(provider)
		def prettyJson = JsonOutput.prettyPrint(json)
		tmpJsonFile.append(prettyJson)
		tmpJsonFile.append(",\n")
		println "$json"
	} catch (Exception e){
		println "Error downloading from $baseURL: $e"
	}
}

def providers = ["providers":listProviders]
def json = JsonOutput.toJson(providers)
File jsonFile = new File("providers.json")
jsonFile << JsonOutput.prettyPrint(json)

