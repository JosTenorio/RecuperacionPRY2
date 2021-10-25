import java.util.InputMismatchException;
import java.util.Scanner;

public class Console {

    final static Scanner ui = new Scanner(System.in);


    public static void showMainMenu() {
        int option;
        String error = null;
        while (true) {
            System.out.println("\nBienvenido al sistema de indexado de páginas web");
            System.out.println("1. Indexar colecciones");
            System.out.println("2. Realizar consultas");
            System.out.println("3. Salir del programa");
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
                    System.out.println("Terminando ejecución");
                    System.exit(0);
                }
                default -> System.out.println("Error, no se reconoce al comando: '" + Integer.toString(option) + "', favor reintentar");
            }
        }
    }

    private static void indexCollection() {
        System.out.println("\nIndexación de colecciones");
        System.out.println("0. H0");
        System.out.println("1. H0");
        System.out.println("2. H0");
        System.out.println("3. H0");
        System.out.println("4. H0");
        System.out.println("5. H0");
        System.out.println("6. H0");
        System.out.print("Favor digite el número de su selección: ");

    }

    private static void queryIndex() {
        System.out.println("Indizar colección");
    }
}