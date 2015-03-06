package es.upm.oeg.epnoi.collector.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;

public class ConfigTypeTest {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigTypeTest.class);

    @Test
    public void toJson(){

        Provider provider = new Provider();
        provider.setName("slashdot");
        provider.setProtocol("rss");
        provider.setUrl("http://rss.slashdot.org/Slashdot/slashdot");
        provider.setDelay(6000);
        provider.setFrom("2015-03-06T16:26:00Z");


        Provider.Publication publication = new Provider.Publication();
        publication.setTitle("//rss:item/rss:title/text()");
        publication.setDescription("//rss:item/rss:description/text()\"");
        publication.setUri("//rss:item/rss:link/text()");
        publication.setUrl("//rss:item/rss:link/text()");
        publication.setCreators("string-join(//rss:channel/dc:creator/text(),\";\")");
        publication.setLanguage("//rss:channel/dc:language/text()\"");
        publication.setRights("//rss:channel/dc:rights/text()");
        publication.setFormat("htm");
        provider.setPublication(publication);


        provider.validate();

        Configuration configType = new Configuration();
        configType.add(provider);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(configType);
        LOG.debug("Json read: {}",json);
        Assert.assertNotNull(configType);
    }

    @Test
    public void fromJson() throws FileNotFoundException {

        String json = "{\n" +
                "  \"providers\": [\n" +
                "    {\n" +
                "      \"name\":\"slashdot\",\n" +
                "      \"protocol\": \"rss\",\n" +
                "      \"url\": \"http://rss.slashdot.org/Slashdot/slashdot\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"name\": \"ucm\",\n" +
                "      \"protocol\": \"oaipmh\",\n" +
                "      \"url\": \"http://eprints.ucm.es/cgi/oai2\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        Gson gson = new Gson();
        Configuration configType = gson.fromJson(json, Configuration.class);
        Assert.assertNotNull(configType);
        Assert.assertEquals(2, configType.getProviders().size());
    }

}
