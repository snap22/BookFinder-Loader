package com.example.loader.clients;

import com.example.loader.utils.DotEnvHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.submodule.SubmoduleStatus;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
public class GitHubClient {
    private final GitHub github;
    private final String githubToken;
    private final String githubUser;

    @Value("${github.repository.name}")
    private String repositoryName;

    public GitHubClient(DotEnvHolder dotEnvHolder) {
        try {
            github = GitHubBuilder.fromEnvironment().build();
        } catch (IOException e) {
            log.error("Error while creating GitHub instance", e);
            throw new RuntimeException(e);
        }

        githubToken = dotEnvHolder.getVariable("GITHUB_OAUTH");
        githubUser = dotEnvHolder.getVariable("GITHUB_USER");
    }

    /**
     * Clones the repository to the specified path
     *
     * @param repositoryPath Path to clone the repository to, will be overridden
     * @return Git instance
     */
    public Git cloneRepositoryToPath(String repositoryPath) {
        try {
            GHRepository repository = github.getRepository(repositoryName);

            File localRepositoryPath = new File(repositoryPath);
            if (localRepositoryPath.exists()) {
                FileUtils.deleteDirectory(localRepositoryPath);
            }

            Git git = Git.cloneRepository()
                    .setURI(repository.getHttpTransportUrl())
                    .setDirectory(localRepositoryPath)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            githubUser,
                            githubToken
                    ))
                    .call();

            Map<String, SubmoduleStatus> statuses = git.submoduleStatus().call();

            // Check if there are no submodules
            if (statuses.isEmpty()) {
                throw new RuntimeException("No submodules found in the repository! At least 1 should be present!");
            }

            // Initialize submodules
            git.submoduleInit().call();
            git.submoduleUpdate().call();

            return git;
        } catch (IOException | GitAPIException e) {
            log.error("Error while cloning repository", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Commits the changes to the repository
     *
     * @param git     Git instance
     * @param message Commit message
     */
    public void commitChanges(Git git, String message) {
        try {
            git.add().addFilepattern(".").call();

            // Stage updated & deleted files, without this the deleted files won't be staged
            git.add().setUpdate(true).addFilepattern(".").call();

            git.commit().setMessage(message).call();
        } catch (GitAPIException e) {
            log.error("Error while committing changes", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Pushes the changes to the repository
     *
     * @param git Git instance
     */
    public void pushChanges(Git git) {
        try {
            git.push()
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider(
                            githubUser,
                            githubToken
                    ))
                    .call();
        } catch (GitAPIException e) {
            log.error("Error while pushing changes", e);
            throw new RuntimeException(e);
        }
    }
}
