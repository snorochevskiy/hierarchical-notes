package snorochevskiy.ui.windows;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;
import snorochevskiy.config.AppConfig;
import snorochevskiy.config.AppConfigManager;
import snorochevskiy.config.PersistedSpace;
import snorochevskiy.mynotes.markups.AbstractMarkupTransformer;
import snorochevskiy.mynotes.markups.MarkupTransformerManager;
import snorochevskiy.mynotes.sources.AbstractNoteSource;
import snorochevskiy.mynotes.sources.NoteResource;
import snorochevskiy.mynotes.space.AbstractSpace;
import snorochevskiy.mynotes.space.FsBasedSpace;
import snorochevskiy.mynotes.space.SpaceMarshaller;
import snorochevskiy.mynotes.space.SpacesMarshallerFactory;
import snorochevskiy.ui.notes.NoteTreeCellImpl;
import snorochevskiy.ui.notes.TreeNoteElement;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class MainWindow {

    private Stage stage;

    @FXML private BorderPane mainWindow;
    @FXML private TreeView notesTreeView;
    @FXML private Tab editorTab;
    @FXML private Tab resultTab;
    @FXML private ChoiceBox<AbstractSpace> spaceChoiceBox;

    @FXML private TextArea editorTextArea;
    @FXML private WebView resultWebView;

    //private List<AbstractSpace> spaces = new ArrayList<AbstractSpace>();
    private AbstractNoteSource selectedNote;

    @FXML private ToolBar editToolbar;
    @FXML private ToolBar resultToolbar;

    @FXML private Button saveToolButton;
    @FXML private Button viewToolButton;

    private boolean isEditMode = false;


    public MainWindow(Stage stage) {
        this.stage = stage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainWindow.fxml"));
            loader.setController(this);
            Parent root = loader.load();

            stage.setTitle("My notes");

            Scene scene = new Scene(root, 640, 480);
            scene.getStylesheets().add(getClass().getClassLoader().getResource("stylesheets/application.css").toExternalForm());

            stage.setScene(scene);
            //stage.getScene().getStylesheets().add("http://fonts.googleapis.com/css?family=Gafata");
        } catch (IOException e) {
            e.printStackTrace();
            Platform.exit();
        }

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                AppConfig appConfig = new AppConfig();
                if (spaceChoiceBox.getValue() != null) {
                    appConfig.setLastActiveSpace(spaceChoiceBox.getValue().getId());
                }
                if (!spaceChoiceBox.getItems().isEmpty()) {
                    appConfig.setOpenedSpaces(AppConfigManager.toPersistedSpace(spaceChoiceBox.getItems()));
                }
                AppConfigManager.write(appConfig);
            }
        });
    }

    @FXML
    private void initialize() {
        notesTreeView.setEditable(false);

        notesTreeView.setCellFactory(new Callback<TreeView<TreeNoteElement>, TreeCell<TreeNoteElement> >() {
            public TreeCell call(TreeView param) {
                return new NoteTreeCellImpl(MainWindow.this);
            }
        });

        notesTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<AbstractNoteSource>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<AbstractNoteSource>> observable, TreeItem<AbstractNoteSource> oldValue, TreeItem<AbstractNoteSource> newValue) {
                if (newValue instanceof TreeNoteElement) {
                    setSelectedNote(((TreeNoteElement)newValue).getValue());
                }
            }
        });

        URL webViewCss = this.getClass().getClassLoader().getResource("stylesheets/web-view.css");

        resultWebView.getEngine().setUserStyleSheetLocation(webViewCss.toExternalForm());

        AppConfig appConfig = AppConfigManager.load();
        if (appConfig != null) {
            for (PersistedSpace s : appConfig.getOpenedSpaces()) {
                SpaceMarshaller marshaller = SpacesMarshallerFactory.getInstance().getMarshaller(s.getSpaceClassName());
                AbstractSpace space = marshaller.unmarshall(s.getSpaceObject());
                spaceChoiceBox.getItems().add(space);
                if (appConfig.getLastActiveSpaceId() != null && appConfig.getLastActiveSpaceId().equals(space.getId())) {
                    spaceChoiceBox.setValue(space);
                }
            }
            initFileView();
        }

    }

    //-----------------
    // Menu section
    //-----------------

    @FXML
    private void onMenuCreateNewSpace() {
        CreateSpaceWindow createSpaceWindow = new CreateSpaceWindow();
        AbstractSpace space = createSpaceWindow.show();

        if (space != null) {
            spaceChoiceBox.getItems().add(space);
            setSelectedSpace(space);
        }
    }

    @FXML
    private void onFileMenuOpen() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedFile = directoryChooser.showDialog(null);

        if (selectedFile != null) {
            AbstractSpace space = new FsBasedSpace(selectedFile);
            if (!spaceChoiceBox.getItems().contains(space)) {
                spaceChoiceBox.getItems().add(space);
            } else {
                space = spaceChoiceBox.getItems().get(spaceChoiceBox.getItems().indexOf(space));
            }
            setSelectedSpace(space);

            initFileView();
        }
    }

    @FXML
    private void onFileMenuClose() {
        Platform.exit();
    }


    @FXML
    private void onSpaceMenuDeleteSpace() {
        // TODO : implement
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete space");
        alert.setHeaderText("Not implemented yet");
        alert.setContentText("Not implemented yet");
        alert.showAndWait();
    }

    @FXML
    private void onHelpMenuAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("About program");
        alert.setContentText("Tool for storing notes in hierarchical structure");
        alert.showAndWait();
    }

    // ---------------------
    // Toolbar section
    // ---------------------
    @FXML
    private void onSaveButtonAction(ActionEvent event) {
        if (selectedNote == null) {
            return;
        }
        selectedNote.saveContents(editorTextArea.getText());
    }

    @FXML
    private void onViewNoteButtonAction() {

        AbstractMarkupTransformer transformer = MarkupTransformerManager.getInstance().byMarkup(selectedNote.getMarkup());
        String html = transformer.transform(selectedNote, editorTextArea.getText());

        System.out.println(html);
        resultWebView.getEngine().loadContent(html);

        toNoteViewMode();
    }

    @FXML
    private void onEditNoteButtonAction(ActionEvent event) {
        toNoteEditMode();
    }

    @FXML
    private void onResourcesToolButton(ActionEvent event) {
        NoteResourcesWindow noteResourcesWindow = new NoteResourcesWindow(this.selectedNote);
        NoteResource noteResource = noteResourcesWindow.show();
        if (noteResource != null) {
            if (noteResource.getContentType().startsWith("image")) {
                int caretPosition = editorTextArea.getCaretPosition();
                String text = editorTextArea.getText();
                text = text.substring(0, caretPosition)
                        + "![" + noteResource.getName() + "](" + noteResource.getName() + ")"
                        + text.substring(caretPosition);
                editorTextArea.setText(text);
            } else {
                int caretPosition = editorTextArea.getCaretPosition();
                String text = editorTextArea.getText();
                text = text.substring(0, caretPosition)
                        + "[" + noteResource.getName() + "](" + noteResource.getName() + ")"
                        + text.substring(caretPosition);
                editorTextArea.setText(text);
            }
        }
    }


    // --------------------
    // inner functionality
    // --------------------

    private void toNoteEditMode() {
        isEditMode = true;

        editToolbar.setVisible(true);
        resultToolbar.setVisible(false);

        editorTextArea.setVisible(true);
        resultWebView.setVisible(false);
    }

    private void toNoteViewMode() {
        isEditMode = false;

        editToolbar.setVisible(false);
        resultToolbar.setVisible(true);

        editorTextArea.setVisible(false);
        resultWebView.setVisible(true);
    }

    public void setSelectedNote(AbstractNoteSource note) {
        if (note == null) {
            return;
        }
        selectedNote = note;
        editorTextArea.setText(selectedNote.getContents());

        AbstractMarkupTransformer transformer = MarkupTransformerManager.getInstance().byMarkup(selectedNote.getMarkup());
        String html = transformer.transform(selectedNote, selectedNote.getContents());

        System.out.println(html);
        resultWebView.getEngine().loadContent(html);
        resultWebView.getEngine().documentProperty().addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> observable, Document oldValue, Document newValue) {
                if (newValue != null) {
                    modifyDom(newValue);
                }
            }
        });
        toNoteViewMode();
    }

    private void setSelectedSpace(AbstractSpace space) {
        spaceChoiceBox.setValue(space);
        initFileView();
    }


    // TODO call this method on space choice box CHANGED event
    private void initFileView() {

        if (spaceChoiceBox.getValue() == null) {
            return;
        }

        TreeNoteElement rootItem = new TreeNoteElement(spaceChoiceBox.getValue().getRootNote());
        notesTreeView.setRoot(rootItem);
        rootItem.setExpanded(true);

    }

    private void modifyDom(Document document) {
        NodeList nodeList = document.getElementsByTagName("a");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node= nodeList.item(i);
            EventTarget eventTarget = (EventTarget) node;
            eventTarget.addEventListener("click", new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    EventTarget target = evt.getCurrentTarget();
                    HTMLAnchorElement anchorElement = (HTMLAnchorElement) target;
                    String href = anchorElement.getHref();
                    System.out.println(href);

                    if (href.matches("^\\w{2,}:.+")) {
                        // TODO : open in media viewer
                    } else {
                        TreeNoteElement treeNoteElement = findRelativeNote(selectedNote, href);
                        if (treeNoteElement != null) {
                            notesTreeView.getSelectionModel().select(treeNoteElement);
                            setSelectedNote(treeNoteElement.getValue());
                        }
                    }

                    evt.preventDefault();
                }
            }, false);
        }
    }

    private TreeNoteElement findRelativeNote(AbstractNoteSource ownerNote, String href) {
        String[] path = href.split("/+");

        TreeNoteElement currentlySelectedTreeItem = ((TreeNoteElement)notesTreeView.getSelectionModel().getSelectedItem());

//        if (path.length == 1) {
//            return findChildTreeNoteItemByName(currentlySelectedTreeItem, href);
//        }

        for (String p : path) {
            if (currentlySelectedTreeItem == null) {
                break;
            } else if (p.equals(".")) {
                // skip
            } else if (p.equals("..")) {
                currentlySelectedTreeItem = (TreeNoteElement)currentlySelectedTreeItem.getParent();
            } else {
                currentlySelectedTreeItem = findChildTreeNoteItemByName(currentlySelectedTreeItem, p);
            }
        }
        return currentlySelectedTreeItem;
    }

    private TreeNoteElement findChildTreeNoteItemByName(TreeNoteElement current, String name) {
        for (TreeItem<AbstractNoteSource> child : current.getChildren()) {
            if (child.getValue().getNoteName().equals(name)) {
                return (TreeNoteElement)child;
            }
        }
        return null;
    }

}
