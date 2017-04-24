package snorochevskiy.ui.windows;


import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import snorochevskiy.mynotes.sources.AbstractNoteSource;
import snorochevskiy.mynotes.sources.NoteResource;

import java.io.IOException;

public class NoteResourcesWindow {

    private Stage stage = new Stage();

    @FXML private ListView<NoteResource> resourcesListView;
    @FXML private Label fileInfoLabel;

    private AbstractNoteSource noteSource;
    private NoteResource selectedResource = null;

    public NoteResourcesWindow(AbstractNoteSource noteSource) {
        this.noteSource = noteSource;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NoteResourcesWindow.fxml"));
            loader.setController(this);

            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        stage.setTitle("Create new note");
        stage.initModality(Modality.APPLICATION_MODAL);

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
        if (db.hasString()) {
            db.getFiles().stream().forEach(f -> noteSource.addResource(f));
            System.out.println(db.getString()); // file URI
            success = true;
            initResourcesList();
        }
        // let the source know whether the string was successfully transferred and used
        event.setDropCompleted(success);

        event.consume();
    }

    @FXML
    private void onDragDetected(DragEvent event) {
        System.out.println("onDragDetected: " + event.getClass().getCanonicalName());
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
            selectedResource = resourcesListView.getSelectionModel().getSelectedItems().get(0);
            stage.close();
        }
    }
}
