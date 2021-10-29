import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import Controlers.*;
import Utils.StopWordsHandler;

public class Console {

    final static Scanner ui = new Scanner(System.in);

    public static void showMainMenu() {
        int option;
        String error = null;
        while (true) {
            System.out.println("---------------------------------------------------------");
            System.out.println("Bienvenido al sistema de indexado de páginas web");
            System.out.println("1. Indexar colecciones");
            System.out.println("2. Realizar consultas");
            System.out.println("3. Salir del programa");
            System.out.println("---------------------------------------------------------");
            System.out.print("Favor digite el número de su selección: ");
            try {
                option = ui.nextInt();
            } catch (InputMismatchException exception) {
                option = -1;
                error = ui.next();
            }
            if (error != null) {
                System.out.println("Error, no se reconoce al comando: '" + error + "', favor reintentar");
                error = null;
                continue;
            }
            switch (option) {
                case 1 -> indexCollection();
                case 2 -> queryIndex();
                case 3 -> {
                    System.out.println("Terminando ejecución...");
                    System.exit(0);
                }
                default -> System.out.println("Error, no se reconoce al comando: '" + option + "', favor reintentar");
            }
        }
    }

    private static void indexCollection() {
        Scanner scanner = new Scanner(System.in);
        String stemmingOption;
        System.out.println("->Indexación de colecciones\n");
        System.out.print("Favor ingresar la dirección de la colección a indizar: ");
        System.out.flush();
        String collectionPath = scanner.nextLine();
        System.out.print("Favor ingresar la dirección del archivo de stopwords a usar: ");
        System.out.flush();
        String stopwordsPath = scanner.nextLine();
        System.out.print("Favor ingresar la dirección del directorio en el cuál se almacenara el índice creado: ");
        System.out.flush();
        String indexPath = scanner.nextLine();
        System.out.print("Se debe aplicar stemming durante la indixación? (Y/N): ");
        System.out.flush();
        stemmingOption = scanner.nextLine();
        while (!(stemmingOption.equals("Y") || stemmingOption.equals("y")  || stemmingOption.equals("N") || stemmingOption.equals("n"))){
            System.out.print("Se debe aplicar stemming durante la indixación? (Y/N): ");
            System.out.flush();
            stemmingOption = scanner.nextLine();
        }
        CollectionParser.indexCollection(collectionPath, stopwordsPath, indexPath, (stemmingOption.equals("Y") || stemmingOption.equals("y")));

    }

    private static void queryIndex() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("->Consultas en colección:  \n");
        System.out.println("Por favor ingrese la dirección del índice a utilizar:  ");
        System.out.flush();
        String indexPath = scanner.nextLine();
        System.out.println("Por favor ingrese la consulta a realizar:  ");
        String query = scanner.nextLine();
        QueryHandler.searchQuery(query,indexPath);

    }

}

