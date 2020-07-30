package com.whiskytangofox.ptbadiscordbot.GoogleSheet;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class GoogleSheetAPI {

    private static final String APPLICATION_NAME = "Google Sheets API PtbADiscordBotIntegration";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    //private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static Credential credential = null;

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/service_credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        if (credential == null) {
            InputStream is = GoogleSheetAPI.class
                    .getResourceAsStream(CREDENTIALS_FILE_PATH);
            if (is == null) {
                throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
            }
            credential = GoogleCredential.fromStream(is)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
        }
        return credential;

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
                    CellReference ref = new CellReference(columns, rows);
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
        range.stream().forEach(ref -> ranges.add((tab + "!" + ref)));
        Sheets.Spreadsheets.Get request = service.spreadsheets().get(sheetID);
        request.setRanges(ranges);
        request.setFields("sheets/data/rowData/values/formattedValue");
        Spreadsheet response = request.execute();
        return response.getSheets().get(0).getData().stream()
                .map(data -> data.getRowData().get(0).getValues().get(0).getFormattedValue())
                .collect(Collectors.toList());
    }

    public void setValues(String sheetID, String tab, List<String> cells, List<String> values) throws IOException {
        List<String> ranges = cells.stream().map(c -> tab + "!" + c + ":" + c).collect(Collectors.toList());

        // How the input data should be interpreted.
        String valueInputOption = "RAW";

        // The new values to apply to the spreadsheet.
        List<ValueRange> dataSet = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            ValueRange data = new ValueRange();
            data.setRange(ranges.get(i));
            List<List<Object>> list = new ArrayList<>();
            List<Object> list2 = new ArrayList<>();
            Object value = values.get(i);
            if (values.get(i).equalsIgnoreCase("true")) {
                value = true;
            } else if (values.get(i).equalsIgnoreCase("false")) {
                value = false;
            }
            list2.add(value);
            list.add(list2);
            data.setValues(list);
            dataSet.add(data);
        }

        BatchUpdateValuesRequest requestBody = new BatchUpdateValuesRequest();
        requestBody.setValueInputOption(valueInputOption);
        requestBody.setData(dataSet);


        Sheets.Spreadsheets.Values.BatchUpdate request =
                service.spreadsheets().values().batchUpdate(sheetID, requestBody);

        BatchUpdateValuesResponse response = request.execute();
    }

}


