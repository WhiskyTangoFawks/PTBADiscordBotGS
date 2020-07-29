package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StatParserTest extends INoteParserTest {

    StatParser parser = new StatParser();

    @Test
    public void testReadSheetStat() {
        String note = SheetParserService.PARSER.stat.name() + "=str";
        values.put(new CellReference("A2"), "+2");
        parser.parse(service, sheet, book, note, 1, 2);
        assertEquals("A2", book.stats.get("str").getCellRef());
    }

}