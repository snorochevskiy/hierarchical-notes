package snorochevskiy.ui.notes;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import snorochevskiy.mynotes.sources.AbstractNoteSource;


public class TreeNoteElement extends TreeItem<AbstractNoteSource> {

    private boolean childrenLoaded = false ;


    public TreeNoteElement(AbstractNoteSource noteSource) {
        super(noteSource);
    }

    @Override
    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    @Override
    public ObservableList<TreeItem<AbstractNoteSource>> getChildren() {

        if (childrenLoaded) {
            return super.getChildren();
        }

        childrenLoaded = true;
        for (AbstractNoteSource noteSource : getValue().getChildrenNotes()) {
            TreeNoteElement treeItem = new TreeNoteElement(noteSource);
            treeItem.setExpanded(false);
            super.getChildren().add(treeItem);
        }

        return super.getChildren();
    }

    public void addChildNote(NoteInfo noteInfo) {

        AbstractNoteSource note = getValue().createChildNote(noteInfo.getName(), noteInfo.getMarkup());
        note.persist();

        ObservableList<TreeItem<AbstractNoteSource>> children = getChildren();
        children.add(new TreeNoteElement(note));
    }

}
