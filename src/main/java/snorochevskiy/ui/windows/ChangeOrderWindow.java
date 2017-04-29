package snorochevskiy.ui.windows;


import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.stage.Modality;
import snorochevskiy.mynotes.note.AbstractNoteSource;

import java.util.Collections;

public class ChangeOrderWindow extends BaseFxWindow {

    @FXML private ListView<TreeItem<AbstractNoteSource>> elementsListView;

    public ChangeOrderWindow(ObservableList<TreeItem<AbstractNoteSource>> notes) {
        super("/fxml/ChangeOrderWindow.fxml", "Change order", Modality.APPLICATION_MODAL);

        elementsListView.setItems(notes);
    }

    @FXML
    private void onUp() {
        int ind = elementsListView.getItems().indexOf(elementsListView.getSelectionModel().getSelectedItem());
        if (ind > 0) {
            Collections.swap(elementsListView.getItems(), ind - 1, ind);
        }
    }

    @FXML
    private void onDown() {
        int ind = elementsListView.getItems().indexOf(elementsListView.getSelectionModel().getSelectedItem());
        if (ind < elementsListView.getItems().size() - 1) {
            Collections.swap(elementsListView.getItems(), ind, ind + 1);
        }
    }

    public void show() {
        stage.showAndWait();
    }
}
