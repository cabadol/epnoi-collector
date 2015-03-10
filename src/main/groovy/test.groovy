@Grab(group='xalan', module='xalan', version='2.6.0')
import org.apache.xpath.XPathAPI
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.*
import javax.xml.namespace.*


def CAR_RECORDS = '''
    <records>
      <car name='HSV Maloo' make='Holden' year='2006'>
        <country>Australia</country>
        <record type='speed'>Production Pickup Truck with speed of 271kph</record>
      </car>
      <car name='P50' make='Peel' year='1962'>
        <country>Isle of Man</country>
        <record type='size'>Smallest Street-Legal Car at 99cm wide and 59 kg in weight</record>
      </car>
      <car name='Royale' make='Bugatti' year='1931'>
        <country>France</country>
        <record type='price'>Most Valuable Car at $15 million</record>
      </car>
    </records>
  '''

messages = []

def processCar(car) {
    def make = XPathAPI.eval(car, '@make').str()
    def country = XPathAPI.eval(car, 'country/text()').str()
    def type = XPathAPI.eval(car, 'record/@type').str()
    messages << make + ' of ' + country + ' has a ' + type + ' record'
}

def builder     = DocumentBuilderFactory.newInstance().newDocumentBuilder()
def inputStream = new ByteArrayInputStream(CAR_RECORDS.bytes)
def records     = builder.parse(inputStream).documentElement

XPathAPI.selectNodeList(records, '//car').each{ processCar(it) }

assert messages == [
    'Holden of Australia has a speed record',
    'Peel of Isle of Man has a size record',
    'Bugatti of France has a price record'
]