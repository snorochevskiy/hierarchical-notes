package snorochevskiy.mynotes.space;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.note.AbstractNoteSource;

public abstract class AbstractSpace {

    private String id;

    private String name;
    private Markup markupType;

    protected AbstractSpace() {

    }

    protected AbstractSpace(String id, String name, Markup markupType) {
        this.id = id;
        this.name = name;
        this.markupType = markupType;
    }

    public abstract AbstractNoteSource getRootNote();

    public abstract AbstractNoteSource createNote(AbstractNoteSource parent, String name, String markup);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Markup getMarkupType() {
        return markupType;
    }

    public void setMarkupType(Markup markupType) {
        this.markupType = markupType;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof AbstractSpace)) {
            return false;
        }

        AbstractSpace other = (AbstractSpace)o;
        return new EqualsBuilder()
                .append(this.name, other.name)
                .append(this.markupType, other.markupType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.name)
                .append(this.markupType)
                .toHashCode();
    }
}
