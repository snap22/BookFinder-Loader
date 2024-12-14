package com.example.loader.services.implementation;

import com.example.loader.services.IExternalRepositoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class GitHubRepositoryService implements IExternalRepositoryService {

    private final GitHub github;

    public GitHubRepositoryService() {
        try {
            github = GitHubBuilder.fromEnvironment().build();
        } catch (IOException e) {
            log.error("Error while creating GitHub instance", e);
            throw new RuntimeException(e);
        }
    }



    @Override
    public void downloadRepository() {
        try {
            GHRepository repository = github.getRepository("snap22/BookFinder-website");


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void uploadRepository() {

    }
}
