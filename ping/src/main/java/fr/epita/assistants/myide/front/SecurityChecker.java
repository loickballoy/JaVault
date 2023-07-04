package fr.epita.assistants.myide.front;

import org.owasp.dependencycheck.Engine;
import org.owasp.dependencycheck.dependency.Dependency;
import org.owasp.dependencycheck.dependency.Vulnerability;
import org.owasp.dependencycheck.exception.ExceptionCollection;
import org.owasp.dependencycheck.utils.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SecurityChecker {
    private Engine engine;
    private Executor executor;

    public SecurityChecker() {
        Settings settings = new Settings();
        this.engine = new Engine(settings); // Instantiate the OWASP Dependency Check engine
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void analyzeCode(String code) throws ExceptionCollection {

        // Save the code to a temporary file
        File tempFile;
        try {
            tempFile = File.createTempFile("code", ".java");
            FileWriter writer = new FileWriter(tempFile);
            writer.write(code);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Add the temporary file to the OWASP Dependency Check engine
        engine.scan(tempFile.getAbsolutePath());

        // Get the analysis results
        engine.analyzeDependencies();

        // Process the analysis results
        Dependency[] dependencies = engine.getDependencies();
        System.out.println("Possible Vulnerabilities: " + dependencies.length);
        for (Dependency dependency : dependencies) {
            Vulnerability[] vulnerabilities = dependency.getVulnerabilities().toArray(new Vulnerability[0]);
            System.out.println("Vulnerabilities:" + vulnerabilities.length);
            if (vulnerabilities.length > 0) {
                System.out.println("Security Breach in dependency: " + dependency.getFileName());
                for (Vulnerability vulnerability : vulnerabilities) {
                    System.out.println(" - " + vulnerability.getName());
                }
            }
        }

        // Clean up the temporary file
        tempFile.delete();


    }
}