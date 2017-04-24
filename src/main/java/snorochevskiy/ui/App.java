package snorochevskiy.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.SystemUtils;
import snorochevskiy.ui.windows.MainWindow;

import java.io.IOException;

// -Dsun.awt.disablegrab=true
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        if (SystemUtils.IS_OS_LINUX) {
            // For font in Linux
            System.setProperty("prism.lcdtext", "false");
            System.setProperty("prism.text", "t2k");
        }
        MainWindow mainWindow = new MainWindow(primaryStage);
        primaryStage.show();
    }
}
