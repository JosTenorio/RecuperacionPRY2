package Controlers;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;



public class CollectionHandler {

    private static Analyzer analyzerWrapper;
    private static Directory dir;

    private static void setAnalyzerWrapper (String stopwords) throws IOException {
        analyzerWrapper = createWrapper(stopwords);
    }

    private static void createIndex (String indexPath) throws IOException {
        dir = FSDirectory.open(Paths.get(indexPath));

    }

    public static int primeCollection (String stopwordsPath, String indexPath) {
        try {
            createIndex(indexPath);
        } catch (IOException e) {
            System.out.println("\n No se ha podido crear un índice en la dirección especificada.");
            return -1;
        }
        try {
            setAnalyzerWrapper(stopwordsPath);
        } catch (IOException e) {
            System.out.println("\n No se ha podido procesar el archivo de stopwords dado, o ha fallado el analizador.");
            return -1;
        }
        return 0;
    }

    public static int insertDocument(ParsedDocument document, BigInteger docBeginning, BigInteger docEnd){
        IndexWriter writer;
        IndexWriterConfig config = new IndexWriterConfig(analyzerWrapper);
        try {
            writer = new IndexWriter(dir, config);
            addDoc(writer, document, docBeginning, docEnd);
            writer.close();
        } catch (IOException e) {
            System.out.println("\n No se ha podido acceder al índice creado para asignar un escritor.");
            return -1;
        }
        return 1;
    }

    private static void addDoc(IndexWriter w, ParsedDocument parsedDoc, BigInteger docBeginning, BigInteger docEnd) throws IOException {
        Document doc = new Document();
        doc.add(new TextField("texto", parsedDoc.text, Field.Store.YES));
        doc.add(new TextField("ref", parsedDoc.ref, Field.Store.YES));
        doc.add(new TextField("encab", parsedDoc.headers, Field.Store.YES));
        doc.add(new TextField("titulo", parsedDoc.title, Field.Store.YES));
        doc.add(new StringField("enlace",docBeginning.toString(), Field.Store.YES));
        doc.add(new StringField("beginningByte",parsedDoc.enlace.toString(), Field.Store.YES));
        doc.add(new StringField("endByte",docEnd.toString(), Field.Store.YES));
        w.addDocument(doc);
    }

    private static PerFieldAnalyzerWrapper createWrapper (String stopWordsFile) throws IOException {
        Map<String, Analyzer> analyzerMap = new HashMap<>();
        analyzerMap.put("texto", getSpanishAnalyzer(stopWordsFile, true));
        analyzerMap.put("ref", getSpanishAnalyzer(stopWordsFile, false));
        analyzerMap.put("encab", getSpanishAnalyzer(stopWordsFile, true));
        analyzerMap.put("titulo", getSpanishAnalyzer(stopWordsFile, false));
        return new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerMap);
    }

    private static Analyzer getSpanishAnalyzer (String stopWordsFile, boolean useStemmer) throws IOException {
        Analyzer analyzer;
        analyzer = useStemmer ?
                CustomAnalyzer.builder()
                        .withTokenizer("pattern", "pattern", "([A-Za-zÁÉÍÓÚÜáéíóúüÑñ]+)", "group", "0")
                        .addTokenFilter(SnowballPorterFilterFactory.class, "language", "Spanish")
                        .addTokenFilter("stop", "words", stopWordsFile)
                        .build()
                :
                CustomAnalyzer.builder()
                        .withTokenizer("pattern", "pattern", "([A-Za-zÁÉÍÓÚÜáéíóúüÑñ]+)", "group", "0")
                        .addTokenFilter("lowercase")
                        .addTokenFilter("ASCIIFolding")
                        .build();
        return analyzer;
    }

    public static void testAnalyzer (String sample, String stopWordsFile, boolean useStemmer) throws IOException {
        Analyzer analyzer = getSpanishAnalyzer(stopWordsFile, useStemmer);
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
