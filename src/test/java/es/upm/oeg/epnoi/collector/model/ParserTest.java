package es.upm.oeg.epnoi.collector.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.upm.oeg.camel.euia.model.Context;
import es.upm.oeg.camel.euia.model.Publication;
import es.upm.oeg.camel.euia.model.Reference;
import es.upm.oeg.camel.euia.model.Source;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class ParserTest {

    @Test
    public void marshall(){

        Context context = new Context();

        Source source = new Source();
        source.setName("slashdot");
        source.setUri("http://www.epnoi.org/feeds/slashdot");
        source.setUrl("http://rss.slashdot.org/Slashdot/slashdot");
        source.setProtocol("oaipmh");
        context.setSource(source);

        Publication publication = new Publication();
        publication.setTitle("Interviews: Ask Stephen Wolfram a Question");
        publication.setUri("http://rss.slashdot.org/~r/Slashdot/slashdot/~3/L06b4tszG08/story01.htm");
        publication.setUrl("file://target/oaipmh/2015-3-3/slashdot/resource-1425392911570.htm");
        publication.setFormat("htm");
        publication.setLanguage("en-us");
        publication.setPublished("2015-02-17T19:23:00Z");
        publication.setRights("Copyright 1997-2015, Dice. All Rights Reserved. Slashdot is a Dice Holdings, Inc. service");

        publication.setDescription("Stephen Wolfram's accomplishments and contributions to ...");
        List<String> creators = Arrays.asList(new String[]{"samzenpus","zing, e"});
        publication.setCreators(creators);


        Reference reference = new Reference();
        reference.setFormat("xml");
        reference.setUrl("file://target/oaipmh/2015-3-3/slashdot/resource-1425392911570.xml");

        publication.setReference(reference);
        context.add(publication);


        //Gson gson = new Gson();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        String json = gson.toJson(context);

        System.out.println(json);

    }

}
