package snorochevskiy.ui.windows;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.markups.MarkupsManager;
import snorochevskiy.ui.notes.NoteInfo;

public class CreateNewNoteWindow extends BaseFxWindow {

    @FXML private TextField newNoteNameTextEdit;
    @FXML private ChoiceBox<Markup> newNoteMarkupChoiseBox;

    public CreateNewNoteWindow() {
        super("/fxml/CreateNewNoteWindow.fxml", "Create new note", Modality.APPLICATION_MODAL);
    }

    @FXML
    private void initialize() {
        newNoteMarkupChoiseBox.getItems().addAll(MarkupsManager.getInstance().getMarkups());
        newNoteMarkupChoiseBox.setValue(newNoteMarkupChoiseBox.getItems().get(0));
    }

    @FXML
    private void onCreateButtonClick(MouseEvent event) {
        stage.close();
    }

    public NoteInfo show() {
        stage.showAndWait();

        return new NoteInfo(newNoteNameTextEdit.getText(), newNoteMarkupChoiseBox.getValue());
    }
}
