package com.example.loader.services.implementation;

import com.example.loader.dto.Book;
import com.example.loader.dto.User;
import com.example.loader.services.IDataDownloadService;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleSheetsDataDownloadService implements IDataDownloadService {
    @Value("${google.sheets.spreadsheet.id}")
    private String SPREADSHEET_ID;

    @Value("${google.service.account.credentials.path}")
    private String CREDENTIALS_FILE_PATH;

    @Override
    public List<User> downloadUsers() {
        Sheets sheetsService = buildSheetsService();

        List<Sheet> sheets = getSheets(sheetsService);

        List<User> users = new ArrayList<>();
        // Loop through each sheet and fetch data
        for (Sheet sheet : sheets) {
            User user = mapSheetToUser(sheetsService, sheet);
            users.add(user);
        }

        return users;
    }

    private Sheets buildSheetsService() {
        GoogleCredentials credentials = loadCredentials();

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

    private GoogleCredentials loadCredentials() {
        try {
            return GoogleCredentials.fromStream(new FileInputStream(CREDENTIALS_FILE_PATH))
                    .createScoped("https://www.googleapis.com/auth/spreadsheets.readonly");
        } catch (IOException e) {
            log.error("Failed to load credentials", e);
            throw new RuntimeException(e);
        }
    }

    private List<Sheet> getSheets(Sheets sheetsService) {
        try {
            Spreadsheet spreadsheet = sheetsService.spreadsheets()
                    .get(SPREADSHEET_ID)
                    .execute();

            return spreadsheet.getSheets();
        } catch (IOException e) {
            log.error("Failed to fetch sheets from Google Sheets", e);
            throw new RuntimeException(e);
        }
    }

    private User mapSheetToUser(Sheets sheetsService, Sheet sheet) {
        String sheetName = sheet.getProperties().getTitle();
        log.debug("Fetching data from sheet: " + sheetName);

        // (A2:C assumes headers are in the first row)
        String sheetRange = "!A2:C";
        String range = sheetName + sheetRange;

        // Fetch data from the sheet
        ValueRange response = null;
        try {
            response = sheetsService.spreadsheets().values()
                    .get(SPREADSHEET_ID, range)
                    .execute();

            List<List<Object>> values = response.getValues();

            List<Book> books = mapSheetRowsToBooks(values);

            return User.builder()
                    .name(sheetName)
                    .books(books)
                    .build();

        } catch (IOException e) {
            log.error("Failed to fetch data from Google Sheets", e);
            throw new RuntimeException(e);
        }
    }

    private List<Book> mapSheetRowsToBooks(List<List<Object>> values) {
        return values.stream()
                .map(this::mapSheetRowToBook)
                .collect(Collectors.toList());
    }

    private Book mapSheetRowToBook(List<Object> row) {
        String isbn = getRowValue(row, 0);
        String author = getRowValue(row, 1);
        String name = getRowValue(row, 2);

        return Book.builder()
                .isbn(isbn)
                .author(author)
                .name(name)
                .build();
    }

    private String getRowValue(List<Object> row, int index) {
        return row.size() > index ? row.get(index).toString() : "N/A";
    }
}
