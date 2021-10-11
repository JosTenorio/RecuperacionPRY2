import Controlers.CollectionParser;

import java.io.InputStreamReader;
import java.util.Scanner;

public class main {
    public static void main(String[] args){
        String collectionPath = null;
        System.out.printf("Por favor ingrese la ruta de la colleción: \n >>");
        try{
            Scanner scanner = new Scanner(System.in);
            collectionPath = scanner.nextLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        CollectionParser col = new CollectionParser(collectionPath);
        col.readFile();

}
}
