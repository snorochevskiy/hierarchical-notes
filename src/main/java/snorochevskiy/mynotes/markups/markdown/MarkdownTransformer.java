package snorochevskiy.mynotes.markups.markdown;


import com.vladsch.flexmark.ast.Document;
import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.abbreviation.AbbreviationExtension;
import com.vladsch.flexmark.ext.definition.DefinitionExtension;
import com.vladsch.flexmark.ext.footnotes.FootnoteExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.typographic.TypographicExtension;
import com.vladsch.flexmark.ext.wikilink.WikiImage;
import com.vladsch.flexmark.ext.wikilink.WikiLink;
import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.parser.ParserEmulationProfile;
import com.vladsch.flexmark.util.collection.iteration.ReversiblePeekingIterator;
import com.vladsch.flexmark.util.options.MutableDataSet;
import snorochevskiy.mynotes.markups.AbstractMarkupTransformer;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.sources.AbstractNoteSource;
import snorochevskiy.mynotes.sources.FileNoteSource;

import java.util.Arrays;

import static com.vladsch.flexmark.ext.wikilink.WikiLinkExtension.IMAGE_PREFIX;
import static com.vladsch.flexmark.parser.Parser.FENCED_CODE_CONTENT_BLOCK;

public class MarkdownTransformer extends AbstractMarkupTransformer {

    Parser parser;
    HtmlRenderer renderer;

    public MarkdownTransformer(Markup markup) {
        super(markup);
    }

    @Override
    public String transform(AbstractNoteSource note, String markupText) {

        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, Arrays.asList(
                InternalLinkExtension.create(),
                AbbreviationExtension.create(),
                DefinitionExtension.create(),
                FootnoteExtension.create(),
                TablesExtension.create(),
                TypographicExtension.create()
        ));
        //options.setFrom(ParserEmulationProfile.KRAMDOWN);
        options.setFrom(ParserEmulationProfile.MARKDOWN);
        options.set(InternalLinkExtension.INTERNAL_URL_RELATIVE_PATH, ((FileNoteSource)note).getNoteDirectory().getAbsolutePath());

        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).escapeHtml(true).build();

        Node document = parser.parse(markupText);
        String html = renderer.render(document);

        // Adding Highlight.js
        String highlightJs = getClass().getClassLoader().getResource("highlight/highlight.pack.js").toExternalForm();
        String highlightCss = getClass().getClassLoader().getResource("highlight/styles/default.css").toExternalForm();

        html += "<link rel=\"stylesheet\" href=\"" + highlightCss + "\">";
        html += "<script src=\"" + highlightJs + "\"></script>";
        html += "<script>hljs.initHighlightingOnLoad();</script>";

        return html;
    }

}
