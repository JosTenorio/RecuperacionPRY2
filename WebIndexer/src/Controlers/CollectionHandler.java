package Controlers;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Customs.AccentFilterFactory;
import Models.ParsedDocument;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class CollectionHandler {

    private static Analyzer analyzerWrapper;
    private static Directory dir;
    private static IndexWriter writer;

    public static void setAnalyzerWrapper (String stopwords, boolean useStemmer) throws IOException {
        analyzerWrapper = createWrapper(stopwords, useStemmer);
    }

    public static Analyzer getAnalyzerWrapper(){
        return analyzerWrapper;
    }

    private static void createIndex (String indexPath) throws IOException {
        dir = FSDirectory.open(Paths.get(indexPath));
    }

    public static void closeWriter () throws IOException {
        if ((writer != null) && (writer.isOpen())) {
            writer.close();
        }
    }

    public static void setAndOpenWriter (boolean recreateIndex) throws IOException {
        IndexWriterConfig config = new IndexWriterConfig(analyzerWrapper);
        if (recreateIndex) {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        }
        else {
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        }
        writer = new IndexWriter(dir, config);
    }

    public static int primeCollection (String stopwordsPath, String collectionPath, String indexPath, boolean useStemmer,
                                       boolean recreateIndex)
    {
        try {
            createIndex(indexPath);
        } catch (IOException e) {
            System.out.println("\n No se ha podido crear un índice en la dirección especificada.");
            return -1;
        }
        try {
            setAnalyzerWrapper(stopwordsPath, useStemmer);
            setAndOpenWriter(recreateIndex);
            saveIndexConfig(collectionPath, useStemmer,indexPath);
        } catch (IOException e) {
           System.out.println("\n No se ha podido procesar el archivo de stopwords dado o el directorio de destino.");
           e.printStackTrace();
           return -1;
        }
        return 0;
    }

    private static void saveIndexConfig (String collectionPath, boolean usesStemmer, String indexPath) throws IOException {
        BufferedWriter out;
        FileWriter fstream = new FileWriter(indexPath.concat("\\CustomConfig.txt"), false);
        out = new BufferedWriter(fstream);
        out.write(collectionPath.concat("\n"));
        out.write(usesStemmer ? "true" : "false");
        out.close();
    }

    public static int insertDocument(ParsedDocument document, Long docBeginning, Long docEnd){
        try {
            if (writer.isOpen()){
                addDoc(writer, document, docBeginning, docEnd);
            } else {
                System.out.println("\n El escritor creado se ha cerrrado antes de poder indexar un documento.");
                return -1;
            }
        } catch (IOException e) {
            System.out.println("\n No se ha podido acceder al índice creado para asignar un escritor.");
            return -1;
        }
        return 1;
    }

    private static void addDoc(IndexWriter w, ParsedDocument parsedDoc, Long docBeginning, Long docEnd) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("texto", parsedDoc.text, Field.Store.YES));
        doc.add(new TextField("ref", parsedDoc.ref, Field.Store.YES));
        doc.add(new TextField("encab", parsedDoc.headers, Field.Store.YES));
        doc.add(new TextField("titulo", parsedDoc.title, Field.Store.YES));
        int i = 0;
        for (String link : parsedDoc.enlace) {
            doc.add(new StringField("enlace".concat(Integer.toString(i)), link, Field.Store.YES));
            i++;
        }
        doc.add(new StringField("beginningByte", docBeginning.toString(), Field.Store.YES));
        doc.add(new StringField("endByte", docEnd.toString(), Field.Store.YES));
        w.addDocument(doc);
    }

    private static PerFieldAnalyzerWrapper createWrapper (String stopWordsFile, boolean useStemmer) throws IOException {
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put("texto", getBodyAnalyzer(stopWordsFile, useStemmer));
        analyzerMap.put("ref", getTitleAnalyzer(stopWordsFile));
        analyzerMap.put("encab", getBodyAnalyzer(stopWordsFile, useStemmer));
        analyzerMap.put("titulo", getTitleAnalyzer(stopWordsFile));
        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
    }

    private static Analyzer getBodyAnalyzer (String stopWordsFile, boolean useStemmer) throws IOException {
        Map<String, String> accentFilterParams = new HashMap<>();
        Analyzer analyzer;
        analyzer = useStemmer ?
                CustomAnalyzer.builder()
                        .withTokenizer("pattern", "pattern", "([A-Za-zÁÉÍÓÚÜáéíóúüÑñ_]+)", "group", "0")
                        .addTokenFilter("lowercase")
                        .addTokenFilter("stop", "words", stopWordsFile)
                        .addTokenFilter(SnowballPorterFilterFactory.class, "language", "Spanish")
                        .build()
                :
                CustomAnalyzer.builder()
                        .withTokenizer("pattern", "pattern", "([A-Za-zÁÉÍÓÚÜáéíóúüÑñ_]+)", "group", "0")
                        .addTokenFilter("lowercase")
                        .addTokenFilter("stop", "words", stopWordsFile)
                        .addTokenFilter(AccentFilterFactory.class, accentFilterParams)
                        .build();
        return analyzer;
    }


    private static Analyzer getTitleAnalyzer (String stopWordsFile) throws IOException {
        Map<String, String> accentFilterParams = new HashMap<>();
        Analyzer analyzer;
        analyzer =
                CustomAnalyzer.builder()
                        .withTokenizer("pattern", "pattern", "([A-Za-zÁÉÍÓÚÜáéíóúüÑñ_]+)", "group", "0")
                        .addTokenFilter("lowercase")
                        .addTokenFilter(AccentFilterFactory.class, accentFilterParams)
                        .addTokenFilter("stop", "words", stopWordsFile)
                        .build();
        return analyzer;
    }

    public static void testAnalyzer (String sample, String stopWordsFile) throws IOException {
        Analyzer analyzer = getBodyAnalyzer(stopWordsFile, true);
        List<String> result = new ArrayList<>();
        try {
            TokenStream stream  = analyzer.tokenStream(null, new StringReader(sample));
            stream.reset();
            while (stream.incrementToken()) {
                result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(result);
    }
}
