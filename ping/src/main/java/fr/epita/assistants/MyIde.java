package fr.epita.assistants;

import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.service.ProjectServicesEntity;
import fr.epita.assistants.myide.domain.service.ProjectService;
import fr.epita.assistants.utils.Given;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Starter class, we will use this class and the init method to get a
 * configured instance of {@link ProjectService}.
 */
@Given(overwritten = false)
public class MyIde {

    /**
     * Init method. It must return a fully functional implementation of {@link ProjectService}.
     *
     * @return An implementation of {@link ProjectService}.
     */
    public static ProjectService init(final Configuration configuration) throws IOException {
        ProjectServicesEntity projectServicesEntity = new ProjectServicesEntity(configuration);
        return projectServicesEntity;
    }

    /**
     * Record to specify where the configuration of your IDE
     * must be stored. Might be useful for the search feature.
     */
    public record Configuration(Path indexFile,
                                Path tempFolder) {
    }

}
