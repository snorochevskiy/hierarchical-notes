package snorochevskiy.mynotes.note;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Comparator;

public class NotesByFileNameComparator implements Comparator<AbstractNoteSource> {

    private final String[] fileNames;

    public NotesByFileNameComparator(final String[] fileNames) {
        this.fileNames = fileNames;
    }

    @Override
    public int compare(AbstractNoteSource o1, AbstractNoteSource o2) {

        FileNoteSource note1 = (FileNoteSource)o1;
        FileNoteSource note2 = (FileNoteSource)o2;

        int i1 = ArrayUtils.indexOf(fileNames, note1.getNoteDirectory().getName());
        int i2 = ArrayUtils.indexOf(fileNames, note2.getNoteDirectory().getName());
        if (i1 == ArrayUtils.INDEX_NOT_FOUND && i2 == ArrayUtils.INDEX_NOT_FOUND) {
            return 0;
        } else if (i1 != ArrayUtils.INDEX_NOT_FOUND && i2 == ArrayUtils.INDEX_NOT_FOUND) {
            return -1;
        } else if (i1 == ArrayUtils.INDEX_NOT_FOUND && i2 != ArrayUtils.INDEX_NOT_FOUND) {
            return 1;
        } else {
            return Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
        }
    }
}