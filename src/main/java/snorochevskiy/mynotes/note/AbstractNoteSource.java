package snorochevskiy.mynotes.note;

import snorochevskiy.mynotes.markups.Markup;

import java.io.File;
import java.util.List;

public abstract class AbstractNoteSource {

    private AbstractNoteSource parent;

    private String id;
    private String name;
    private Markup markup;

    public AbstractNoteSource(AbstractNoteSource parent, String id, String name, Markup markup) {
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.markup = (markup != null ? markup : Markup.MARKDOWN);
    }

    public AbstractNoteSource(AbstractNoteSource parent) {
        this.parent = parent;
    }

    public String getId() {
        return id;
    }

    // XXX:  maybe make this setter at least protected?
    public void setId(String id) {
        this.id = id;
    }

    public String getNoteName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Markup getMarkup() {
        return markup;
    }

    public void setMarkup(Markup markup) {
        this.markup = markup;
    }

    public AbstractNoteSource getParent() {
        return parent;
    }

    public abstract String getContents();

    public abstract void saveContents(String contents);

    public abstract void persist();

    public abstract boolean hasChildrenNotes();

    public abstract List<AbstractNoteSource> getChildrenNotes();

    public abstract void updateChildrenOrder(List<AbstractNoteSource> children);

    public abstract AbstractNoteSource createChildNote(String name, Markup markup);

    public abstract void removedNote();

    public abstract List<NoteResource> listResource();

    /**
     * Add resource from user's local filesystem to note.
     * @param file
     * @return
     */
    public abstract NoteResource addResource(File file);

    public abstract void deleteResource(NoteResource noteResource);

    /**
     * Used for creating new notes.
     * Transforms note name to internal (for storage) name.
     * e.g. removes forbidden for OS filesystem characters.
     *
     * @param noteName
     * @return
     */
    protected String internalName(String noteName) {
        return noteName.replaceAll(",|:|;|$|!", "");
    }

    @Override
    public String toString() {
        return this.getNoteName();
    }

}
