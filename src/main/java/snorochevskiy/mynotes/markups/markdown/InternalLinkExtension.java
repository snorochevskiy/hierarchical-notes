package snorochevskiy.mynotes.markups.markdown;


import com.vladsch.flexmark.Extension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.DataKey;
import com.vladsch.flexmark.util.options.MutableDataHolder;

public class InternalLinkExtension implements Parser.ParserExtension {

    public static final DataKey<String> INTERNAL_URL_RELATIVE_PATH = new DataKey("INTERNAL_URL_RELATIVE_PATH", "");

    private String noteRelativePath;

    public static Extension create() {
        return new InternalLinkExtension();
    }

    @Override
    public void parserOptions(MutableDataHolder mutableDataHolder) {
        this.noteRelativePath = mutableDataHolder.get(INTERNAL_URL_RELATIVE_PATH);
    }

    @Override
    public void extend(Parser.Builder builder) {
        builder.postProcessorFactory(new InternalLinkPostProcessor.Factory(noteRelativePath));
    }
}
