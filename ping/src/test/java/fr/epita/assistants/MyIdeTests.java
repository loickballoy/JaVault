package fr.epita.assistants;

import fr.epita.assistants.myide.domain.entity.*;
import fr.epita.assistants.myide.domain.service.ProjectService;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MyIdeTests {

    @Test
    void init_test() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        System.out.println("exec");
        Project pent = project.load(Paths.get(new File(".").getPath()));


        /*NodeService nser = project.getNodeService();
        //Project pent = project.load()

       // System.out.println(Paths.get(new File(".").getCanonicalPath()));
        Node root = pent.getRootNode();
        for (Node child : root.getChildren()){
            System.out.println("paths: " + child.getPath().getFileName().toString());
            if (child.getPath().getFileName().toString().equals("testDir")){
                System.out.println("in if");
                //String toInsert = "FUCK ERO1!";
                nser.delete(child);
                break;
            }
        }
        // System.out.println(nser.delete(newNode));
        System.out.println(root.getType());
        // nser.move(newNode, destNode);
        System.out.println();*/
        //System.out.println(root.getPath());

    }
}