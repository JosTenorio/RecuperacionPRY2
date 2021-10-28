import java.io.IOException;
import Controlers.CollectionHandler;
import Controlers.CollectionParser;

public class main {
    public static void main(String[] args) throws IOException {
        //Console.showMainMenu();
        CollectionParser.indexCollection("D:\\Universidad\\RecuperacionPRY2\\testFiles\\magnoel.txt","stopwords/stopwords.txt","a",true);
    }
}
