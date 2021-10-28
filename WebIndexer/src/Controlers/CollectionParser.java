package Controlers;

import Models.ParsedDocument;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;


import java.io.*;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
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

    public static void indexCollection(String collectionPath, String stopwordsPath, String indexPath, boolean useStemmer)
    {
        File collection = openCollection(collectionPath);
        if (collection == null) {
            System.out.println("\n No se ha podido abrir la colección indicada o esta no existe.");
            return;
        }
//        if (CollectionHandler.primeCollection(stopwordsPath, indexPath, useStemmer, true ) < 0) {
//            return;
//        }

        try(FileInputStream myInputStream       = new FileInputStream(collection);)
        {
            InputStreamReader myInputStreamReader = new InputStreamReader(myInputStream);
            BufferedReader myBufferedReader    = new BufferedReader(myInputStreamReader);
            FileChannel myFileChannel = myInputStream.getChannel();
            String currentDocString = "";


            currentDocString += myBufferedReader.readLine();
            int docCount = 0;
            BigInteger byteCount = BigInteger.valueOf((Integer)(currentDocString.getBytes(StandardCharsets.UTF_8).length));
            Pattern pathEndHtml = Pattern.compile("</html.*?>");
            Pattern patHtml = Pattern.compile("<html.*?>");
            Pattern patDoctype = Pattern.compile(".*?<!DOCTYPE.*?>");
            BigInteger documentStart = BigInteger.valueOf((Integer) 0);

            for(String currentLine="";currentLine!=null;currentLine=myBufferedReader.readLine())
            {

                BigInteger linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                byteCount = byteCount.add(linebytesize.add(BigInteger.valueOf(1)));
                if(patDoctype.matcher(currentLine).matches())
                {
                    // If we find a possible html start point we save that byte count to index later
                    documentStart = byteCount;
                }
                else if(patHtml.matcher(currentLine).matches())
                {

                    // If we find an opening html tag then we need to parse all the content into a single string to open the document in jsoup
                    long position = myFileChannel.position();
                    String documentSource = "";
                    documentSource = documentSource.concat(currentLine);
                    while(!pathEndHtml.matcher((currentLine)).matches())
                    {
                        // Gets next line and adds it to the source string
                        currentLine = myBufferedReader.readLine();
                        if(currentLine==null){
                            return;
                        }
                        documentSource+=currentLine;
                        linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                        byteCount = byteCount.add(linebytesize.add(BigInteger.valueOf(1)));
                    }
                    //End of the doc
                    BigInteger documentEnd = byteCount;
                    System.out.println(position);
                    ParsedDocument parsedDoc = HTMLHandler.parseHTML(documentSource);
//                    if (CollectionHandler.insertDocument(parsedDoc, documentStart, documentEnd ) < 0) {
//                        return;
//                    }
                    docCount++;
                }
            }
            System.out.println(docCount);
            CollectionHandler.closeWriter();
        } catch (IOException e)
        {
            System.out.println("\n Error en la lectura del archivo fuente durante la indexación");
            return;
        }
    }

}
