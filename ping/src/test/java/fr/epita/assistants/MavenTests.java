package fr.epita.assistants;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class MavenTests {

    @Test
    void CompileExecute() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        // System.out.println("exec");
        Project pent = project.load(Paths.get(new File("./src/test/TestProject").getPath()));

        System.out.println("path: " + pent.getRootNode().getPath().toString());
        for (Node child : pent.getRootNode().getChildren()){
            System.out.println("child: " + child.getPath().toString());
        }

        System.out.print("pent aspect list: ");
        pent.getAspects().forEach((aspect -> {
            System.out.printf(aspect.getType().toString() + " ");
        }));

        pent.getFeature(Mandatory.Features.Maven.COMPILE).get().execute(pent, "-c");
    }

    @Test
    void TreeExecute() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        // System.out.println("exec");
        Project pent = project.load(Paths.get(new File("./src/test/TestProject").getPath()));

        System.out.println("path: " + pent.getRootNode().getPath().toString());
        for (Node child : pent.getRootNode().getChildren()){
            System.out.println("child: " + child.getPath().toString());
        }

        System.out.print("pent aspect list: ");
        pent.getAspects().forEach((aspect -> {
            System.out.printf(aspect.getType().toString() + " ");
        }));

        Boolean result = pent.getFeature(Mandatory.Features.Maven.TREE).get().execute(pent).isSuccess();
        System.out.println("result: " + result);
    }
}