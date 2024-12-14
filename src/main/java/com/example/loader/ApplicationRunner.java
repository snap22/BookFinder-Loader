package com.example.loader;

import com.example.loader.models.User;
import com.example.loader.services.IDataDownloadService;
import com.example.loader.services.IDataUploadService;
import com.example.loader.services.IExternalRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ApplicationRunner implements CommandLineRunner {

    private final IDataDownloadService dataDownloadService;
    private final IDataUploadService dataUploadService;
    private final IExternalRepositoryService externalRepositoryService;

    @Override
    public void run(String... args) {
//        List<User> users = dataDownloadService.downloadUsers();
//        dataUploadService.uploadUsers(users);

        externalRepositoryService.downloadRepository();
    }
}
