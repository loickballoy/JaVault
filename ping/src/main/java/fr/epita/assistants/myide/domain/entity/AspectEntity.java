package fr.epita.assistants.myide.domain.entity;

import fr.epita.assistants.myide.domain.entity.any.Cleanup;
import fr.epita.assistants.myide.domain.entity.any.Dist;
import fr.epita.assistants.myide.domain.entity.any.Search;
import fr.epita.assistants.myide.domain.entity.git.Add;
import fr.epita.assistants.myide.domain.entity.git.Commit;
import fr.epita.assistants.myide.domain.entity.git.Pull;
import fr.epita.assistants.myide.domain.entity.git.Push;
import fr.epita.assistants.myide.domain.entity.maven.*;
import fr.epita.assistants.myide.domain.entity.maven.Package;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the AspectEntity class where we implement the given Aspect interface.
 *
 * @author remy.decourcelle@epita.fr guillaume.charvolin@epita.fr
 * @version 1.0
 */
public class AspectEntity implements Aspect{

    private Type type;
    private List<Feature> features;

    public AspectEntity(Type type)
    {
        this.type = type;
        this.features = new ArrayList<Feature>();
        if (type == Mandatory.Aspects.ANY)
        {
            features.add(new Cleanup());
            features.add(new Dist());
            features.add(new Search());
        }
        else if (type == Mandatory.Aspects.MAVEN)
        {
            features.add(new Compile());
            features.add(new Clean());
            features.add(new Test());
            features.add(new Package());
            features.add(new Install());
            features.add(new Exec());
            features.add(new Tree());
        }
        else if (type == Mandatory.Aspects.GIT)
        {
            features.add(new Pull());
            features.add(new Add());
            features.add(new Commit());
            features.add(new Push());
        }
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public List<Feature> getFeatureList() {
        return features;
    }
}
