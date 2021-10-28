package Customs;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import java.util.Map;

public class AccentFilterFactory extends LowerCaseFilterFactory {

    public AccentFilterFactory(Map<String, String> args) {
        super(args);
    }

    @Override
    public TokenStream create(TokenStream ts) {
        return new AccentFilter(ts);
    }
}