package Utils;

import org.apache.lucene.analysis.CharArraySet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class StopWordsHandler {

    public static CharArraySet loadStopwords (String filepath) {
        Scanner s = null;
        try {
            s = new Scanner(new File("filepath"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ArrayList<String> list = new ArrayList<String>();
        while (s.hasNext()){
            list.add(s.next());
        }
        s.close();
        return new CharArraySet (list, true );
    }
}
