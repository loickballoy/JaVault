package fr.epita.assistants.utils.front;

import javafx.scene.control.TreeItem;

import java.io.File;

/**
 * This is the TreeBuilder class which we use for the display of our directory tree
 *
 * @author remy.decourcelle@epita.fr
 * @version 1.0
 */
public class TreeBuilder {
    public static void buildTree(File directory, TreeItem<String> parentItem)
    {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                TreeItem<String> newItem = new TreeItem<>(parentItem.getValue()+"\\"+file.getName());
                parentItem.getChildren().add(newItem);
                if (file.isDirectory()) {
                    buildTree(file, newItem);
                }
            }
        }
    }
}
