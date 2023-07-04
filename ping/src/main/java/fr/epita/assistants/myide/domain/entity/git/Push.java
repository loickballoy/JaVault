package fr.epita.assistants.myide.domain.entity.git;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import fr.epita.assistants.myide.domain.entity.maven.Exec;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Push implements Feature {

    @Override
    public ExecutionReport execute(Project project, Object... params){
        return new ExecutionReportEntity(gitPush(project, params));
    }

    public ExecutionReport execute2(Project project, Object... params){
        if (isRepositoryUpToDate(project.getRootNode().getPath().toString() + "./git"))
            return new ExecutionReportEntity(false);

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

        Iterable<PushResult> pushResults;

        try {
            pushResults = git.push().setRemote(remoteUri.toString()).call();
        } catch (GitAPIException | JGitInternalException e) {
            return new ExecutionReportEntity(false);
        }

        for (PushResult pushResult : pushResults) {
            for (RemoteRefUpdate remoteUpdate : pushResult.getRemoteUpdates()) {
                if (remoteUpdate.getStatus() == RemoteRefUpdate.Status.OK)
                    return new ExecutionReportEntity(true);
                else
                    return new ExecutionReportEntity(false);
            }
        }

        repository.close();
        git.close();
        return new ExecutionReportEntity(true);
    }

    @Override
    public Type type() {
        return Mandatory.Features.Git.PUSH;
    }

    private static boolean isRepositoryUpToDate(String repositoryPath) {
        try {
            // Ouvrir le référentiel Git
            Repository repository = new FileRepositoryBuilder()
                    .setGitDir(new File(repositoryPath))
                    .build();

            // Récupérer la branche courante
            Ref head = repository.exactRef(repository.getFullBranch());

            // Récupérer l'ID du dernier commit du référentiel distant
            ObjectId latestCommitId = repository.resolve("origin/" + head.getName());

            // Récupérer l'ID du commit courant du référentiel local
            ObjectId currentCommitId = head.getObjectId();

            // Comparer les IDs de commit
            boolean isUpToDate = latestCommitId.equals(currentCommitId);

            // Fermer le référentiel
            repository.close();

            return isUpToDate;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean gitPush(Project project, Object[] params) {
        try {
            Git git = openGitRepository(project);
            if (git == null) {
                return false;
            }

            Iterable<PushResult> results = git.push().call();
            return areAllUpdatesSuccessful(results);
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Git openGitRepository(Project project) throws IOException {
        Repository repository = FileRepositoryBuilder.create(new File(project.getRootNode().getPath().toString(), ".git"));
        return new Git(repository);
    }

    private static boolean areAllUpdatesSuccessful(Iterable<PushResult> results) {
        boolean allOk = true;

        for (PushResult result : results) {
            for (RemoteRefUpdate update : result.getRemoteUpdates()) {
                if (update.getStatus() != RemoteRefUpdate.Status.OK) {
                    allOk = false;
                    break;
                }
            }
        }

        return allOk;
    }
}
