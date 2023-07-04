package fr.epita.assistants;

import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectService;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AnyTests {

    @Test
    void cleanup_exec() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        System.out.println("exec");
        Project pent = project.load(Paths.get(new File(".").getPath()));
        System.err.println("call cleanup");
    }

    @Test
    void dist_exec() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        System.out.println("exec");
        Project pent = project.load(Paths.get(new File("../ping").getPath()));
        pent.getFeature(Mandatory.Features.Any.DIST).get().execute(pent);
        System.err.println("call dist");
    }

    @Test
    void search_exec() throws IOException {
        Path indexFile = Paths.get("/indexFile");
        Path tempFolder = Paths.get("/tempFolder/");
        ProjectService project = MyIde.init(new MyIde.Configuration(indexFile, tempFolder));
        //System.out.println("exec");
        Project pent = project.load(Paths.get(new File(".").getPath()));
        System.err.println(pent.getFeature(Mandatory.Features.Any.SEARCH).get().execute(pent, "CLASS").isSuccess());
        //System.err.println("call dist");
    }

}
