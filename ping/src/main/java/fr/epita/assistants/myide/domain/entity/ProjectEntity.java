package fr.epita.assistants.myide.domain.entity;

import javax.validation.constraints.NotNull;
import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.*;

public class ProjectEntity implements Project {
    private Node rootNode;
    private Set<Aspect> aspects;

    public ProjectEntity(Path root) {
        File file = new File(root.toString());
        if (!file.exists())
            throw new InvalidPathException(root.toString(), "root node does not exist");

        rootNode = new NodeEntity(root);
        aspects = new HashSet<Aspect>();
        aspects.add(new AspectEntity(Mandatory.Aspects.ANY));
        for (Node child : rootNode.getChildren())
        {
            if (child.getPath().getFileName().toString().equals("pom.xml"))
                aspects.add(new AspectEntity(Mandatory.Aspects.MAVEN));
            else if (child.getPath().getFileName().toString().equals(".git"))
                aspects.add(new AspectEntity(Mandatory.Aspects.GIT));
        }
    }

    @Override
    public Node getRootNode() {
        return rootNode;
    }

    @Override
    public Set<Aspect> getAspects() {
        return aspects;
    }

    @Override
    public Optional<Feature> getFeature(Feature.Type featureType) {
        for (Aspect aspect : aspects)
        {
            for (Feature feature : aspect.getFeatureList())
            {
                if (feature.type().equals(featureType))
                    return Optional.of(feature);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<@NotNull Feature> getFeatures() {
        List<@NotNull Feature> ret = new ArrayList<Feature>();
        for (Aspect aspect : aspects)
        {
            ret.addAll(aspect.getFeatureList());
        }
        return ret;
    }
}
