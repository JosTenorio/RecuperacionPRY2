package Controlers;

import Models.ParsedDocument;
import Utils.StopWordsHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.store.Directory;


import java.io.*;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;


public class CollectionParser {

    public static final String stopwordsDeposit = "Stopwords.txt";
    private static final Pattern pathEndHtml = Pattern.compile("</html.*?>");
    private static final Pattern patHtml = Pattern.compile("<html.*?>");
    private static final Pattern patDoctype = Pattern.compile(".*?<!DOCTYPE.*?>");

    public static void indexCollection(String collectionPath, String stopwordsPath, String indexPath, boolean useStemmer)
    {
        File collection = openCollection(collectionPath);
        if (collection == null) {
            return;
        }
        if (CollectionHandler.primeCollection(stopwordsDeposit, collectionPath, indexPath, useStemmer, true) < 0) {
            return;
        }
        try {
            StopWordsHandler.loadStopwords(stopwordsPath, stopwordsDeposit);
        } catch (IOException e) {
            System.out.println("No ha sido posible cargar el archivo de stopwords");
            return;
        }

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(collectionPath, "r");)
        {
//            InputStreamReader myInputStreamReader = new InputStreamReader(myInputStream);
//            BufferedReader myBufferedReader    = new BufferedReader(myInputStreamReader);
            //FileChannel myFileChannel = myInputStream.getChannel();
            String currentDocString = "";


            //currentDocString += randomAccessFile.readLine();
            int docCount = 0;
            BigInteger byteCount = BigInteger.valueOf((Integer)(currentDocString.getBytes(StandardCharsets.UTF_8).length));
            Pattern pathEndHtml = Pattern.compile("</html.*?>");
            Pattern patHtml = Pattern.compile("<html.*?>");
            Pattern patDoctype = Pattern.compile(".*?<!DOCTYPE.*?>");
            BigInteger documentStart = BigInteger.valueOf((Integer) 0);
            for(String currentLine="";currentLine!=null;currentLine=randomAccessFile.readLine())
            {

                BigInteger linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                byteCount = byteCount.add(linebytesize.add(BigInteger.valueOf(1)));
                if(patDoctype.matcher(currentLine).matches())
                {
                    // If we find a possible html start point we save that byte count to index later
//                    System.out.println(", Position : "+randomAccessFile.getFilePointer());
//                    System.out.println(currentLine);
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
                        currentLine = randomAccessFile.readLine();
                        if(currentLine==null){
                            return;
                        }
                        documentSource+=currentLine;
                        linebytesize =  BigInteger.valueOf((Integer)(currentLine.getBytes(StandardCharsets.UTF_8).length));
                        byteCount = byteCount.add(linebytesize.add(BigInteger.valueOf(1)));
                    }
                    //System.out.println(currentLine);
                    //End of the doc
                    BigInteger documentEnd = byteCount;

                    ParsedDocument parsedDoc = HTMLHandler.parseHTML(documentSource);
                    if (CollectionHandler.insertDocument(parsedDoc, documentStart, documentEnd ) < 0) {
                        return;
                    }
                    docCount++;
                }
            }
            System.out.println(docCount);
            CollectionHandler.closeWriter();
        } catch (IOException e)
        {   e.printStackTrace();
            System.out.println("\n Error en la lectura del archivo fuente durante la indexación");
        }
    }

    public static File openCollection (String collectionPath) {
        File f = new File(collectionPath);
        if (f.exists() && !f.isDirectory()) {
            return f;
        }
        System.out.println("\n No se ha podido abrir la colección indicada o esta no existe.");
        return null;
    }
}
