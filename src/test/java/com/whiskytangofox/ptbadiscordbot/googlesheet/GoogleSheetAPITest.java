package com.whiskytangofox.ptbadiscordbot.googlesheet;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import com.whiskytangofox.ptbadiscordbot.App;
import org.junit.Test;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

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

}