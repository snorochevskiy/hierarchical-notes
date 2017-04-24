package snorochevskiy.ui.windows;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.markups.MarkupsManager;
import snorochevskiy.ui.notes.NoteInfo;

import java.io.IOException;

public class CreateNewNoteWindow {

    @FXML private TextField newNoteNameTextEdit;
    @FXML private ChoiceBox<Markup> newNoteMarkupChoiseBox;

    Stage stage = new Stage();

    public CreateNewNoteWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateNewNoteWindow.fxml"));
            loader.setController(this);

            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        stage.setTitle("Create new note");
        stage.initModality(Modality.APPLICATION_MODAL);
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
