package fr.epita.assistants.myide.domain.entity.git;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;

import java.io.File;
import java.io.IOException;

/**
 * This is the Commit class where we handle the git.features: commit.
 *
 * @author Hamza.Ouhmani@epita.fr
 * @version 1.0
 */
public class Commit implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repository;
        try {
            repository = repositoryBuilder.setGitDir(
                    new File(project.getRootNode().getPath().toString() + "/.git"))
                    .readEnvironment().findGitDir().build();
        } catch (IOException e) {
            return new ExecutionReportEntity(false);
        }

        Git git = new Git(repository);

        try {
            if (params == null)
                git.commit().setMessage("").call();
             else
                git.commit().setMessage(params[0].toString()).call();
        }
        catch (GitAPIException e) {
            return new ExecutionReportEntity(false);
        }

        repository.close();
        git.close();
        return new ExecutionReportEntity(true);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.COMMIT;
    }
}
