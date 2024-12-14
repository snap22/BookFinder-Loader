package com.example.loader;

import com.example.loader.dto.User;
import com.example.loader.services.IDataDownloadService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationRunner implements CommandLineRunner {

    private final IDataDownloadService dataDownloadService;

    public ApplicationRunner(IDataDownloadService dataDownloadService) {
        this.dataDownloadService = dataDownloadService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<User> users = dataDownloadService.downloadUsers();
        users.forEach(System.out::println);
    }
}
