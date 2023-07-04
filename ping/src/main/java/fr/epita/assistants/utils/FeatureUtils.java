package fr.epita.assistants.utils;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Project;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This is the Feature class where we create an util function to help us execute the different features (Git, maven, Any).
 *
 * @author loick.balloy@epita.fr guillaume.charrvolin@epita.fr
 * @version 1.0
 */
public class FeatureUtils {

    public static Feature.ExecutionReport FeatureExecute(Project project, String aspect, String type, Object... params){
        File projectDirectory = new File(project.getRootNode().getPath().toString());

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(projectDirectory);
        List<String> parameters = new ArrayList<>();
        parameters.add(aspect); parameters.add(type);
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
}
