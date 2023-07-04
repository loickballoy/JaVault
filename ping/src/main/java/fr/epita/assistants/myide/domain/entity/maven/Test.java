package fr.epita.assistants.myide.domain.entity.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.utils.FeatureUtils;

public class Test implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return FeatureUtils.FeatureExecute(project, "mvn","test", params);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.TEST;
    }
}