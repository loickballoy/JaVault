package fr.epita.assistants;

import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Node;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GitTests {

    @Test
    void AddExecute() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        // System.out.println("exec");
        Project pent = project.load(Paths.get(new File("./src/test/GitProjectTest").getPath()));

        System.out.println("path: " + pent.getRootNode().getPath().toString());
        for (Node child : pent.getRootNode().getChildren()){
            System.out.println("child: " + child.getPath().toString());
        }

        System.out.print("pent aspect list: ");
        pent.getAspects().forEach((aspect -> {
            System.out.printf(aspect.getType().toString() + " ");
        }));

        Boolean bool = pent.getFeature(Mandatory.Features.Git.ADD).get().execute(pent, ".").isSuccess();
        bool = pent.getFeature(Mandatory.Features.Git.ADD).get().execute(pent, "khjbeaouj").isSuccess();
        //bool = pent.getFeature(Mandatory.Features.Git.PUSH).get().execute(pent).isSuccess();
        System.out.println("response: " + bool);
    }

    @Test
    void PushExecute() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        // System.out.println("exec");
        Project pent = project.load(Paths.get(new File("./src/test/GitProjectTest").getPath()));

        System.out.println("path: " + pent.getRootNode().getPath().toString());
        for (Node child : pent.getRootNode().getChildren()){
            System.out.println("child: " + child.getPath().toString());
        }

        System.out.print("pent aspect list: ");
        pent.getAspects().forEach((aspect -> {
            System.out.printf(aspect.getType().toString() + " ");
        }));

        Boolean bool = pent.getFeature(Mandatory.Features.Git.PUSH).get().execute(pent, "Wilharch", "Autruche4240??").isSuccess();
    }
    @Test
    void PullExecute() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        // System.out.println("exec");
        Project pent = project.load(Paths.get(new File("./src/test/GitProjectTest").getPath()));

        System.out.println("path: " + pent.getRootNode().getPath().toString());
        for (Node child : pent.getRootNode().getChildren()){
            System.out.println("child: " + child.getPath().toString());
        }

        System.out.print("pent aspect list: ");
        pent.getAspects().forEach((aspect -> {
            System.out.printf(aspect.getType().toString() + " ");
        }));

        Boolean bool = pent.getFeature(Mandatory.Features.Git.PULL).get().execute(pent).isSuccess();
    }
}
