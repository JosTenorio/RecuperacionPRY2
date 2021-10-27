package Controlers;

import org.apache.lucene.document.Document;
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
import java.io.FileInputStream;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;


public class QueryHandler {
    // Returns a directory given a path
    public static File openIndex (String indexPath) {
        File f = new File(indexPath);
        if (f.exists() && f.isDirectory()) {
            return f;
        }
        return null;
    }
    // Returns an indexReader based on a index directory
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
    // Searches a given query in the specified index
    public static void searchQuery(String query,String indexPath)  {
        try {
            IndexReader reader = getIndexReader(indexPath);
            if (reader == null) {
                System.out.println("Hubo un error cargando el índice por favor inténtelo de nuevo");
                return;
            }
            IndexSearcher searcher = new IndexSearcher(reader);
            // Stopword load
            CollectionHandler.setAnalyzerWrapper("Stopwords/Stopwords.txt", true);
            // Custom analyzer setup
            QueryParser parser = new QueryParser("titulo", CollectionHandler.getAnalyzerWrapper());
            Query queryObject = parser.parse(query);
            // We obtain number of hits to optimize memory usage by creating an optimal array
            long hits = searcher.search(queryObject,1).totalHits.value;
            if(hits<1){
                System.out.println("No se encontraron coincidencias");
                return;
            }
            showResults(searcher.search(queryObject, (int) hits).scoreDocs,searcher);
        }
        catch (ParseException exception){
            System.out.println("Error en el parseo de la consulta");
        }
        catch (IOException exception){
            System.out.println("Error al momento de cargar el índice");
        }
    }
    private static void showResults(ScoreDoc[] result,IndexSearcher searcher) throws IOException {
        Scanner scanner = new Scanner(System.in);
        String option = null;
        clearScreen();
        System.out.println("-------- Resultados --------");
        Document doc;
        // Arraylist containing the last 20 documents shown to the user
        ArrayList<Document> last20Docs = new ArrayList<Document>();
        for(int i =1;i<= result.length;i++){
            // Stops to ask for user input
            if(i%20==0 && i!=0){
                doc = searcher.doc(result[i-1].doc);
                last20Docs.add(doc);
                System.out.println("["+i+"]"+"Título del documento:  "+doc.get("titulo"));
                System.out.println("-----Menú de opciones-----\n1 --> Ver los siguiente 20 resultados\n2 --> Ver los anteriores 20 resultados\n3 " +
                        "--> Obtener el documento de un resultado\n4 --> Obtener los enlaces de un documento\nPara salir presiona cualquier otra tecla");
                System.out.flush();
                option = scanner.nextLine();
                switch (option) {
                    // Continues with the next 20 results
                    case "1" -> {i++;}
                    // Shows the past 20 results if possible
                    case "2" -> {
                        if(i==20){i=1;}
                        else {i-=39;}
                    }
                    // Opens a given document in a web browser
                    case "3" ->{
                        clearScreen();
                        Document selectedDoc = selectDoc(last20Docs);
                        if(selectedDoc==null){
                            System.out.println("Error al elegir el documento");
                            return;
                        }
                        // Opens the selected document in browser
                        //TODO: FIX HARDCODED COLLECTION FILE
                        openInBrowser(selectedDoc,"D:\\Universidad\\Colecciones\\h8.txt");
                        scanner.nextLine();
                        // Set counters as they were before
                        if(i==20){i=1;}
                        else {i-=39;}
                    }
                    // Get all the links in a given document
                    case "4" ->{}
                    default -> {
                        System.out.println("Terminando consulta");
                        return;
                    }
                }
                last20Docs.clear();
                clearScreen();
                System.out.println("-------- Resultados --------");
            }
            doc = searcher.doc(result[i-1].doc);
            last20Docs.add(doc);
            System.out.println("["+i+"]"+"Título del documento:  "+doc.get("titulo"));
        }
        // Menu in case of less than 20 documents
        if(result.length<20){
            while(true) {
                clearScreen();
                System.out.println("-----Menú de opciones-----\n1 --> Obtener el documento de un resultado\n2 --> Obtener los enlaces de un documento\n3 ");
                option = scanner.nextLine();
                switch (option) {
                    // Selects document to show
                    case "1" -> {
                        Document selectedDoc = selectDoc(last20Docs);
                        if (selectedDoc == null) {
                            System.out.println("Error al elegir el do cumento");
                            return;
                        }
                        // Opens the selected document in browser
                        //TODO: FIX HARDCODED COLLECTION FILE
                        openInBrowser(selectedDoc, "D:\\Universidad\\Colecciones\\h8.txt");
                        scanner.nextLine();
                    }
                    // Gets links of a document
                    case "2" -> {
                        }
                    default -> {
                        System.out.println("Terminando consulta");
                        return;
                    }
                }
            }
        }
    }
    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println("\n");
        }
    }
    public static Document selectDoc(ArrayList<Document> docs){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Por favor seleccione mediante el número entre [ ] el documento que desea abrir");
        for(int o=0;o<docs.size();o++){
            System.out.println("["+o+"]" +docs.get(o).get("titulo"));
        }
        System.out.println("Opcion: 3");
        String doc = scanner.nextLine();
        try{
            int docId = Integer.parseInt(doc);
            // If not in range throws an error catched below
            if(docId>-1 && docId<docs.size()){
                return docs.get(docId);
            }
            else {
                throw new Exception("Something bad happened.");
            }
        }
        // Continues to ask for a valid input until a valid input is received
        catch (Exception e)
        {
            clearScreen();
            System.out.println("Asegúrese de digitar un número entre 0 y 19");
            selectDoc(docs);
        }
        return null;
    }
    public static void openInBrowser(Document doc,String collection) throws IOException {
        int beginningByte = Integer.parseInt(doc.get("beginningByte"));
        int endByte = Integer.parseInt(doc.get("endByte"));
        FileInputStream fis = new FileInputStream(collection);

        ByteBuffer bytes = ByteBuffer.allocate(endByte-beginningByte);
        fis.getChannel().read(bytes, beginningByte);

        byte[] readBytes = bytes.array();
        String htmlPage = new String(readBytes, StandardCharsets.UTF_8);
        System.out.println(htmlPage);
    }
}