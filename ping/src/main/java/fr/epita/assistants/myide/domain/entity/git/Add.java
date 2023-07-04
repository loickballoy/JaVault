package fr.epita.assistants.myide.domain.entity.git;

import fr.epita.assistants.myide.domain.entity.ExecutionReportEntity;
import fr.epita.assistants.myide.domain.entity.Feature;
import fr.epita.assistants.myide.domain.entity.Mandatory;
import fr.epita.assistants.myide.domain.entity.Project;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Add implements Feature {

    public ExecutionReport execute2(Project project, Object... params) {
        return new ExecutionReportEntity(gitAdd(project, params));
    }

    @Override
    public ExecutionReport execute(Project project, Object... params) {
        if (!Stream.of(params).map(p -> (String)p).allMatch(s -> s.contains("*") || s.equals(".") || Path.of(project.getRootNode().getPath() + "/" + s).toFile().exists()))
            return new ExecutionReportEntity(false);

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
        boolean resp = true;
        if (params == null) {
            repository.close();
            git.close();
            return new ExecutionReportEntity(false);
            // git.add().addFilepattern("").call();
        }
        else {
            List<String> parameters = new ArrayList<>();
            Arrays.stream(params).forEach(param -> {
            if (param instanceof String)
                parameters.add((String) param);
            });


            for (String p : parameters)
            {
                try {
                    File f = new File(project.getRootNode().getPath().toString() + "/" +p);
                    if (f.exists())
                         git.add().addFilepattern(p).call();
                    else
                    {
                        git.add().addFilepattern(p).call();
                        resp = false;
                    }
                } catch (GitAPIException e) {
                    return new ExecutionReportEntity(false);
                }
            }
        }
        repository.close();
        git.close();
        return new ExecutionReportEntity(resp);
    }

    private static boolean gitAdd(Project project, Object[] params) {
        if (!areAllParamsValid(project, params)) {
            return false;
        }

        try {
            Git git = openGitRepository(project);
            if (git == null) {
                return false;
            }

            boolean found = addFilesToGit(git, params);
            if (!found) {
                return false;
            }

            git.add().call();
            return true;
        } catch (IOException | GitAPIException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean areAllParamsValid(Project project, Object[] params) {
        return Stream.of(params)
                .map(p -> (String) p)
                .allMatch(s -> s.contains("*") || s.equals(".") || Path.of(project.getRootNode().getPath() + "/" + s).toFile().exists());
    }

    private static Git openGitRepository(Project project) throws IOException {
        Repository repository = FileRepositoryBuilder.create(new File(project.getRootNode().getPath().toString(), ".git"));
        return new Git(repository);
    }

    private static boolean addFilesToGit(Git git, Object[] params) {
        boolean found = false;
        var add = git.add();

        for (var param : params) {
            add.addFilepattern(param.toString());
            found = true;
        }

        return found;
    }


    @Override
    public Type type() {
        return Mandatory.Features.Git.ADD;
    }
}
