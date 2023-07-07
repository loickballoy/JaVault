package fr.epita.assistants.utils.front;

import fr.epita.assistants.Interface;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * This is the FileManger class which creates functions to help us handle the files opened in our IDE
 *
 * @author loick.balloy@epita.fr remy.decourcelle@epita.fr
 * @version 1.0
 */
public class FileManager {

    private static boolean listenerLinesEnabled = true;
    private static boolean listenerEnabled = true;


    public static String readFileContent(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                contentBuilder.append(line).append("\n");
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            //System.out.println("Le fichier n'a pas été trouvé : " + filePath);
            return "";
        }
        return contentBuilder.toString();
    }

    private static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
    public static void openFile(TreeItem<String> item)
    {
        if(!item.isLeaf())
            return;
        File file = new File(item.getValue());
        Path path = Paths.get(item.getValue());
        String fileContent;
        String fileExtension = getFileExtension(path);
        if ("vault".equals(fileExtension)) {
            try {
                fileContent = CryptoManager.decryptAES(CryptoManager.readByte(path.toString()), PasswordManager.getKey());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else
            fileContent = readFileContent(item.getValue());
        String fileName = file.getName();
        if (Interface.tabOpened.contains(fileName))
            return;
        VBox Veditor = createTab(fileName, item.getValue());
        CodeArea codeArea = (CodeArea) Veditor.getChildren().get(0);
        ListView<String> suggestArea = (ListView<String>) Veditor.getChildren().get(1);
        Interface.tabOpened.add(fileName);
        getAutoComplete(fileContent);
        codeArea.appendText(fileContent);
        codeArea.setPrefHeight(5000);
        removeSuggestionList(codeArea, suggestArea);
        JavaTheme.applyThemeOpen(codeArea);
    }

    private static VBox createTab(String name, String path)
    {
        //LINE NUMBERS
        CodeArea lineNumbers = new CodeArea();
        lineNumbers.setEditable(false);
        //lineNumbers.getStyleClass().add("line-numbers");
        lineNumbers.setBackground(new Background(new BackgroundFill(Color.GREY, CornerRadii.EMPTY, Insets.EMPTY)));
        lineNumbers.setPrefWidth(25);
        lineNumbers.setPrefHeight(5000);
        lineNumbers.setStyle("-fx-font-size: 14px;  -fx-text-fill: white;");



        CodeArea editor = new CodeArea();
        ListView<String> suggestionList = new ListView<>();
        VBox Veditor = new VBox(editor, suggestionList);
        editor.setStyle("-fx-caret-color: white;");
        //editor.setStyle("-fx-font-size: 14px;");
        suggestionList.setVisible(false);
        suggestionList.setMinHeight(0);
        suggestionList.setMaxHeight(0);
        editor.setPrefHeight(5000);

        editor.getStyleClass().add("code-area");

        suggestionList.setOnMouseClicked(event -> {
            String selectedWord = suggestionList.getSelectionModel().getSelectedItem();
            if (selectedWord != null) {
                FileManager.insertSelectedWord(selectedWord, editor);
                removeSuggestionList(editor,suggestionList);
            }
        });

        ScrollBar scrollBar = new ScrollBar();
        scrollBar.adjustValue(1.0 - editor.getEstimatedScrollY());
        scrollBar.setOrientation(Orientation.VERTICAL);


        editor.addEventFilter(ScrollEvent.SCROLL, scroll ->
        {
            scrollBar.setValue((editor.getEstimatedScrollY()*100)/(editor.totalHeightEstimateProperty().getValue()-editor.getHeight()));
            lineNumbers.scrollBy(0, scroll.getDeltaY());
            editor.scrollBy(scroll.getDeltaX(), scroll.getDeltaY());
            scroll.consume();
        });

        lineNumbers.addEventFilter(ScrollEvent.ANY, Event::consume);

        scrollBar.valueProperty().addListener( scroll ->
        {
            double place = (scrollBar.getValue()/100)*(editor.totalHeightEstimateProperty().getValue()-editor.getHeight());
            lineNumbers.scrollBy(0,place-lineNumbers.getEstimatedScrollY());
            editor.scrollBy(editor.getEstimatedScrollX(), place-editor.getEstimatedScrollY());
        });

        StackPane content1 = new StackPane();

        GridPane gridPane = new GridPane();
        gridPane.add(lineNumbers, 0, 0);
        gridPane.add(Veditor, 1, 0);
        gridPane.add(scrollBar, 2, 0);

        GridPane.setVgrow(lineNumbers, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHalignment(lineNumbers, HPos.LEFT);
        GridPane.setVgrow(Veditor, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(Veditor, Priority.ALWAYS);
        GridPane.setHalignment(scrollBar, HPos.RIGHT);


        content1.getChildren().addAll(gridPane);
        Tab tab1 = new Tab(name);
        tab1.setContent(content1);
        tab1.setId(path);
        tab1.getStyleClass().add("tab");

        tab1.setOnCloseRequest(event -> {
            Interface.tabOpened.remove(name);
            //event.consume();
        });
        Interface.tabPane.getTabs().addAll(tab1);

        UpdateTimer updateTimer = new UpdateTimer();

        // Ajouter un écouteur de texte pour détecter les modifications du contenu du TextArea
        editor.textProperty().addListener((obs, oldText, newText) ->
        {
            if(listenerLinesEnabled)
            {
                listenerLinesEnabled = false;
                JavaTheme.updateLineNumbers(editor, lineNumbers, scrollBar);
                listenerLinesEnabled = true;
            }
            if(listenerEnabled)
            {
                listenerEnabled = false;

                if (!updateTimer.globalVariable)
                    JavaTheme.applyTheme(editor);
                else {
                    updateTimer.startTimer();
                    JavaTheme.applyThemeOpen(editor);
                    updateTimer.updateGlobalVariable();
                }

                JavaTheme.autoComplete(editor, suggestionList);
                listenerEnabled = true;
            }
        });
        return Veditor;
    }

    private static void getAutoComplete(String content)
    {
        content = content.replaceAll("/\\*(?s).*?\\*/", "");
        String[] tokens = content.split("\\s+|:|;|\\{|}|/|\\(|\\)|\\[|]");
        for (String token : tokens)
        {
            if (token.length() > 1 && token.length() < 50) {
                Interface.autoComplete.add(token);
            }
        }
    }

    private static void insertSelectedWord(String selectedWord, CodeArea codeArea)
    {
        int pos = codeArea.getCaretPosition();
        String text = codeArea.getText();
        String currWord = codeArea.getText(0,pos);
        String[] words = currWord.split("\\s+");
        currWord = words[words.length-1];
        String beforeCaret = text.substring(0, pos-currWord.length());
        String afterCaret = text.substring(pos);
        String modifiedText = beforeCaret + selectedWord + afterCaret;
        codeArea.replaceText(modifiedText);
    }

    public static void removeSuggestionList(CodeArea editor, ListView<String> suggest)
    {
        suggest.setVisible(false);
        suggest.setMinHeight(0);
        suggest.setMaxHeight(0);
        editor.setPrefHeight(5000);
        suggest.getItems().clear();
    }
}
