package fr.epita.assistants.utils.front;

import fr.epita.assistants.myide.front.Interface;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaTheme {
    private static final Color typeColor = Color.RED;
    private static final Color keywordColor = Color.BLUE;
    private static final Set<String> nativeTypes = Set.of("boolean", "byte", "short", "int", "long", "float", "double", "char", "String");
    private static final Set<String> nativeKeywords = Set.of("abstract", "assert", "break", "case", "catch", "class", "const", "continue", "default", "do", "else",
            "enum", "extends", "final", "finally", "for", "if", "implements", "import", "instanceof", "interface", "native", "new", "package", "private",
            "protected", "public", "return", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void",
            "volatile", "while");

    //private static final String COMMENT_PATTERN = "//[^\\\\n]*|/\\\\*(.|\\\\R)*?\\\\*/";
    private static final String COMMENT_PATTERN = "//.*|/\\*(.|\\R)*?\\*/";
    private static final Pattern keywordPattern = Pattern.compile(
            "(?<KEYWORD>\\b(abstract|assert|break|case|catch|class|const|continue|default|do|else|enum|extends|final|finally|for|if|implements|import|instanceof|interface|native|new|package|private|protected|public|return|static|strictfp|super|switch|synchronized|this|throw|throws|transient|try|void|volatile)\\b)"
                    + "|(?<TYPE>\\b(while|boolean|byte|short|int|long|float|double|char|String|var)\\b)"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<ANNOTATIONS>@\\w+(?:\\.\\w+)*(\\((?:[^()]*|\\([^()]*\\))*\\))?)"
                    + "|(?<STRINGS>)" + "\"([^\"]*)\""
                    /*+ "|(?<DEFAULT>[^\\\\s]+)"*/);



    public static void updateLineNumbers(CodeArea editor, CodeArea lineNumbers, ScrollBar scrollBar) {

        int nblines= lineNumbers.getParagraphs().size();
        int totalLines = editor.getParagraphs().size();
        if(totalLines == nblines)
            return;

        if (totalLines < nblines) {
            int startPos = lineNumbers.position(totalLines, 0).toOffset();
            int endPos = lineNumbers.position(nblines, 0).toOffset();
            lineNumbers.deleteText(startPos-1, endPos-1);
            return;
        }

        StringBuilder numbers = new StringBuilder();
        for (int i = nblines; i < totalLines; i++) {
            numbers.append(i).append("\n");
        }
        lineNumbers.appendText(numbers.toString());
        JavaTheme.applyThemeOpen(lineNumbers);
        scrollBar.setValue(scrollBar.getValue()-0.01);
    }

    public static void applyTheme(CodeArea editor) {
        //Créer un Text avec le nouveau texte
        String currW = editor.getText(0,editor.getCaretPosition());
        String[] words = currW.split("\\s+");
        final String currWord = words[words.length-1];
        editor.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> {
                    if (getLenToSpace(editor) < 4)
                        return;
                    editor.setStyleSpans(editor.getCaretPosition() - getLenToSpace(editor), computeHighlighting(editor.getText().substring(editor.getCaretPosition() - getLenToSpace(editor))));
                });
        editor.getStylesheets().add("styles.css");
    }

    private static int getLenToSpace(CodeArea editor){
        int length = 1;
        int start = editor.getCaretPosition();
        if (start < 20)
            return start;
        char c = editor.getText().charAt(start - length);
        while (c != ' ' && c != '\n'){
            length += 1;
            c = editor.getText().charAt(start - length);
        }
        return length;
    }



    public static void applyThemeOpen(CodeArea editor)
    {//Créer un Text avec le nouveau texte
        editor.setStyleSpans(0, computeHighlighting(editor.getText()));
        editor.getStylesheets().add("lines.css");
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String code) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        Matcher matcher;

        // Apply keyword style
        matcher = keywordPattern.matcher(code);
        int lastKeywordEnd = 0;
        while (matcher.find()) {
            String styleClass = matcher.group("KEYWORD") != null ? "keyword" :
                                matcher.group("TYPE") != null ? "type" :
                                matcher.group("COMMENT") != null ? "comment" :
                                matcher.group("ANNOTATIONS") != null ? "annotation" :
                                matcher.group("STRINGS") != null ? "strings" :
                                "default-code";
            spansBuilder.add(Collections.singleton("default-code"), matcher.start() - lastKeywordEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKeywordEnd = matcher.end();
        }
        spansBuilder.add(Collections.singleton("default-code"), code.length() - lastKeywordEnd);
        return spansBuilder.create();
    }



    public static void autoComplete(CodeArea editor, ListView<String> suggest)
    {
        int pos = editor.getCaretPosition();
        if(pos > 1 && !editor.getText(pos-1,pos).equals(" ") && !editor.getText(pos-1,pos).equals("\n"))
        {
            String currWord = editor.getText(0,pos);
            String[] words = currWord.split("\\s+");
            currWord = words[words.length-1];
            Set<String> propose = new HashSet<>();
            for (String str : Interface.autoComplete)
            {
                if(str.startsWith(currWord) && !str.equals(currWord)) {
                    propose.add(str);
                }
            }

            suggest.getItems().setAll(propose);
            suggest.getStyleClass().add("suggest");
            suggest.setVisible(true);
            suggest.setMinHeight(75);
            if(propose.isEmpty()){
                FileManager.removeSuggestionList(editor,suggest);
            }
            return;
        }
        FileManager.removeSuggestionList(editor,suggest);

    }
}
