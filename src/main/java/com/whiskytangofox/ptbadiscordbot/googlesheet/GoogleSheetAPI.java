package com.whiskytangofox.ptbadiscordbot.googlesheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import com.whiskytangofox.ptbadiscordbot.App;
import org.w3c.dom.ranges.Range;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class GoogleSheetAPI {

    private static final String APPLICATION_NAME = "Google Sheets API PtbADiscordBotIntegration";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        InputStream in = GoogleSheetAPI.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    public final Sheets service;

    public GoogleSheetAPI() throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();

        service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public ArrayList<RangeWrapper> getSheet(String sheetID) throws IOException {
        Spreadsheet spreadsheet = service.spreadsheets().get(sheetID).setIncludeGridData(true).execute();
        ArrayList<RangeWrapper> list = new ArrayList<>();
        for (Sheet sheet : spreadsheet.getSheets()){
            if (sheet != null) {
                for (GridData data : sheet.getData()) {
                    int columns = sheet.getProperties().getGridProperties().getColumnCount();
                    int rows = sheet.getProperties().getGridProperties().getRowCount();
                    CellRef ref = new CellRef(columns, rows);
                    list.add(new RangeWrapper(data, sheet.getProperties().getTitle(), "A1:"+ref.getCellRef()));
                }
            }
        }
        return list;
    }

    public String getCellValue(String sheetId, String tab, String cell) throws IOException {
        String range = cell + ":" + cell;
        try {
            CellData value = getData(sheetId, tab, range).getRowData().get(0).getValues().get(0);
            return value.getFormattedValue() == null ? value.getNote() : value.getFormattedValue();
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    public RangeWrapper getRange(String sheetID, String tab, String range) throws IOException {
        return new RangeWrapper(getData( sheetID, tab, range),tab, range);
    }

    GridData getData(String sheetID, String tab, String range) throws IOException {
        ArrayList<String> ranges = new ArrayList<String>();
        ranges.add((tab+"!"+range));
        Sheets.Spreadsheets.Get request = service.spreadsheets().get(sheetID);
        request.setRanges(ranges);
        request.setFields("sheets/data/rowData/values/formattedValue,sheets/data/rowData/values/note");
        Spreadsheet response = request.execute();
        return response.getSheets().get(0).getData().get(0);
    }

    public List<String> getValues(String sheetID, String tab, List<String> range) throws IOException {
        ArrayList<String> ranges = new ArrayList<String>();
        range.stream().forEach(ref ->ranges.add((tab+"!"+ref)));
        Sheets.Spreadsheets.Get request = service.spreadsheets().get(sheetID);
        request.setRanges(ranges);
        request.setFields("sheets/data/rowData/values/formattedValue");
        Spreadsheet response = request.execute();
        return response.getSheets().get(0).getData().stream()
                .map(data -> data.getRowData().get(0).getValues().get(0).getFormattedValue())
                .collect(Collectors.toList());
    }

    public void setValues(String sheetID, String tab, List<String> cells, List<String> value) throws IOException {
        List<String> ranges = cells.stream().map(c->c+":"+c).collect(Collectors.toList());
        //String valueRenderOption = ""; // TODO: Update placeholder value.

        Sheets.Spreadsheets.Values.BatchGet request =
                service.spreadsheets().values().batchGet(sheetID);
        request.setRanges(ranges);
        //request.setValueRenderOption(valueRenderOption);
        //request.setDateTimeRenderOption(dateTimeRenderOption);

        BatchGetValuesResponse response = request.execute();
    }

}


