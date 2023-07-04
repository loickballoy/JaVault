package fr.epita.assistants.myide.front;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.utils.front.FileManager;
import fr.epita.assistants.utils.front.PasswordManager;
import fr.epita.assistants.utils.front.TreeBuilder;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.TextArea;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.*;


public class Interface extends Application {

    public static Stage stage = null;
    public static TreeView treeview = null;
    public static ProjectService projectService = null;
    public static Project project = null;
    public static BorderPane borderRoot = null;
    public static TabPane tabPane = null;

    public static Tab currTab = null;
    public static List<String> tabOpened = null;

    public static Set<String> autoComplete = null;

    public static List<MenuItem> shortCut = null;

    public static String css = null;

    public class TextAreaOutputStream extends OutputStream {
        private TextArea textArea;

        public TextAreaOutputStream(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        public void write(int b) throws IOException {
            textArea.appendText(String.valueOf((char) b));
        }
    }

    private class TerminalPrintStream extends PrintStream {
        private final TextArea textArea;
        private final Color color;

        public TerminalPrintStream(TextArea textArea, Color color) {
            super(new TextAreaOutputStream(textArea));
            this.textArea = textArea;
            this.color = color;
        }

        @Override
        public void println(String x) {
            Text textFlow = new Text(x + System.lineSeparator());
            textArea.setStyle("-fx-text-fill: " + toWebColor(color));
            System.out.println("color: " + toWebColor(color));
            textArea.appendText(textFlow.getText());
        }


        private String toWebColor(Color color) {
            return "#" + color.toString().substring(2, 8);
        }

    }


    @Override
    public void start(Stage primaryStage) throws IOException {

        stage = primaryStage;
        tabOpened = new ArrayList<>();
        autoComplete = new HashSet<>();
        tabPane = new TabPane();
        shortCut = new ArrayList<>();
        projectService = MyIde.init(new MyIde.Configuration(null, null));
        project = projectService.load(Paths.get(new File(".").getPath()));


        // Ajouter la feuille de style
        String cssPath = "styles.css";
        css = getClass().getClassLoader().getResource(cssPath).toExternalForm();

        //PARTIE TREE VIEW
        String rootPath = "./"; //System.getProperty("user.dir");
        File rootDirectory = new File(rootPath);
        TreeItem<String> rootNode = new TreeItem<>(rootDirectory.getAbsolutePath());
        rootNode.setExpanded(false);
        TreeBuilder.buildTree(rootDirectory, rootNode);
        var treeView = new TreeView<>(rootNode);
        treeview = treeView;
        treeView.setShowRoot(true);
        treeView.setCellFactory(param -> new TreeCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    File file = new File(item);
                    setText(file.getName());
                }
            }
        });

        // Gère l'action de sélection d'un élément dans l'arborescence
        treeView.setOnMouseClicked(event -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                FileManager.openFile(selectedItem);
            }
        });

        //PARTIE TOOL BAR
        ToolBar toolBar = MyToolBar.init(stage, rootDirectory);


        // Crée la zone de texte pour le terminal
        var terminalTextArea = new TextArea();
        terminalTextArea.setEditable(false);
        terminalTextArea.setPrefRowCount(15);



        System.setOut(new PrintStream(new TerminalPrintStream(terminalTextArea, Color.WHITE)));
        System.setErr(new PrintStream(new TerminalPrintStream(terminalTextArea, Color.RED)));

        // Crée le conteneur principal
        BorderPane root = new BorderPane();
        borderRoot = root;
        root.setPadding(new Insets(5));
        root.setTop(toolBar);
        root.setLeft(treeview);
        root.setCenter(tabPane);
        root.setBottom(terminalTextArea);

        // Crée la scène
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("Theme.css");

        // Configure le stage
        // Définit le logo de la fenêtre
        Image logoImage = new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("logo.png")));
        primaryStage.getIcons().add(logoImage);
        primaryStage.setTitle("Vault");
        primaryStage.setScene(scene);
        primaryStage.show();

        KeyCombination lockCombination = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
        KeyCombination cryptoCombination = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);

        scene.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (lockCombination.match(event)) {
                shortCut.get(0).fire();
            }
            else if (cryptoCombination.match(event)) {
                shortCut.get(1).fire();
            }
        });
        // Ajouter un écouteur de changement de sélection
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                currTab = newTab;
            }
        });
    }

    public static void updateTree()
    {
        File selectedDirectory = new File(treeview.getRoot().getValue().toString());
        TreeItem<String> rootNode = new TreeItem<>(selectedDirectory.getAbsolutePath());
        rootNode.setExpanded(true);
        TreeBuilder.buildTree(selectedDirectory, rootNode);
        Interface.treeview.setRoot(rootNode);
        Interface.treeview.refresh();
        Interface.project = Interface.projectService.load(Paths.get(selectedDirectory.getAbsolutePath()));
        PasswordManager.startVaultProject();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

