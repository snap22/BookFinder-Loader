package com.example.loader.clients;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleApiClient {
    @Value("${google.service.account.credentials.path}")
    private String CREDENTIALS_FILE_PATH;

    private GoogleCredentials loadCredentialsForDrive() {
        return loadCredentialsWithScope(DriveScopes.DRIVE);
    }

    private GoogleCredentials loadCredentialsForSheets() {
        return loadCredentialsWithScope(SheetsScopes.SPREADSHEETS_READONLY);
    }

    private GoogleCredentials loadCredentialsWithScope(String scope) {
        try {
            return GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                    .createScoped(scope);
        } catch (IOException e) {
            log.error("Failed to load credentials", e);
            throw new RuntimeException(e);
        }
    }

    public Sheets buildSheetsService() {
        GoogleCredentials credentials = loadCredentialsForSheets();

        try {
            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Google Sheets API Downloader")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to build Sheets service", e);
            throw new RuntimeException(e);
        }
    }

    public Drive buildDriveService() {
        GoogleCredentials credentials = loadCredentialsForDrive();

        try {
            return new Drive.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("Google Drive API Downloader")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            log.error("Failed to build Drive service", e);
            throw new RuntimeException(e);
        }
    }
}
