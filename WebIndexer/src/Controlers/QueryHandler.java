package Controlers;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;


public class QueryHandler {


    public static File openIndex (String indexPath) {
        File f = new File(indexPath);
        if (f.exists() && f.isDirectory()) {
            return f;
        }
        return null;
    }
    public static IndexReader getIndexReader(String indexPath){
        IndexReader reader = null;
        try{
            File index = openIndex(indexPath);
            if (index == null) {
                System.out.println("\n No se ha podido abrir la colección indicada o esta no existe.");
                return null;
            }
            Directory directory = FSDirectory.open(index.toPath());
            reader  = DirectoryReader.open(directory);
            return reader;
        } catch (IOException e) {
            System.out.println("Invalid directory");
            e.printStackTrace();
            return null;
        }
    }
    public static boolean searchQuery(String query,String indexPath)  {
        try {
            IndexReader reader = getIndexReader(indexPath);
            if (reader == null) {
                System.out.println("Hubo un error cargando el índice por favor inténtelo de nuevo");
                return false;
            }
            IndexSearcher searcher = new IndexSearcher(reader);
            CollectionHandler.setAnalyzerWrapper("Stopwords/Stopwords.txt", true);
            QueryParser parser = new QueryParser("titulo", CollectionHandler.getAnalyzerWrapper());
            Query queryObject = parser.parse(query);
            System.out.println(queryObject);
            ScoreDoc[] hits = searcher.search(queryObject,10).scoreDocs;
            System.out.println(hits.length);
            return true;
        }
        catch (ParseException exception){
            System.out.println("Error en el parseo de la consulta");
            return false;
        }
        catch (IOException exception){
            System.out.println("Error al momento de cargar el índice");
            return false;
        }

    }

}
