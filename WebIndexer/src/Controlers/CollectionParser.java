package Controlers;

import Models.ParsedDocument;
import Utils.StopWordsHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.lucene.store.Directory;


import java.io.*;
import java.lang.reflect.Field;
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
        try {
            StopWordsHandler.loadStopwords(stopwordsPath, stopwordsDeposit);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if (CollectionHandler.primeCollection(stopwordsDeposit, collectionPath, indexPath, useStemmer, true) < 0) {
            return;
        }

        try(RandomAccessFile randomAccessFile = new RandomAccessFile(collectionPath, "r");)
        {

            BufferedReader myBufferedReader    = new BufferedReader(new FileReader(randomAccessFile.getFD()));
            String currentDocString = "";
            long currentOffset = 0;
            long previousOffset = -1;
            Long documentStart = 0L;
            for(String currentLine="";currentLine!=null;currentLine=myBufferedReader.readLine())
            {

                if(patDoctype.matcher(currentLine).matches())
                {
                    // If we find a possible html start point we save that byte count to index later
                    documentStart = calcPosition(myBufferedReader,randomAccessFile,currentOffset,previousOffset);

                }
                else if(patHtml.matcher(currentLine).matches())
                {
                    // If we find an opening html tag then we need to parse all the content into a single string to open the document in jsoup
                    StringBuilder documentSource = new StringBuilder();
                    documentSource = new StringBuilder(documentSource.toString().concat(currentLine));
                    while(!pathEndHtml.matcher((currentLine)).matches())
                    {
                        // Gets next line and adds it to the source string
                        currentLine = myBufferedReader.readLine();
                        if(currentLine==null){
                            return;
                        }
                        documentSource.append(currentLine);
                    }
                    //End of the doc
                    Long documentEnd = calcPosition(myBufferedReader,randomAccessFile,currentOffset,previousOffset);
                    ParsedDocument parsedDoc = HTMLHandler.parseHTML(documentSource.toString());
                    if (CollectionHandler.insertDocument(parsedDoc, documentStart, documentEnd ) < 0) {
                        return;
                    }
                }
            }
            CollectionHandler.closeWriter();
            try {
                StopWordsHandler.saveStopwords(stopwordsDeposit, indexPath);
            } catch (IOException e) {
                System.out.println("\n No se ha podido guardar las stopwords usadas en el índice creado");
                return;

            }
        } catch (IOException e)
        {   e.printStackTrace();
            System.out.println("\n Error en la lectura del archivo fuente durante la indexación");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static long calcPosition(BufferedReader bufferedReader,RandomAccessFile randomAccessFile,long currentOffset, long previousOffset) throws NoSuchFieldException, IllegalAccessException, IOException {
        long fileOffset = randomAccessFile.getFilePointer();
        if (fileOffset != previousOffset) {
            if (previousOffset != -1) {
                currentOffset = previousOffset;
            }
            previousOffset = fileOffset;
        }
        int bufferOffset = getOffset(bufferedReader);
        long realPosition = currentOffset + bufferOffset;
        return realPosition;
    }
    private static int getOffset(BufferedReader bufferedReader) throws NoSuchFieldException, IllegalAccessException {
        Field field = BufferedReader.class.getDeclaredField("nextChar");
        int result = 0;
        try {
            result = (Integer) field.get(bufferedReader);
        } finally {

        }
        return result;
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
