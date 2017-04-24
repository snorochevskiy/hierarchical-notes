package snorochevskiy.ui.notes;

import snorochevskiy.mynotes.markups.Markup;

public class NoteInfo {

    private String name;
    private Markup markup;

    public NoteInfo(String name, Markup markup) {
        this.name = name;
        this.markup = markup;
    }

    public String getName() {
        return name;
    }

    public Markup getMarkup() {
        return markup;
    }
}
