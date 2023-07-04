package fr.epita.assistants.myide.domain.service;

import fr.epita.assistants.MyIde;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.entity.ProjectEntity;
import fr.epita.assistants.myide.domain.service.NodeService;
import fr.epita.assistants.myide.domain.service.ProjectService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is the ProjectServiceEntity class where we implement the given ProjectService interface.
 *
 * @author remy.decourcelle@epita.fr guillaume.charvolin@epita.fr
 * @version 1.0
 */
public class ProjectServicesEntity implements ProjectService {

    private MyIde.Configuration configuration;
    private ProjectEntity project;
    private NodeServiceEntity nodeServiceEntity;


    public ProjectServicesEntity(MyIde.Configuration configuration) throws IOException {
        this.configuration = configuration;
        this.project = null;
        this.nodeServiceEntity = null;
    }

    @Override
    public Project load(Path root) {
        project = new ProjectEntity(root);
        nodeServiceEntity = new NodeServiceEntity(project);
        return project;
    }

    @Override
    public Feature.ExecutionReport execute(Project project, Feature.Type featureType, Object... params) {
        return null;
    }

    @Override
    public NodeService getNodeService() {
        if (nodeServiceEntity == null) {
            throw new NullPointerException("Project has not been loaded");
        }
        return nodeServiceEntity;
    }
}
