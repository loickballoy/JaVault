package fr.epita.assistants.myide.front;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.utils.front.CryptoManager;
import fr.epita.assistants.utils.front.PasswordManager;
import fr.epita.assistants.utils.front.TreeBuilder;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;
import org.owasp.dependencycheck.exception.ExceptionCollection;

import fr.epita.assistants.myide.domain.entity.maven.*;
import fr.epita.assistants.myide.domain.entity.git.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static fr.epita.assistants.myide.domain.entity.Feature.*;

public class MyToolBar {

    public static ToolBar init(Stage primaryStage, File rootPath) {
        Button anyButton = new Button("Any");
        Button gitButton = new Button("Git");
        Button mavenButton = new Button("Maven");
        Button safetyButton = new Button("Safety");
        Button fileButton = new Button("Files");

        // Crée les menus contextuels
        ContextMenu fileMenu = new ContextMenu();
        MenuItem openMenu = new MenuItem("Open Folder");
        MenuItem addFileMenu = new MenuItem("Add File");
        MenuItem addFolderMenu = new MenuItem("Add Folder");
        fileMenu.getItems().add(openMenu);
        fileMenu.getItems().add(addFileMenu);
        fileMenu.getItems().add(addFolderMenu);

        // Crée les menus contextuels
        ContextMenu gitMenu = new ContextMenu();
        gitMenu.getItems().add(new MenuItem("Add"));
        gitMenu.getItems().add(new MenuItem("Commit"));
        gitMenu.getItems().add(new MenuItem("Pull"));
        gitMenu.getItems().add(new MenuItem("Push"));
        //new MyMenuItem("Add", gitMenu, Mandatory.Features.Git.ADD, ".");
        //new MyMenuItem("Commit", gitMenu, Mandatory.Features.Git.COMMIT);
        //new MyMenuItem("Pull", gitMenu, Mandatory.Features.Git.PULL);
        //new MyMenuItem("Push", gitMenu, Mandatory.Features.Git.PUSH);

        ContextMenu anyMenu = new ContextMenu();
        anyMenu.getItems().add(new MenuItem("Cleanup"));
        anyMenu.getItems().add(new MenuItem("Dist"));
        anyMenu.getItems().add(new MenuItem("Search"));
        new MyMenuItem("Cleanup", anyMenu, Mandatory.Features.Any.CLEANUP);
        new MyMenuItem("Dist", anyMenu, Mandatory.Features.Any.DIST);
        new MyMenuItem("Search", anyMenu, Mandatory.Features.Any.SEARCH);

        ContextMenu mavenMenu = new ContextMenu();
        mavenMenu.getItems().add(new MenuItem("Clean"));
        mavenMenu.getItems().add(new MenuItem("Compile"));
        mavenMenu.getItems().add(new MenuItem("Exec"));
        mavenMenu.getItems().add(new MenuItem("Install"));
        mavenMenu.getItems().add(new MenuItem("Package"));
        mavenMenu.getItems().add(new MenuItem("Test"));
        mavenMenu.getItems().add(new MenuItem("Tree"));
        new MyMenuItem("Tree", mavenMenu, Mandatory.Features.Maven.TREE);
        new MyMenuItem("Test", mavenMenu, Mandatory.Features.Maven.TEST);
        new MyMenuItem("Package", mavenMenu, Mandatory.Features.Maven.PACKAGE);
        new MyMenuItem("Install", mavenMenu, Mandatory.Features.Maven.INSTALL);
        new MyMenuItem("Exec", mavenMenu, Mandatory.Features.Maven.EXEC);
        new MyMenuItem("Compile", mavenMenu, Mandatory.Features.Maven.COMPILE);
        new MyMenuItem("Clean", mavenMenu, Mandatory.Features.Maven.CLEAN);




        ContextMenu safetyMenu = new ContextMenu();
        MenuItem lockItem = new MenuItem("Lock");
        Interface.shortCut.add(lockItem);
        safetyMenu.getItems().add(lockItem);
        MenuItem cryptoSave = new MenuItem("CryptoSave");
        Interface.shortCut.add(cryptoSave);
        safetyMenu.getItems().add(cryptoSave);
        MenuItem SecurityCheck = new MenuItem("SecuCheck");
        Interface.shortCut.add(SecurityCheck);
        safetyMenu.getItems().add(SecurityCheck);

        // Crée le bouton d'exécution du code
        Button runButton = new Button("Exécuter");
        runButton.setOnAction(event -> {
            String javaCode = "caught hihi";
            try {
                javaCode = Files.readString(Paths.get(Interface.currTab.getId()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(javaCode);
            execute(Interface.currTab.getId());
        });

        //Show menus
        gitButton.setOnAction(event -> {
            gitMenu.show(gitButton, Side.BOTTOM, 0, 0);
        });

        anyButton.setOnAction(event -> {
            anyMenu.show(anyButton, Side.BOTTOM, 0, 0);
        });

        mavenButton.setOnAction(event -> {
            mavenMenu.show(mavenButton, Side.BOTTOM, 0, 0);
        });

        safetyButton.setOnAction(event -> {
            safetyMenu.show(safetyButton, Side.BOTTOM, 0, 0);
        });

        fileButton.setOnAction(event -> {
            fileMenu.show(fileButton, Side.BOTTOM, 0, 0);
        });

        openMenu.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File selectedDirectory = directoryChooser.showDialog(Interface.stage);
            if (selectedDirectory != null)
                System.out.println("Dossier sélectionné : " + selectedDirectory.getAbsolutePath());
            if(selectedDirectory == null)
                return;
            TreeItem<String> rootNode = new TreeItem<>(selectedDirectory.getAbsolutePath());
            rootNode.setExpanded(true);
            TreeBuilder.buildTree(selectedDirectory, rootNode);
            Interface.treeview.setRoot(rootNode);
            Interface.treeview.refresh();
            Interface.project = Interface.projectService.load(Paths.get(selectedDirectory.getAbsolutePath()));
            PasswordManager.startVaultProject();
        });
        
        addFileMenu.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save File");
            fileChooser.setInitialDirectory(new File(System.getProperty("user.dir")));
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null)
                saveTofile(file);
            refreshTreeView(rootPath);
        });

        lockItem.setOnAction(event ->
        {
            if (!PasswordManager.mdpIsPresent())
                PasswordManager.createMdp();
            Stage blockingStage = new Stage();
            blockingStage.initModality(Modality.APPLICATION_MODAL);
            blockingStage.initStyle(StageStyle.TRANSPARENT);

            // Remplir la fenêtre avec un fond
            Rectangle background = new Rectangle(0, 0, Screen.getPrimary().getBounds().getWidth(),
                    Screen.getPrimary().getBounds().getHeight());
            background.setFill(Color.rgb(0, 0, 0, 0));

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Mot de passe");
            dialog.setHeaderText(null);
            dialog.setContentText("Veuillez entrer le mot de passe :");
            Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

            VBox layout = new VBox(dialog.getDialogPane());
            layout.setAlignment(Pos.CENTER);

            Scene blockingScene = new Scene(new Group(background, layout));
            blockingStage.setScene(blockingScene);
            blockingStage.setOnCloseRequest(Event::consume);

            okButton.addEventFilter(ActionEvent.ACTION, open -> {
                TextField textField = dialog.getEditor();
                String mdp = textField.getText();
                if(PasswordManager.checkPassword(mdp))
                    blockingStage.close();
                else
                    open.consume();
            });

            blockingStage.showAndWait();
        });

        addFolderMenu.setOnAction(event -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("New Folder");
            dialog.setHeaderText("Enter folder name:");
            dialog.setContentText("Name:");

            dialog.showAndWait().ifPresent(name -> {
                String folderName = name.trim();
                if (!folderName.isEmpty()) {
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setTitle("Select Directory");
                    directoryChooser.setInitialDirectory(new File(System.getProperty("user.dir")));

                    File selectedDirectory = directoryChooser.showDialog(primaryStage);
                    if (selectedDirectory != null) {
                        File newDirectory = new File(selectedDirectory, folderName);
                        if (newDirectory.mkdir())
                            refreshTreeView(rootPath);
                    }
                }
            });
        });
        cryptoSave.setOnAction(event -> {
            if (Interface.currTab == null)
                return;
            Tab curr = Interface.currTab;
            String path = curr.getId();
            CryptoManager.encryptFile(path);
        });

        SecurityCheck.setOnAction(event -> {
            SecurityChecker securityChecker = new SecurityChecker();

            try {
                System.out.println(Files.readString(Paths.get(Interface.currTab.getId())));
                //CompletableFuture<Void> analysisFuture = securityChecker.analyzeCode(code);
                securityChecker.analyzeCode(Files.readString(Paths.get(Interface.currTab.getId())));
            } catch (ExceptionCollection | IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Crée la barre d'outils et ajoute les boutons
        ToolBar toolBar = new ToolBar();
        toolBar.getItems().addAll(fileButton, anyButton, gitButton, mavenButton, safetyButton, runButton);
        return toolBar;
    }

    private static void refreshTreeView(File rootPath){
        TreeItem<String> rootNode = new TreeItem<>(rootPath.getAbsolutePath());
        rootNode.setExpanded(true);
        TreeBuilder.buildTree(rootPath, rootNode);
        Interface.treeview.setRoot(rootNode);
        Interface.treeview.refresh();
        Interface.project = Interface.projectService.load(Paths.get(rootPath.getAbsolutePath()));
        Interface.updateTree();
    }

    static private void saveTofile(File file){
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("// A safe place to work");
            fileWriter.close();
        }
        catch (IOException e){
            //System.out.println(e);
            return;
        }
    }

    public static void execute(String filePath){
        try {
            // Read the Java code file as a string
            String javaCode = Files.readString(Paths.get(filePath));

            String className = extractClassName(javaCode);


            // Create a temporary Java source file
            Path sourcePath = Paths.get(className + ".java");
            Files.writeString(sourcePath, javaCode);

            // Get the Java compiler
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            // Compile the Java source file
            int compilationResult = compiler.run(null, null, null, sourcePath.toString());
            if (compilationResult == 0) {
                // Load the compiled class
                Class<?> compiledClass = Class.forName(className);

                // Find the main method
                Method mainMethod = compiledClass.getDeclaredMethod("main", String[].class);

                // Invoke the main method
                mainMethod.invoke(null, (Object) null);
            } else {
                System.err.println("Compilation failed!");
            }

            // Delete the temporary Java source file
            Files.deleteIfExists(sourcePath);
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static String extractClassName(String javaCode){
        String className = "";

        String regex = "class\\s+([a-zA-Z_$][a-zA-Z\\d_$]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(javaCode);

        if (matcher.find()) {
            className = matcher.group(1);
        }

        return className;
    }
}
