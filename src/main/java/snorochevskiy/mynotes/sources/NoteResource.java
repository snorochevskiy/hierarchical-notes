package snorochevskiy.mynotes.sources;

/**
 * Represents resource nested inside the note.
 * It can be: image, audio, binary file, etc.
 */
public class NoteResource {

    private String name;
    private String contentType;

    public NoteResource(String name) {
        this.name = name;
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
