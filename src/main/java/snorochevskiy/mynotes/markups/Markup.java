package snorochevskiy.mynotes.markups;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Markup {

    public static final Markup MARKDOWN = new Markup("Markdown", "md");

    private String name;
    private String extension;


    public Markup(String name, String extension) {
        this.name = name;
        this.extension = extension;
    }

    public String getName() {
        return name;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Markup)) {
            return false;
        }

        Markup other = (Markup) o;
        return new EqualsBuilder()
                .append(this.name, other.name)
                .append(this.extension, other.extension)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.name)
                .append(this.extension)
                .toHashCode();
    }
}
