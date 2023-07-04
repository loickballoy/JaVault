package fr.epita.assistants.myide.domain.entity.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.utils.FeatureUtils;

public class Compile implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return FeatureUtils.FeatureExecute(project, "mvn","compile", params);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.COMPILE;
    }
}
