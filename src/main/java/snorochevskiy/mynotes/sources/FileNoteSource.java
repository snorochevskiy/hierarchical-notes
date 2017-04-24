package snorochevskiy.mynotes.sources;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class FileNoteSource extends AbstractNoteSource {

    private static final String FILE_NOTE_PROPERTIES = "note.properties";

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "name";
    private static final String PROPERTY_MARKUP = "markup";

    private static final String FILE_CONTENTS = "contents";

    private static  final FileFilter FILTER = new FileNoteSourceFileFilter();

    private List<AbstractNoteSource> childNotes = null;

    private File noteDirectory;

    /**
     * For case when we're creating a new note.
     * @param name
     * @param markup
     * @param noteDirectory
     */
    public FileNoteSource(AbstractNoteSource parent, String name, Markup markup, File noteDirectory) {
        super(parent, name, markup);
        this.noteDirectory = noteDirectory;
    }

    /**
     * When existing note is read from file.
     * @param noteDirectory
     */
    public FileNoteSource(AbstractNoteSource parent, File noteDirectory) {
        super(parent);
        this.noteDirectory = noteDirectory;

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(new File(noteDirectory, FILE_NOTE_PROPERTIES)));

            setName(props.getProperty(PROPERTY_NAME));
            setMarkup(MarkupsManager.getInstance().byName(props.getProperty(PROPERTY_MARKUP)));

        } catch (IOException e) {
            throw new SpaceException("Cannot open note.", e);
        }
    }

    public String getContents() {
        File contentsFile = contentsFile();
        if (!contentsFile.exists()) {
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
            noteProperties.setProperty(PROPERTY_NAME, getNoteName());
            noteProperties.setProperty(PROPERTY_MARKUP, getMarkup().getName());
            noteProperties.store(new FileOutputStream(new File(noteDirectory, FILE_NOTE_PROPERTIES)), "Note info");

        } catch (IOException e) {
            throw new SpaceException("Cannot create note.", e);
        }
    }

    public boolean hasChildrenNotes() {
        return getChildrenNotes().size() > 0;
    }

    public List<AbstractNoteSource> getChildrenNotes() {

        if (childNotes != null) {
            return childNotes;
        }

        if (!noteDirectory.exists()) {
            // For not initialized note case
            return new ArrayList<>();
        }

        childNotes = new ArrayList<>();

        for (File file : noteDirectory.listFiles(FILTER)) {
            FileNoteSource fileNoteSource = new FileNoteSource(this, file);
            childNotes.add(fileNoteSource);
        }

        return childNotes;
    }

    @Override
    public AbstractNoteSource createChildNote(String name, Markup markup) {
        File childNoteDirectory = new File(noteDirectory, internalName(name));
        FileNoteSource childNote = new FileNoteSource(this, name, markup, childNoteDirectory);

        getChildrenNotes().add(childNote);

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

    public File getNoteDirectory() {
        return noteDirectory;
    }

    private File contentsFile() {
        System.out.println(getMarkup());
        return new File(noteDirectory, FILE_CONTENTS + "." + getMarkup().getExtension());
    }

    private NoteResource toNoteResources(File file) {
        NoteResource resource = new NoteResource(file.getName());
        try {
            resource.setContentType(Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            // TODO : handle
        }
        return resource;
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
