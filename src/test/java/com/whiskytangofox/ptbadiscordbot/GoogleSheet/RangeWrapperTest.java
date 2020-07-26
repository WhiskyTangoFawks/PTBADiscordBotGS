package com.whiskytangofox.ptbadiscordbot.GoogleSheet;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static org.junit.Assert.assertEquals;

public class RangeWrapperTest {

    public static final Logger logger = LoggerFactory.getLogger(RangeWrapperTest.class);
    static GoogleSheetAPI api;
    String sheetID = "1zwZlDaLdNDF7vs5Gx_WVFf19-E_IAy6pCNp_guQjMko";

    @BeforeClass
    public static void setup() throws IOException, GeneralSecurityException {
        logger.info("Running @BeforeClass Setup");
        api = new GoogleSheetAPI();

    }

    //@Test TODO
    public void testGetValueIntInt() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "A1:AC100");

        assertEquals("A2", range.getValue(1, 2).toUpperCase());
        assertEquals("B1", range.getValue(2, 1).toUpperCase());
        assertEquals("C10", range.getValue(3, 10).toUpperCase());
        assertEquals("AA90", range.getValue(27, 90).toUpperCase());

    }

    //@Test TODO
    public void testGetValueIntIntShifted() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "B3:AA100");

        assertEquals("B3", range.getValue(2, 3).toUpperCase());
        assertEquals("C5", range.getValue(3, 5).toUpperCase());
        assertEquals("C10", range.getValue(3, 10).toUpperCase());
        assertEquals("AA90", range.getValue(27, 90).toUpperCase());
        assertEquals("B3", range.getValue("B3").toUpperCase());
        assertEquals("C5", range.getValue("C5").toUpperCase());
        assertEquals("C10", range.getValue("C10").toUpperCase());
        assertEquals("AA90", range.getValue("AA90").toUpperCase());

    }

    //@Test TODO
    public void testGetValueString() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "A1:AC100");

        assertEquals("A2", range.getValue("A2").toUpperCase());
        assertEquals( "B1", range.getValue("B1").toUpperCase());
        assertEquals("C10", range.getValue("C10").toUpperCase());
        assertEquals("AA90", range.getValue("AA90").toUpperCase());
    }

    // @Test TODO
    public void testGetValuesAfterSubrange() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "A1:AC100");
        RangeWrapper subRange = new RangeWrapper(range, "B10:V90",0);

        assertEquals("E12", subRange.getValue(5, 12).toUpperCase());
        assertEquals("J15", subRange.getValue(10, 15).toUpperCase());
        assertEquals("L10", subRange.getValue(12, 10).toUpperCase());
        assertEquals("Q90", subRange.getValue(17, 90).toUpperCase());

        assertEquals("E12", subRange.getValue("E12").toUpperCase());
        assertEquals("J15", subRange.getValue("J15").toUpperCase());
        assertEquals("L10", subRange.getValue("L10").toUpperCase());
        assertEquals("Q90", subRange.getValue("Q90").toUpperCase());
    }

    //@Test TODO
    public void testGetSingleColumn() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "A1:A100");

        assertEquals("A1", range.getValue("A1").toUpperCase());
        assertEquals("A100", range.getValue("A100").toUpperCase());
    }

    //@Test TODO
    public void testGetSingleRow() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "A1:AA1");

        assertEquals("A1", range.getValue("A1").toUpperCase());
        assertEquals("AA1", range.getValue("AA1").toUpperCase());
    }

    //@Test TODO
    public void testGetNoteValues() throws IOException {
        RangeWrapper range = api.getRange(sheetID, "test", "A1:AA100");
        assertEquals("notea1", range.getNote("A1"));
        assertEquals("noteb3", range.getNote(2, 3));
    }

}