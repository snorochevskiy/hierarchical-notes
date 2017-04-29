package snorochevskiy.mynotes.note;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.markups.MarkupsManager;
import snorochevskiy.mynotes.space.FsBasedSpace;
import snorochevskiy.mynotes.space.SpaceException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Represents note implemented as folder in file system.
 * See details in {@see FsBasedSpace}
 */
public class FileNoteSource extends AbstractNoteSource {

    private static final String FILE_NOTE_PROPERTIES = "note.properties";

    private static final String PROPERTY_ID = "id";
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "name";
    private static final String PROPERTY_MARKUP = "markup";
    private static final String PROPERTY_CHILD_ORDER = "childOrder";

    private static final String FILE_CONTENTS = "contents";

    private static  final FileFilter FILTER = new FileNoteSourceFileFilter();

    // Directory where note is placed
    private File noteDirectory;

    // Children notes's folder names sorted in the way in which children notes should be displayed.
    private String[] childrenOrder;

    /**
     * For case when we're creating a new note.
     * @param name
     * @param markup
     * @param noteDirectory
     */
    public FileNoteSource(AbstractNoteSource parent, String id, String name, Markup markup, File noteDirectory) {
        super(parent, id, name, markup);
        this.noteDirectory = noteDirectory;
    }

    /**
     * For "loading" existing note from file
     * @param parent
     * @param noteDirectory
     * @return
     */
    public static FileNoteSource fromFile(FileNoteSource parent, File noteDirectory) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesFile(noteDirectory)));

            String id = props.getProperty(PROPERTY_ID);
            if (id == null) {
                id = generateId();
            }

            String name = props.getProperty(PROPERTY_NAME);
            if (StringUtils.isEmpty(name)) {
                name = "undefined";
            }

            String markupName = props.getProperty(PROPERTY_MARKUP);
            Markup markup = MarkupsManager.getInstance().byName(markupName);

            FileNoteSource noteSource = new FileNoteSource(parent, id, name, markup, noteDirectory);

            String childrenOrderString = props.getProperty(PROPERTY_CHILD_ORDER);
            if (childrenOrderString != null) {
                noteSource.childrenOrder = childrenOrderString.split(",");
            }

            return noteSource;

        } catch (IOException e) {
            throw new SpaceException("Cannot open note.", e);
        }
    }

    public String getContents() {
        File contentsFile = contentsFile();
        if (contentsFile == null || !contentsFile.exists()) {
            return "";
        }

        try {
            return IOUtils.toString(new FileInputStream(contentsFile), Charset.defaultCharset());
        } catch (IOException e) {
            throw new SpaceException("Cannot read file.", e);
        }

    }

    @Override
    public void saveContents(String contents) {
        try {
            IOUtils.write(contents, new FileOutputStream(contentsFile()), Charset.defaultCharset());
        } catch (IOException e) {
            throw new SpaceException("Cannot write file.", e);
        }
    }

    @Override
    public void persist() {
        try {
            if (!noteDirectory.exists()) {
                noteDirectory.mkdirs();
            }

            Properties noteProperties = new Properties();
            noteProperties.setProperty(PROPERTY_ID, getId());
            noteProperties.setProperty(PROPERTY_NAME, getNoteName());
            noteProperties.setProperty(PROPERTY_MARKUP, getMarkup().getName());

//            String[] childDirNames = new String[childNotes.size()];
//            for (int i = 0; i < childNotes.size(); i++) {
//                childDirNames[i] = childNotes.get(i).getId();
//            }
//            noteProperties.setProperty(PROPERTY_CHILD_ORDER, String.join(",", childDirNames));
            if (childrenOrder != null) {
                noteProperties.setProperty(PROPERTY_CHILD_ORDER, String.join(",", childrenOrder));
            }

            noteProperties.store(new FileOutputStream(propertiesFile()), "Note info");

        } catch (IOException e) {
            throw new SpaceException("Cannot create note.", e);
        }
    }

    public boolean hasChildrenNotes() {
        return getChildrenNotes().size() > 0;
    }

    public List<AbstractNoteSource> getChildrenNotes() {

        if (!noteDirectory.exists()) {
            // For not initialized note case
            return new ArrayList<>();
        }

        List<AbstractNoteSource> childNotes = new ArrayList<>();

        for (File file : noteDirectory.listFiles(FILTER)) {
            FileNoteSource fileNoteSource = fromFile(this, file); // new FileNoteSource(this, file);
            childNotes.add(fileNoteSource);
        }

        final String[] childrenFileNames = readChildrenOrder();

        // Sort children in the order they appear in config
        Collections.sort(childNotes, new NotesByFileNameComparator(childrenFileNames));

        return childNotes;
    }

    @Override
    public void updateChildrenOrder(List<AbstractNoteSource> children) {

        this.childrenOrder = new String[children.size()];
        for (int i = 0; i < children.size(); i++) {
            FileNoteSource child = (FileNoteSource) children.get(i);
            childrenOrder[i] = child.noteDirectory.getName();
        }
        this.persist();
    }

    @Override
    public AbstractNoteSource createChildNote(String name, Markup markup) {
        File childNoteDirectory = new File(noteDirectory, internalName(name));
        FileNoteSource childNote = new FileNoteSource(this, generateId(), name, markup, childNoteDirectory);

        this.childrenOrder = ArrayUtils.add(this.childrenOrder, childNoteDirectory.getName());
        this.persist();

        return childNote;
    }

    @Override
    public void removedNote() {
        if (getParent() == null) {
            throw new SpaceException("Cannot delete root note", null);
        }
        try {
            FileUtils.deleteDirectory(this.noteDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }
        getParent().getChildrenNotes().remove(this);
    }

    @Override
    public List<NoteResource> listResource() {
        File[] resourceFiles = noteDirectory.listFiles(this::filterResourceFiles);
        List<NoteResource> resources = new ArrayList<>(resourceFiles.length);
        for (File f : resourceFiles) {
            resources.add(toNoteResources(f));
        }
        return resources;
    }

    @Override
    public NoteResource addResource(File file) {
        Path src = file.toPath();
        Path dst = new File(noteDirectory, file.getName()).toPath();
        try {
            Path path =  Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            return toNoteResources(path.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw new SpaceException("Cannot add resource.", e);
        }
    }

    @Override
    public void deleteResource(NoteResource noteResource) {
        Path resourceToDelete = new File(noteDirectory, noteResource.getName()).toPath();
        try {
            Files.delete(resourceToDelete);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SpaceException("Cannot delete resource.", e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof FileNoteSource)) {
            return false;
        }

        FileNoteSource other = (FileNoteSource) o;
        return new EqualsBuilder()
                .append(this.noteDirectory, other.noteDirectory)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.noteDirectory)
                .toHashCode();
    }

    public static File propertiesFile(File requestedNoteDirectory) {
        return new File(requestedNoteDirectory, FILE_NOTE_PROPERTIES);
    }

    private File propertiesFile() {
        return propertiesFile(this.noteDirectory);
    }

    private String[] readChildrenOrder() {

        File propertiesFile = propertiesFile();
        if (!propertiesFile.exists()) {
            return new String[]{};
        }

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(propertiesFile));

            // children ids joined with comma
            String orderStr = props.getProperty(PROPERTY_CHILD_ORDER);
            return orderStr != null ? orderStr.split(",") : new String[]{};
        } catch (IOException e) {
            throw new SpaceException("Cannot open note.", e);
        }
    }

    public File getNoteDirectory() {
        return noteDirectory;
    }

    private File contentsFile() {
        return new File(noteDirectory, FILE_CONTENTS + "." + getMarkup().getExtension());
    }

    private NoteResource toNoteResources(File file) {
        NoteResource resource = new NoteResource(this, file.getName());
        try {
            resource.setContentType(Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO : handle
        }
        return resource;
    }

    public static String generateId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public boolean filterResourceFiles(File pathname) {
        if (!pathname.isFile()
                || pathname.getName().equals(FILE_NOTE_PROPERTIES)
                || pathname.getName().equals(FsBasedSpace.FILE_SPACE_PROPERTIES)
                || pathname.getName().equals(FILE_CONTENTS + "." + getMarkup().getExtension())) {
            return false;
        }
        return true;
    }

    static class FileNoteSourceFileFilter implements FileFilter {

        public boolean accept(File pathname) {
            if (!pathname.isDirectory()
                    || pathname.getName().equals(".")
                    || pathname.getName().equals("..")
                    || !(new File(pathname, FILE_NOTE_PROPERTIES).exists())) {
                return false;
            }
            return true;
        }

    }

}
