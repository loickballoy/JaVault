package fr.epita.assistants.myide.domain.entity.git;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This is the Pull class where we handle the git.features: pull.
 *
 * @author Guillaume.charvolin@epita.fr
 * @version 1.0
 */
public class Pull implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        RepositoryBuilder repositoryBuilder = new RepositoryBuilder();
        Repository repository;
        String repositoryURL;
        try {
            repository = repositoryBuilder.setGitDir(
                            new File(project.getRootNode().getPath().toString() + "/.git"))
                    .readEnvironment().findGitDir().build();
            repositoryURL = repository.getConfig().getString("remote", "origin", "url");
            System.out.println(repositoryURL);
        } catch (IOException e) {
            return new ExecutionReportEntity(false);
        }

        Git git = null;
        try {
            git = Git.open(new File(project.getRootNode().getPath().toString()));
        } catch (IOException e) {
            return new ExecutionReportEntity(false);
        }
        URIish remoteUri;
        try {
            remoteUri = new URIish(repositoryURL);
        } catch (URISyntaxException e) {
            return new ExecutionReportEntity(false);
        }
        CredentialsProvider credentialsProvider;
        if (params.length != 0 && params[0] != null && params[1] != null)
            credentialsProvider = new UsernamePasswordCredentialsProvider(params[0].toString(), params[1].toString());
        else
            credentialsProvider = new UsernamePasswordCredentialsProvider("", "");

        try {
            System.out.println("\n remote URI: " + remoteUri.toString() + "\n");
            git.pull().call();
        } catch (GitAPIException | JGitInternalException e)
        {
            return new ExecutionReportEntity(false);
        }

        repository.close();
        git.close();
        return new ExecutionReportEntity(true);
        //return FeatureUtils.FeatureExecute(project, "git","pull", params);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.PULL;
    }
}
