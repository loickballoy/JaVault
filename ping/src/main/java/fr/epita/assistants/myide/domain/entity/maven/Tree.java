package fr.epita.assistants.myide.domain.entity.maven;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.utils.FeatureUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tree implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        File projectDirectory = new File(project.getRootNode().getPath().toString());

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(projectDirectory);

        processBuilder.redirectOutput(new File("output.txt"));

        List<String> parameters = new ArrayList<>();
        parameters.add("mvn"); parameters.add("dependency:tree");

        Arrays.stream(params).forEach(param -> {
            if (param instanceof String)
                parameters.add((String) param);
        });

        processBuilder.command(parameters.toArray(new String[0]));

        try {
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                // System.out.println("hey");
                return new ExecutionReportEntity(true);
            } else {
                // System.out.println("bye");
                return new ExecutionReportEntity(false);
            }
        }
        catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
        return new ExecutionReportEntity(false);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.TREE;
    }
}
