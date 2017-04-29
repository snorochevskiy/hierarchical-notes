package snorochevskiy.ui.windows;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import snorochevskiy.mynotes.markups.Markup;
import snorochevskiy.mynotes.markups.MarkupsManager;
import snorochevskiy.mynotes.space.FsBasedSpace;

import java.io.File;
import java.io.IOException;

public class CreateSpaceWindow {

    Stage stage = new Stage();

    private File spaceDirectory = null;
    private FsBasedSpace createdSpace = null;

    @FXML private TextField spaceNameTextEdit;
    @FXML private ChoiceBox<Markup> markupChoiceBox;
    @FXML private TextField selectedPathTextField;
    @FXML private Button createButton;


    public CreateSpaceWindow() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CreateSpaceWindow.fxml"));
            loader.setController(this);

            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        stage.setTitle("Create new AbstractSpace");
        stage.initModality(Modality.APPLICATION_MODAL);
    }

    public FsBasedSpace show() {
        stage.showAndWait();

        return this.createdSpace;
    }

    @FXML
    private void initialize() {
        markupChoiceBox.getItems().addAll(MarkupsManager.getInstance().getMarkups());
        markupChoiceBox.setValue(markupChoiceBox.getItems().get(0));
    }

    @FXML
    private void onSelectPathClick(MouseEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            spaceDirectory = selectedDirectory;
            selectedPathTextField.setText(spaceDirectory.getAbsolutePath());
            createButton.setDisable(false);
        }
    }

    @FXML
    private void onCreateButtonClick(MouseEvent event) {
        if (spaceDirectory == null) {
            return;
        }
        if (spaceNameTextEdit.getText().trim().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Creation space error");
            alert.setHeaderText("Cannot create space");
            alert.setContentText("AbstractSpace name shouldn't be blank");

            alert.showAndWait();
            return;
        }

        this.createdSpace = FsBasedSpace.createNewSpace(spaceNameTextEdit.getText(), markupChoiceBox.getValue(), spaceDirectory);
        stage.close();
    }

}
