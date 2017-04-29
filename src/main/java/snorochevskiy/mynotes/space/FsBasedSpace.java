package snorochevskiy.mynotes.space;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.markups.MarkupsManager;
import snorochevskiy.mynotes.note.AbstractNoteSource;
import snorochevskiy.mynotes.note.FileNoteSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class FsBasedSpace extends AbstractSpace {

    public static final String FILE_SPACE_PROPERTIES = "space.properties";

    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "name";
    private static final String PROPERTY_MARKUP = "markup";

    private FileNoteSource rootNote;

    public static FsBasedSpace createNewSpace(String name, Markup markupType, File spaceDirectory) {

        if (!spaceDirectory.exists()) {
            // XXX: Is this necessary? We can select only existing directory in UI
            spaceDirectory.mkdirs();
        }

        File spacePropertiesFile = new File(spaceDirectory, FILE_SPACE_PROPERTIES);
        if (spacePropertiesFile.exists()) {
            throw new SpaceException("AbstractSpace already exists", null);
        }

        String id = String.valueOf(System.currentTimeMillis());

        Properties spaceProperties = new Properties();
        spaceProperties.setProperty(PROPERTY_ID, id);
        spaceProperties.setProperty(PROPERTY_NAME, name);
        spaceProperties.setProperty(PROPERTY_MARKUP, markupType.getName());
        try {
            spaceProperties.store(new FileOutputStream(spacePropertiesFile), "AbstractSpace info");
        } catch (FileNotFoundException e) {
            throw new SpaceException("Cannot create space. No directory.", e);
        } catch (IOException e) {
            throw new SpaceException("Cannot create space. IO issue.", e);
        }

        FsBasedSpace space = new FsBasedSpace(id, name, markupType, spaceDirectory);

        return space;
    }

    public FsBasedSpace(String id, String name, Markup markupType, File spaceDirectory) {
        super(id, name, markupType);
        rootNote = new FileNoteSource(null, FileNoteSource.generateId(), "ROOT", markupType, spaceDirectory);
        rootNote.persist();
    }

    public FsBasedSpace(File spaceDirectory) {
        this.rootNote = FileNoteSource.fromFile(null, spaceDirectory);//new FileNoteSource(null, spaceDirectory);

        Properties spaceProperties = new Properties();
        try {
            spaceProperties.load(new FileInputStream(new File(spaceDirectory, FILE_SPACE_PROPERTIES)));
            this.setId(spaceProperties.getProperty(PROPERTY_ID));
            this.setName(spaceProperties.getProperty(PROPERTY_NAME));
            this.setMarkupType(MarkupsManager.getInstance().byName(spaceProperties.getProperty(PROPERTY_MARKUP)));

        } catch (IOException e) {
            throw new SpaceException("Cannot load space info.", e);
        }
    }

    public AbstractNoteSource getRootNote() {
        return rootNote;
    }

    // TODO: remove
    public AbstractNoteSource createNote(AbstractNoteSource parent, String name, String markup) {
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof FsBasedSpace)) {
            return false;
        }

        FsBasedSpace other = (FsBasedSpace)o;
        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(this.rootNote, other.rootNote)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(this.rootNote)
                .toHashCode();
    }

    public File getSpaceDirectory() {
        return rootNote.getNoteDirectory();
    }
}
