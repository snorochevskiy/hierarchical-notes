package snorochevskiy.ui.windows;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import snorochevskiy.mynotes.note.AbstractNoteSource;
import snorochevskiy.mynotes.note.NoteResource;

public class NoteResourcesWindow extends BaseFxWindow {

    @FXML private ListView<NoteResource> resourcesListView;
    @FXML private Label fileInfoLabel;
    @FXML private Button onInsertButton;
    @FXML private Button onDeleteButton;

    private AbstractNoteSource noteSource;
    private NoteResource selectedResource = null;

    public NoteResourcesWindow(AbstractNoteSource noteSource) {
        super("/fxml/NoteResourcesWindow.fxml", "Note resources", Modality.APPLICATION_MODAL);

        this.noteSource = noteSource;

        onInsertButton.disableProperty().bind(resourcesListView.getSelectionModel().selectedItemProperty().isNull());
        onDeleteButton.disableProperty().bind(resourcesListView.getSelectionModel().selectedItemProperty().isNull());

        resourcesListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<NoteResource>() {
            @Override
            public void changed(ObservableValue<? extends NoteResource> observable, NoteResource oldValue, NoteResource newValue) {
                if (newValue != null) {
                    fileInfoLabel.setText("Name:" + newValue.getName() + "\nContent-Type: " + newValue.getContentType());

                }
            }
        });

        initResourcesList();
    }

    private void initResourcesList() {
        resourcesListView.getItems().clear();
        for (NoteResource noteResource : this.noteSource.listResource()) {
            resourcesListView.getItems().add(noteResource);
        }
    }

    public NoteResource show() {
        stage.showAndWait();

        return selectedResource;
    }

    @FXML
    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasString()) { // db.getString() is file URI
            db.getFiles().stream().forEach(f -> noteSource.addResource(f));
            success = true;
            initResourcesList();
        }
        // let the source know whether the string was successfully transferred and used
        event.setDropCompleted(success);

        event.consume();
    }

    @FXML
    private void onDragDetected(DragEvent event) {

    }

    @FXML
    private void onDragOver(DragEvent event) {
        if (event.getGestureSource() != resourcesListView && event.getDragboard().hasString()) {
            // allow for both copying and moving, whatever user chooses
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    @FXML
    private void onResourcesListViewClicked(MouseEvent event) {
        if (event.getClickCount() >= 2) {
            onInsert();
        }
    }

    @FXML
    private void onInsert() {
        selectedResource = resourcesListView.getSelectionModel().getSelectedItems().get(0);
        stage.close();
    }

    @FXML
    private void onDelete() {
        NoteResource resource = resourcesListView.getSelectionModel().getSelectedItems().get(0);
        resource.getNote().deleteResource(resource);
        resourcesListView.getItems().remove(resource);
    }
}
