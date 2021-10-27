package Controlers;

import Models.ParsedDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.store.Directory;


import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


public class CollectionParser {

    public static File openCollection (String collectionPath) {
        File f = new File(collectionPath);
        if (f.exists() && !f.isDirectory()) {
            return f;
        }
        return null;
    }

    public static void indexCollection(String collectionPath, String stopwordsPath, String indexPath)
    {
        File collection = openCollection(collectionPath);
        if (collection == null) {
            System.out.println("\n No se ha podido abrir la colección indicada o esta no existe.");
            return;
        }
        if (CollectionHandler.primeCollection(stopwordsPath, indexPath) < 0) {
            return;
        }
        try( LineIterator lineIterator = FileUtils.lineIterator(collection,"UTF-8"))
        {
            String currentDocString = "";
            currentDocString += lineIterator.nextLine();
            int docCount = 0;
            BigInteger byteCount = BigInteger.valueOf((Integer)(currentDocString.getBytes(StandardCharsets.UTF_8).length));
            Pattern pathEndHtml = Pattern.compile("</html.*?>");
            Pattern patHtml = Pattern.compile("<html.*?>");
            Pattern patDoctype = Pattern.compile(".*?<!DOCTYPE.*?>");
            while(lineIterator.hasNext())
            {
                BigInteger documentStart = BigInteger.valueOf((Integer) 0);
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
                    documentSource = documentSource.concat(currentLine);
                    while(!pathEndHtml.matcher((currentLine)).matches())
                    {
                        // Gets next line and adds it to the source string
                        if(!lineIterator.hasNext())
                        {
                            return;
                        }
                        currentLine = lineIterator.nextLine();
                        documentSource+=currentLine;
                        linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                        byteCount = byteCount.add(linebytesize);
                    }
                    //End of the doc
                    BigInteger documentEnd = byteCount;

                    ParsedDocument parsedDoc = HTMLHandler.parseHTML(documentSource);
                    if (CollectionHandler.insertDocument(parsedDoc, documentStart, documentEnd ) < 0) {
                        return;
                    }

                    docCount++;
                }
            }
        } catch (IOException e)
        {
            System.out.println("\n Error en la lectura del archivo fuente durante la indexación");
            return;
        }
    }
}
