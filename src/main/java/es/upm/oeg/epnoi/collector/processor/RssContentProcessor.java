package es.upm.oeg.epnoi.collector.processor;

import es.upm.oeg.epnoi.collector.model.Context;
import es.upm.oeg.epnoi.collector.model.Item;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cbadenes on 18/02/15.
 */

@Component
public class RSSContentProcessor implements Processor{


    Logger log = LoggerFactory.getLogger(RSSContentProcessor.class);

    private static final String[] stopWords = { "a", "about", "above", "above",
            "across", "after", "afterwards", "again", "against", "all",
            "almost", "alone", "along", "already", "also", "although",
            "always", "am", "among", "amongst", "amoungst", "amount", "an",
            "and", "another", "any", "anyhow", "anyone", "anything", "anyway",
            "anywhere", "are", "around", "as", "at", "back", "be", "became",
            "because", "become", "becomes", "becoming", "been", "before",
            "beforehand", "behind", "being", "below", "beside", "besides",
            "between", "beyond", "bill", "both", "bottom", "but", "by", "call",
            "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry",
            "de", "describe", "detail", "do", "done", "down", "due", "during",
            "each", "eg", "eight", "either", "eleven", "else", "elsewhere",
            "empty", "enough", "etc", "even", "ever", "every", "everyone",
            "everything", "everywhere", "except", "few", "fifteen", "fify",
            "fill", "find", "fire", "first", "five", "for", "former",
            "formerly", "forty", "found", "four", "from", "front", "full",
            "further", "get", "give", "go", "had", "has", "hasnt", "have",
            "he", "hence", "her", "here", "hereafter", "hereby", "herein",
            "hereupon", "hers", "herself", "him", "himself", "his", "how",
            "however", "hundred", "ie", "if", "in", "inc", "indeed",
            "interest", "into", "is", "it", "its", "itself", "keep", "last",
            "latter", "latterly", "least", "less", "ltd", "made", "many",
            "may", "me", "meanwhile", "might", "mill", "mine", "more",
            "moreover", "most", "mostly", "move", "much", "must", "my",
            "myself", "name", "namely", "neither", "never", "nevertheless",
            "next", "nine", "no", "nobody", "none", "noone", "nor", "not",
            "nothing", "now", "nowhere", "of", "off", "often", "on", "once",
            "one", "only", "onto", "or", "other", "others", "otherwise", "our",
            "ours", "ourselves", "out", "over", "own", "part", "per",
            "perhaps", "please", "put", "rather", "re", "same", "see", "seem",
            "seemed", "seeming", "seems", "serious", "several", "she",
            "should", "show", "side", "since", "sincere", "six", "sixty", "so",
            "some", "somehow", "someone", "something", "sometime", "sometimes",
            "somewhere", "still", "such", "system", "take", "ten", "than",
            "that", "the", "their", "them", "themselves", "then", "thence",
            "there", "thereafter", "thereby", "therefore", "therein",
            "thereupon", "these", "they", "thickv", "thin", "third", "this",
            "those", "though", "three", "through", "throughout", "thru",
            "thus", "to", "together", "too", "top", "toward", "towards",
            "twelve", "twenty", "two", "un", "under", "until", "up", "upon",
            "us", "very", "via", "was", "we", "well", "were", "what",
            "whatever", "when", "whence", "whenever", "where", "whereafter",
            "whereas", "whereby", "wherein", "whereupon", "wherever",
            "whether", "which", "while", "whither", "who", "whoever", "whole",
            "whom", "whose", "why", "will", "with", "within", "without",
            "would", "yet", "you", "your", "yours", "yourself", "yourselves",
            "the" };

    private static final List<String> stopWordsList = Arrays.asList(stopWords);

    private static final int MAX_TOKEN_LENGTH = 12;

    private static final int MIN_TOKEN_LENGTH = 2;

    @Override
    public void process(Exchange exchange) throws Exception {

        String content = exchange.getIn().getBody(String.class);

        log.info("Processing content: {}", content);

        Context feedContext = new Context();

        String itemURI = exchange.getIn().getHeader(Item.HEADER.URI, String.class);

        feedContext.getElements().put(itemURI, content);

        // Send to core by



        // change the existing message to say Hello
        exchange.getIn().setBody("Hello ");
    }


//    private void toCore(){
//        //
////        if (this.harvester.getCore() != null) {
////
////            InformationSource informationSource = (InformationSource) this.harvester
////                    .getCore()
////                    .getInformationHandler()
////                    .get(this.manifest.getURI(),
////                            InformationSourceRDFHelper.INFORMATION_SOURCE_CLASS);
////            feedContext.getParameters().put(
////                    Context.INFORMATION_SOURCE_NAME,
////                    informationSource.getName());
////            feedContext.getParameters().put(
////                    Context.INFORMATION_SOURCE_URI, manifest.getURI());
////
////            this.harvester.getCore().getInformationHandler()
////                    .put(feed, feedContext);
////        } else {
////
////            System.out.println("Result: Feed> " + feed);
////            for (Item item : feed.getItems()) {
////                System.out.println("		 Item> " + item);
////            }
////
////            System.out.println("Result: Context> " + feedContext);
////        }
//    }


}
