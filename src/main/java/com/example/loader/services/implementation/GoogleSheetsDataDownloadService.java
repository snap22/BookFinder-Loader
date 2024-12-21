package com.example.loader.services.implementation;

import com.example.loader.clients.GoogleApiClient;
import com.example.loader.generators.NormalizedNameGenerator;
import com.example.loader.models.Book;
import com.example.loader.models.User;
import com.example.loader.services.IDataDownloadService;
import com.example.loader.utils.DotEnvHolder;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleSheetsDataDownloadService implements IDataDownloadService {
    private final String SPREADSHEET_ID;

    private final GoogleApiClient googleApiClient;

    private final NormalizedNameGenerator nameGenerator;

    public GoogleSheetsDataDownloadService(GoogleApiClient googleApiClient, DotEnvHolder dotEnvHolder) {
        this.googleApiClient = googleApiClient;
        SPREADSHEET_ID = dotEnvHolder.getVariable("GOOGLE_SHEETS_SPREADSHEET_ID");
        nameGenerator = new NormalizedNameGenerator();
    }


    @Override
    public List<User> downloadUsers() {
        Sheets sheetsService = googleApiClient.buildSheetsService();

        List<Sheet> sheets = getSheets(sheetsService);

        List<User> users = sheets.stream()
                .map(sheet -> mapSheetToUser(sheetsService, sheet))
                .toList();

        log.info("Data for {} users has been downloaded from Google Sheets", users.size());
        return users;
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
                    .normalizedName(nameGenerator.generateNormalizedName(sheetName))
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
