package Customs;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.util.TokenFilterFactory;

import java.util.Map;


public class AccentFilterFactory extends TokenFilterFactory {

    protected AccentFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream ts) {
        return new AccentFilter(ts);
    }
}