package es.upm.oeg.epnoi.collector.processor;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.builder.xml.Namespaces;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Ignore;
import org.junit.Test;


public class XPathTest extends CamelTestSupport{

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate template;

    @Test
    @Ignore
    public void testSendMatchingMessage() throws Exception {
        resultEndpoint.expectedMessageCount(1);

        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" xmlns:provenance=\"http://www.openarchives.org/OAI/2.0/provenance\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\">\n" +
                "    <responseDate>2015-03-03T09:38:52Z</responseDate>\n" +
                "    <request verb=\"ListRecords\" metadataPrefix=\"oai_dc\" from=\"2015-03-03T05:00:00Z\">http://eprints.ucm.es/cgi/oai2</request>\n" +
                "    <ListRecords>\n" +
                "        <record>\n" +
                "            <header>\n" +
                "                <identifier>oai:www.ucm.es:28868</identifier>\n" +
                "                <datestamp>2015-03-03T09:30:04Z</datestamp>\n" +
                "                <setSpec>7374617475733D707562</setSpec>\n" +
                "                <setSpec>7375626A656374733D41:415F3131:415F31315F323836</setSpec>\n" +
                "                <setSpec>7375626A656374733D41:415F3131:415F31315F333031</setSpec>\n" +
                "                <setSpec>74797065733D61727469636C65</setSpec>\n" +
                "            </header>\n" +
                "            <metadata>\n" +
                "                <dc>\n" +
                "                    <dc:relation>http://eprints.ucm.es/28868/</dc:relation>\n" +
                "                    <dc:title>Radiation tolerant isolation amplifiers for temperature measurement</dc:title>\n" +
                "                    <dc:creator>Zong, Yi</dc:creator>\n" +
                "                    <dc:creator>Franco Peláez, Francisco Javier</dc:creator>\n" +
                "                    <dc:creator>Agapito Serrano, Juan Andrés</dc:creator>\n" +
                "                    <dc:creator>Fernandes, Ana C.</dc:creator>\n" +
                "                    <dc:creator>Marques, José G.</dc:creator>\n" +
                "                    <dc:subject>Electrónica</dc:subject>\n" +
                "                    <dc:subject>Radiactividad</dc:subject>\n" +
                "                    <dc:description>This paper concentrates on the selection of radiation tolerant isolation amplifiers, which are suitable for the signal conditioners for cryogenic system in the Large Hadron Collider (LHC). The evolution and the results of different commercial isolation amplifiers’ parameters under neutron and gamma radiation are presented. In most cases, the tested isolation amplifiers’ input offset voltage, bias currents and output offset voltage hardly changed during the radiation. The \\{DC\\} gain in input stage was only affected for some isolation amplifiers with a small open loop gain. Transmission coefficient showed decrease for all the tested isolation amplifiers. Also, the \\{DC\\} output voltage increased and the ripple voltage decreased for all the build-in isolated regulators. In addition, results on 1B41 signal conditioner showed that it was tolerant to 7–8×1012&amp;#xa0;n/cm2, which was 50\\% higher than the expected dose in the LHC.</dc:description>\n" +
                "                    <dc:publisher>Elsevier Science BV</dc:publisher>\n" +
                "                    <dc:date>2006-09-29</dc:date>\n" +
                "                    <dc:type>info:eu-repo/semantics/article</dc:type>\n" +
                "                    <dc:type>PeerReviewed</dc:type>\n" +
                "                    <dc:identifier>http://eprints.ucm.es/28868/1/Zong2006_Eprint.pdf</dc:identifier>\n" +
                "                    <dc:format>application/pdf</dc:format>\n" +
                "                    <dc:language>en</dc:language>\n" +
                "                    <dc:rights>info:eu-repo/semantics/openAccess</dc:rights>\n" +
                "                    <dc:relation>http://www.sciencedirect.com/science/article/pii/S0168900206015774</dc:relation>\n" +
                "                    <dc:relation>10.1016/j.nima.2006.09.007</dc:relation>\n" +
                "                    <dc:relation>FPA2002-00912</dc:relation>\n" +
                "                    <dc:relation>K476/LHC</dc:relation>\n" +
                "                </dc>\n" +
                "            </metadata>\n" +
                "        </record>\n" +
                "    </ListRecords>\n" +
                "</OAI-PMH>\n";

        template.sendBody(xml);

        resultEndpoint.assertIsSatisfied();
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        return new RouteBuilder() {
            public void configure() {

                Namespaces ns = new Namespaces("oai", "http://www.openarchives.org/OAI/2.0/");
                ns.add("dc", "http://purl.org/dc/elements/1.1/");
                ns.add("provenance", "http://www.openarchives.org/OAI/2.0/provenance");
                ns.add("oai_dc","http://www.openarchives.org/OAI/2.0/oai_dc/");

                from("direct:start")
                        .setHeader("header.value1", constant("sample"))
                        .setHeader("fromXpath", xpath("//oai:metadata/oai:dc/dc:title/text()",String.class).namespaces(ns))
                        .to("mock:result");
            }
        };
    }

}
