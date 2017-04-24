package snorochevskiy.ui.notes;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import snorochevskiy.mynotes.sources.AbstractNoteSource;
import snorochevskiy.ui.windows.CreateNewNoteWindow;
import snorochevskiy.ui.windows.MainWindow;

import java.util.Optional;

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

        this.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent t) {
                if (t.getButton() == MouseButton.PRIMARY && t.getClickCount() == 2) {
                    mainWindowController.setSelectedNote(getItem());
                    t.consume();
                }
            }
        });
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

}