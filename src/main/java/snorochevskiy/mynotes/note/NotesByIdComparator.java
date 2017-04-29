package snorochevskiy.mynotes.note;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Comparator;

public class NotesByIdComparator implements Comparator<AbstractNoteSource> {

    private final String[] ids;

    public NotesByIdComparator(final String[] ids) {
        this.ids = ids;
    }

    @Override
    public int compare(AbstractNoteSource o1, AbstractNoteSource o2) {
        int i1 = ArrayUtils.indexOf(ids, o1.getId());
        int i2 = ArrayUtils.indexOf(ids, o2.getId());
        if (i1 == ArrayUtils.INDEX_NOT_FOUND  && i2 == ArrayUtils.INDEX_NOT_FOUND) {
            return 0;
        } else if (i1 != ArrayUtils.INDEX_NOT_FOUND  && i2 == ArrayUtils.INDEX_NOT_FOUND) {
            return -1;
        } else if (i1 == ArrayUtils.INDEX_NOT_FOUND  && i2 != ArrayUtils.INDEX_NOT_FOUND) {
            return 1;
        } else {
            return Integer.valueOf(i1).compareTo(Integer.valueOf(i2));
        }
    }
}
