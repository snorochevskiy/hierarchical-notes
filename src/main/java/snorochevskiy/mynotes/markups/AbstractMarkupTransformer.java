package snorochevskiy.mynotes.markups;

import snorochevskiy.mynotes.sources.AbstractNoteSource;

public abstract class AbstractMarkupTransformer {

    private final Markup markup;

    public AbstractMarkupTransformer(Markup markup) {
        this.markup = markup;
    }

    public abstract String transform(AbstractNoteSource note, String markup);

    public Markup getMarkup() {
        return markup;
    }
}
