package Controlers;

import Models.Document;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


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
            String currentDocString = null;
            currentDocString += lineIterator.nextLine();
            int docCount = 1;
            while(lineIterator.hasNext())
            {
                String currentLine = lineIterator.nextLine();
                if(Objects.equals(currentLine, "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"))
                {
                    docCount++;
                }


            }
            System.out.println("Habían "+docCount+" páginas");
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
