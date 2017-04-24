package snorochevskiy.mynotes.markups;


import snorochevskiy.mynotes.markups.markdown.MarkdownTransformer;

import java.util.ArrayList;
import java.util.List;

public class MarkupTransformerManager {

    private static final Object lock = new Object();

    private static volatile MarkupTransformerManager instance = null;

    private final List<AbstractMarkupTransformer> markupTransformers = new ArrayList<>();

    public static MarkupTransformerManager getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new MarkupTransformerManager();

                    instance.initStandardMarkups();
                }
            }
        }

        return instance;
    }

    private void initStandardMarkups() {
        markupTransformers.add(new MarkdownTransformer(Markup.MARKDOWN));
    }

    public List<AbstractMarkupTransformer> getMarkupTransformers() {
        return markupTransformers;
    }

    public AbstractMarkupTransformer byMarkup(Markup markup) {
        for (AbstractMarkupTransformer m : getMarkupTransformers()) {
            if (m.getMarkup().equals(markup)) {
                return m;
            }
        }

        return null;
    }
}
