package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GameSheetService {

    public final String sheetID;
    public final GoogleSheetAPI api;
    public final Properties settings;

    public GameSheetService(String sheetID, GoogleSheetAPI api, Properties settings) {
        this.sheetID = sheetID;
        this.api = api;
        this.settings = settings;
    }

    public String getCellValue(String tab, String cell) throws IOException {
        return api.getCellValue(sheetID, tab, cell);
    }

    public RangeWrapper getRange(String tab, String range) throws IOException {
        return api.getRange(sheetID, tab, range);
    }

    public List<String> getValues(String tab, List<String> values) throws IOException {
        return api.getValues(sheetID, tab, values);
    }

    public void setValues(String tab, List<String> cells, List<String> values) throws IOException {
        api.setValues(sheetID, tab, cells, values);
    }

    public ArrayList<RangeWrapper> getSheet() throws IOException {
        return api.getSheet(sheetID);
    }


}
