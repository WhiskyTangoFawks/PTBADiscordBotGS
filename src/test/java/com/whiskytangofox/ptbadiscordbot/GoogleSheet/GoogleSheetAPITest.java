package com.whiskytangofox.ptbadiscordbot.GoogleSheet;

import com.whiskytangofox.ptbadiscordbot.App;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class GoogleSheetAPITest {

    String sheetID = "1zwZlDaLdNDF7vs5Gx_WVFf19-E_IAy6pCNp_guQjMko";
    String tab = "test";
    String cell = "B6";
    String range = tab+"!"+"A1:B6";


    //@Test
    public void devSpike_Write() throws IOException, GeneralSecurityException {
        GoogleSheetAPI api = new GoogleSheetAPI();
        String string = api.getCellValue(sheetID, "PCs", "C52");
        App.logger.info(string);
    }


    //@Test
    public void devSpike_GetWholeSheet() throws IOException, GeneralSecurityException {
        GoogleSheetAPI api = new GoogleSheetAPI();
        api.getSheet(sheetID);
    }

    //@Test
    public void devSpike_GetMultipleCells() throws IOException, GeneralSecurityException {
        GoogleSheetAPI api = new GoogleSheetAPI();
        //List<String> list = api.getValues(sheetID, tab, "C23", "A2");
        //assertEquals("c23", list.get(0));
        //assertEquals("a2", list.get(1));
    }

    //@Test
    public void devSpike_setValues() throws IOException, GeneralSecurityException {
        GoogleSheetAPI api = new GoogleSheetAPI();
        ArrayList<String> cells = new ArrayList<String>();
        cells.add("B6");
        cells.add("A1");
        ArrayList<String> values = new ArrayList<String>();
        values.add("B6");
        values.add("A1");
        api.setValues(sheetID, "writetest", cells, values);
    }

}