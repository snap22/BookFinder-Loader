package com.example.loader.services.implementation;

import com.example.loader.clients.GitHubClient;
import com.example.loader.models.User;
import com.example.loader.processors.TemplateProcessor;
import com.example.loader.services.IExternalRepositoryService;
import com.example.loader.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubRepositoryService implements IExternalRepositoryService {

    @Value("${tmp.dir.path}")
    private String tmpDir;

    private final TemplateProcessor templateProcessor;
    private final GitHubClient gitHubClient;


    @Override
    public void setupUsers(List<User> users) {
        Git git = gitHubClient.cloneRepositoryToPath(tmpDir);

        Path contentPath = Path.of(tmpDir, "content");

        // Clear directory
        clearDirectory(contentPath.toFile());

        createContentPageForUsersInPath(users, contentPath);

        String currentTimestamp = DateUtils.convertLocalDateTimeToPattern(LocalDateTime.now(), "yyyy-MM-dd HH:mm:ss");
        gitHubClient.commitChanges(git, String.format("[%s] setup users", currentTimestamp));

        // Push changes
        gitHubClient.pushChanges(git);

        // Clear local repository directory
        clearDirectory(new File(tmpDir));

        log.info("Setup users completed [{}]", currentTimestamp);
    }

    public void createContentPageForUsersInPath(List<User> users, Path contentPath) {
        for (User user : users) {
            createContentPageForUser(user, contentPath);
        }
        log.info("Created content pages for {} users", users.size());
    }

    public void createContentPageForUser(User user, Path contentPath) {
        Path userPath = Path.of(contentPath.toString(), user.getNormalizedName());
        try {
            Files.createDirectories(userPath);

            String previewContent = processTemplateForUser("src/main/resources/templates/user_preview.md", user);
            Path previewFilePath = Path.of(userPath.toString(), "preview.md");
            Files.writeString(previewFilePath, previewContent);

            String searchContent = processTemplateForUser("src/main/resources/templates/user_search.md", user);
            Path searchFilePath = Path.of(userPath.toString(), "search.md");
            Files.writeString(searchFilePath, searchContent);
        } catch (IOException ex) {
            log.error("Error while creating content page for user", ex);
            throw new RuntimeException(ex);
        }
    }

    private void clearDirectory(File file) {
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            log.error("Error while clearing directory", e);
            throw new RuntimeException(e);
        }
    }

    private String processTemplateForUser(String templatePath, User user) {
        try {
            return templateProcessor.processTemplate(
                    templatePath,
                    Map.of(
                            "username", user.getNormalizedName(),
                            "userId", user.getExternalId()
                    )
            );
        } catch (IOException e) {
            log.error("Error while processing template for user", e);
            throw new RuntimeException(e);
        }
    }

}


