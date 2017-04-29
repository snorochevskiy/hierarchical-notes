package snorochevskiy.mynotes.note;

/**
 * Represents resource nested inside the note.
 * It can be: image, audio, binary file, etc.
 */
public class NoteResource {

    private AbstractNoteSource note;

    private String name;
    private String contentType;

    public NoteResource(AbstractNoteSource note, String name) {
        this.note = note;
        this.name = name;
    }

    public AbstractNoteSource getNote() {
        return note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return getName();
    }
}
