package fr.epita.assistants.myide.domain.entity.maven;

import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.utils.FeatureUtils;

/**
 * This is the Install class where we handle the mvn.features: install.
 *
 * @author loick.balloy@epita.fr
 * @version 1.0
 */
public class Install implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        return FeatureUtils.FeatureExecute(project, "mvn","install", params);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Maven.INSTALL;
    }
}
