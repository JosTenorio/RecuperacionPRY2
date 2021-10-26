package Controlers;

import Models.ParsedDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HTMLHandler {

    public static ParsedDocument parseHTML (String source) {
        org.jsoup.nodes.Document document = Jsoup.parse(source);
        return new ParsedDocument(parseBody(document), parseHeader(document), parseRef(document), parseLink(document), parseTitle(document));
    }

    private static String parseHeader (org.jsoup.nodes.Document document) {
        Elements Tags = document.select("h1, h2, h3, h4, h5, h6");
        StringBuilder result = new StringBuilder();
        for(Element tag: Tags){
            result.append(tag.text()).append(" ");
        }
        return result.toString();
    }

    private static String parseBody (org.jsoup.nodes.Document document) {
        return document.body().text();
    }

    public static String parseRef (org.jsoup.nodes.Document document) {
        Elements aTag = document.select("a");
        StringBuilder result = new StringBuilder();
        for (Element tag : aTag) {
            result.append(tag.text()).append(" ");
        }
        return result.toString();
    }

    public static String parseTitle (org.jsoup.nodes.Document document) {
        Elements aTag = document.select("head title");
        StringBuilder result = new StringBuilder();
        for (Element tag : aTag) {
            result.append(tag.text()).append(" ");
        }
        return result.toString();
    }

    public static ArrayList<String> parseLink (org.jsoup.nodes.Document document) {
        ArrayList<String> result = new ArrayList<>();
        Elements aTag = document.select("a");
        Pattern beginningRef = Pattern.compile("^\\.\\.\\/\\.\\.\\/\\.\\.\\/\\.\\.\\/articles\\/");
        for (Element tag : aTag) {
            String link = tag.attr("href");
            Matcher matcher = beginningRef.matcher(link);
            if (matcher.find()) {
                int indexF = matcher.end();
                String relativeLink = link.substring(indexF);
                if (!result.contains(relativeLink))
                result.add(relativeLink);
            }
        }
        return result;
    }
}



// C:\Users\JOS\Desktop\RecuperacionPRY2\TestFiles\1Doc.txt