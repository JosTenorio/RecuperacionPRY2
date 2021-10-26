package Models;

import java.util.ArrayList;

public class ParsedDocument {
    public String text;
    public String headers;
    public String ref;
    public ArrayList<String> enlace;
    public String title;

    public ParsedDocument (String text, String headers, String ref, ArrayList<String> enlace, String title) {
        this.text = text;
        this.headers = headers;
        this.ref = ref;
        this.enlace = enlace;
        this.title = title;
    }

}

