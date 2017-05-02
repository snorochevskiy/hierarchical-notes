package snorochevskiy.ui.notes;

import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import snorochevskiy.mynotes.note.AbstractNoteSource;
import snorochevskiy.ui.windows.ChangeOrderWindow;
import snorochevskiy.ui.windows.CreateNewNoteWindow;
import snorochevskiy.ui.windows.MainWindow;

import java.util.Optional;
import java.util.stream.Collectors;

public class NoteTreeCellImpl extends TreeCell<AbstractNoteSource> {

    private MainWindow mainWindowController;

    private ContextMenu contextMenu = new ContextMenu();

    public NoteTreeCellImpl(MainWindow mainWindowController) {
        this.mainWindowController = mainWindowController;

        MenuItem addMenuItem = new MenuItem("Add Note");
        addMenuItem.setOnAction(this::handleAddNote);
        contextMenu.getItems().add(addMenuItem);

        MenuItem removeMenuItem = new MenuItem("Remove Note");
        removeMenuItem.setOnAction(this::handleRemoveNote);
        contextMenu.getItems().add(removeMenuItem);

        MenuItem renameMenuItem = new MenuItem("Rename Note");
        renameMenuItem.setOnAction(this::handleRenameNote);
        contextMenu.getItems().add(renameMenuItem);

        MenuItem reorderMenuItem = new MenuItem("Reorder children");
        reorderMenuItem.setOnAction(this::handleReorderChildren);
        contextMenu.getItems().add(reorderMenuItem);

        MenuItem moveMenuItem = new MenuItem("Move this note");
        moveMenuItem.setOnAction(this::handleMoveNote);
        contextMenu.getItems().add(moveMenuItem);

        setOnDragDetected(this::handleDragDetected);
        setOnDragOver(this::handleDragOver);
        setOnDragDropped(this::handleDragDropped);
        setOnDragDone(this::handleDragDone);

//        this.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
//            @Override public void handle(MouseEvent t) {
//
//                if (t.getButton() == MouseButton.PRIMARY /*&& t.getClickCount() == 2*/) {
//                    mainWindowController.handleNoteSelected(getItem());
//                    //t.consume();
//                }
//            }
//        });
    }

    @Override
    public void updateItem(AbstractNoteSource item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            setText(getItem() == null ? "" : getItem().toString());
            setGraphic(getTreeItem().getGraphic());
            setContextMenu(contextMenu);
        }
    }

    private void handleAddNote(Event t) {
        CreateNewNoteWindow dialog = new CreateNewNoteWindow();
        NoteInfo noteInfo = dialog.show();
        if (noteInfo == null) {
            return;
        }
        ((TreeNoteElement)getTreeItem()).addChildNote(noteInfo);
    }

    private void handleRemoveNote(Event t) {
        getItem().removedNote();
        getTreeItem().getParent().getChildren().remove(getTreeItem());
    }

    private void handleRenameNote(Event t) {
        TextInputDialog dialog = new TextInputDialog(getItem().getNoteName());
        dialog.setTitle("My notes");
        dialog.setHeaderText("Rename not");
        dialog.setContentText("Please enter new note name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            getItem().setName(name);
            getItem().persist();
            // Maybe it's not the best way to update tree item?
            getTreeView().refresh();
        });
    }

    private void handleReorderChildren(Event t) {
        ChangeOrderWindow changeOrderWindow = new ChangeOrderWindow(getTreeItem().getChildren());
        changeOrderWindow.show();
        getItem().updateChildrenOrder(getTreeItem().getChildren().stream().map(x -> x.getValue()).collect(Collectors.toList()));
    }

    private void handleMoveNote(Event t) {

    }

    private void handleDragDetected(MouseEvent event) {
        // event.getSource() = snorochevskiy.ui.notes.NoteTreeCellImpl
        // System.out.println("DRAG DETECTED " + event.getSource().getClass().getCanonicalName());
        //event.setDragDetect(true);

//        Dragboard dragBoard = startDragAndDrop(TransferMode.MOVE);
//        ClipboardContent content = new ClipboardContent();
//        content.put(DataFormat.PLAIN_TEXT, getItem().getNoteName());
//        dragBoard.setContent(content);
//        event.consume();
    }

    private void handleDragOver(DragEvent dragEvent) {
        System.out.println("DRAG OVER " + ((NoteTreeCellImpl)dragEvent.getSource()).getItem().getNoteName());
        if (dragEvent.getDragboard().hasString()) {
            String valueToMove = dragEvent.getDragboard().getString();
            System.out.println("Value to move " + valueToMove);
            if (!valueToMove.matches(getItem().getNoteName())) {
                // We accept the transfer!!!!!
                dragEvent.acceptTransferModes(TransferMode.MOVE);
            }
        }
        dragEvent.consume();
    }

    private void handleDragDropped(DragEvent dragEvent) {
        //System.out.println("Drag dropped on " + getItem());
        //String valueToMove = dragEvent.getDragboard().getString();
//        TreeItem<AbstractNoteSource> itemToMove = search(parentTree.getRoot(), valueToMove);
//        TreeItem<AbstractNoteSource> newParent = search(parentTree.getRoot(), item.getName());
//        // Remove from former parent.
//        itemToMove.getParent().getChildren().remove(itemToMove);
//        // Add to new parent.
//        newParent.getChildren().add(itemToMove);
//        newParent.setExpanded(true);
        //dragEvent.consume();
    }

    private void handleDragDone(DragEvent dragEvent) {
        //System.out.println("Drag done on " + getItem());
        //dragEvent.consume();
    }

}