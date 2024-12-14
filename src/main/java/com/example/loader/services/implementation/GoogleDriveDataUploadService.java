package com.example.loader.services.implementation;

import com.example.loader.clients.GoogleApiClient;
import com.example.loader.dto.BookJson;
import com.example.loader.mappers.BookMapper;
import com.example.loader.models.User;
import com.example.loader.services.IDataUploadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleDriveDataUploadService implements IDataUploadService {

    @Value("${google.drive.folder.id}")
    private String FOLDER_ID;

    private final GoogleApiClient googleApiClient;

    @Override
    public void uploadUsers(List<User> users) {
        Drive service = googleApiClient.buildDriveService();

        FileList result = loadFiles(service);
        List<File> files = result.getFiles();

        clearFiles(service, files);

        for (User user : users) {
            uploadFile(service, user);
        }

        log.info("Data for {} users has been uploaded to Google Drive", users.size());
    }

    private void clearFiles(Drive service, List<File> files) {
        int count = 0;

        for (File file : files) {
            try {
                service.files().delete(file.getId()).execute();
                count++;
            } catch (IOException e) {
                log.error("Failed to delete file {} {}", file.getId(), e.getMessage());
            }
        }

        log.info("Deleted {} files on Google Drive", count);
    }

    private void uploadFile(Drive service, User user) {
        try {
            File fileMetadata = new File();
            fileMetadata.setName(String.format("%s.json", user.getName()));
            fileMetadata.setParents(List.of(FOLDER_ID));

            // TODO: extract to separate method
            ObjectMapper objectMapper = new ObjectMapper();
            List<BookJson> books = user.getBooks().stream().map(BookMapper::toBookJson).toList();
            String booksJson = objectMapper.writeValueAsString(books);

            java.io.File tempFile = java.io.File.createTempFile(user.getName(), ".json");
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(booksJson);
            }

            FileContent fileContent = new FileContent("application/json", tempFile);

            File file = service.files().create(fileMetadata, fileContent)
                    .setFields("id")
                    .execute();

            log.debug("Created file {} with ID: {}", file.getName(), file.getId());
            user.setExternalId(file.getId());

            tempFile.deleteOnExit();
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException(e);
        }
    }


    private FileList loadFiles(Drive service) {
        try {
            return service.files().list()
                    .setQ(String.format("'%s' in parents", FOLDER_ID))
                    .setPageSize(50)
                    .setFields("nextPageToken, files(id, name)")
                    .execute();
        } catch (Exception e) {
            log.error("Failed to load files", e);
            throw new RuntimeException(e);
        }
    }
}
