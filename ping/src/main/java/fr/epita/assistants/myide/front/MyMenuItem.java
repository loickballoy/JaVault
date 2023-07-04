package fr.epita.assistants.myide.front;

import fr.epita.assistants.myide.domain.entity.Feature;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * This is the MyMenuItem class which creates functions to link our backend features to the frontend. It is not exclusively that but mostly used for that in our MyToolBar class.
 *
 * @author guillaume.charvolin@epita.fr
 * @version 1.0
 */
public class MyMenuItem {

    MenuItem menuItem;
    String name;
    ContextMenu contextMenu;

    public MyMenuItem(String name, ContextMenu contextMenu, Feature.Type feature, Object... params) {
        this.name = name;
        this.contextMenu = contextMenu;
        this.menuItem = new MenuItem(name);
        var featureEffective = Interface.project.getFeature(feature);
        if (featureEffective.isEmpty())
            throw new RuntimeException("MenuItem:" + name + " feature is Invalid and not have been Find");  
        contextMenu.getItems().add(menuItem);
        menuItem.setOnAction(event -> {
            System.err.println(Interface.project.getRootNode().getPath());
            System.err.println("heyeyyy");
            if (featureEffective.get().execute(Interface.project, params).isSuccess())
                System.out.println(name + ": It Work");
            else
                System.out.println(name + ": Its dont Work");
        });
    }
}
