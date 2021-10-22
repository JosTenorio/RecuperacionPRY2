package Controlers;

import Models.Document;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;
import org.jsoup.select.Elements;
import org.jsoup.nodes.Element;


public class CollectionParser {
    String collectionPath;
    ArrayList<Document> documents = new ArrayList<Document>();

    public CollectionParser(String collectionPath) {
        this.collectionPath = collectionPath;
    }
    public void readFile()
    {
        File collection = new File(this.collectionPath);
        try( LineIterator lineIterator = FileUtils.lineIterator(collection,"UTF-8"))
        {
            org.jsoup.nodes.Document doc = null;
            String currentDocString = "";
            currentDocString += lineIterator.nextLine();
            int docCount = 0;
            BigInteger byteCount = BigInteger.valueOf((Integer)(currentDocString.getBytes(StandardCharsets.UTF_8).length));
            Pattern pathEndHtml =Pattern.compile("</html.*?>");
            Pattern patHtml = Pattern.compile("<html.*?>");
            Pattern patDoctype = Pattern.compile(".*?<!DOCTYPE.*?>");
            while(lineIterator.hasNext())
            {
                BigInteger documentStart;
                String currentLine = lineIterator.nextLine();
                BigInteger linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                byteCount = byteCount.add(linebytesize);
                if(patDoctype.matcher(currentLine).matches())
                {
                    // If we find a possible html start point we save that byte count to index later
                    documentStart = byteCount;
                }
                else if(patHtml.matcher(currentLine).matches())
                {

                    // If we find an opening html tag then we need to parse all the content into a single string to open the document in jsoup
                    String documentSource = "";
                    documentSource+=currentLine;
                    while(!pathEndHtml.matcher((currentLine)).matches())
                    {
                        // Gets next line and adds it to the source string
                        currentLine = lineIterator.nextLine();
                        documentSource+=currentLine;
                        linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                        byteCount = byteCount.add(linebytesize);
                    }
                    //End of the doc
                    BigInteger documentEnd = byteCount;
                    org.jsoup.nodes.Document document = Jsoup.parse(documentSource);

                    Elements Tags = document.select("h1, h2, h3, h4, h5, h6");
                    for(Element tag: Tags){
                        System.out.println(tag.text());
                    }
                    docCount++;

                }


            }
            System.out.println(byteCount);
            System.out.println("Habían "+docCount+" páginas");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
