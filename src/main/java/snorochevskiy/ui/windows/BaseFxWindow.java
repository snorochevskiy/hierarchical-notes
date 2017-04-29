package snorochevskiy.ui.windows;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class BaseFxWindow {

    protected Stage stage = new Stage();

    protected BaseFxWindow(String windowsFxmlFile, String title, Modality modality) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(windowsFxmlFile));
            loader.setController(this);

            Parent root = loader.load();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }
        stage.setTitle(title);
        stage.initModality(modality);
    }

}
