package Customs;

import java.io.IOException;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class AccentFilter extends TokenFilter {
    private CharTermAttribute charTermAttr;
    private static final String accentArray = "ÁÉÍÓÚÜáéíóúü";
    private static final String nonAccentArray = "AEIOUUaeiouu";

    protected AccentFilter(TokenStream ts) {
        super(ts);
        this.charTermAttr = addAttribute(CharTermAttribute.class);
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (!input.incrementToken()) {
            return false;
        }
        int length = charTermAttr.length();
        char[] buffer = charTermAttr.buffer();
        char[] newBuffer = new char[length];
        for (int i = 0; i < length; i++)
            newBuffer[i] = accentArray.contains("" + buffer[i]) ? nonAccentArray.charAt(accentArray.indexOf("" + buffer[i])) : buffer[i];
        charTermAttr.setEmpty();
        charTermAttr.copyBuffer(newBuffer, 0, newBuffer.length);
        return true;
    }

}
