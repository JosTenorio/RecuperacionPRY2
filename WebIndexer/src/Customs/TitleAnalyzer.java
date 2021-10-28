package Customs;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.pattern.PatternTokenizer;
import Utils.StopWordsHandler;
import java.util.regex.Pattern;

public class TitleAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        PatternTokenizer src = new PatternTokenizer(Pattern.compile("([A-Za-zÁÉÍÓÚÜáéíóúüÑñ_]+)"),0);
        TokenStream result  = new LowerCaseFilter(src);
        result = new StopFilter(result, StopWordsHandler.loadStopwords("Stopwords/StopwordsEs.txt"));
        result = new AccentFilter(result);
        return new TokenStreamComponents(src, result);
    }
}