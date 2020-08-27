package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class ResourceParserTest extends INoteParserTest {

    ResourceParser parser = new ResourceParser();

    @Test
    public void testReadSheetResource_Single() {
        values.put(new CellReference("A2"), "12");
        String note = SheetParserService.PARSER.resource.name() + "=hp";
        notes.put(new CellReference("A2"), note);

        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals("A2", book.resources.get("hp").get(0).getCellRef());
        assertNull(book.resources.get("hp").min);
        assertNull(book.resources.get("hp").max);
    }

    @Test
    public void testReadSheetResource_MinMax() {
        values.put(new CellReference("A2"), "12");
        String note = SheetParserService.PARSER.resource.name() + "=hp;min=0;max=20";
        notes.put(new CellReference("A2"), note);


        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals("A2", book.resources.get("hp").get(0).getCellRef());
        assertEquals(Integer.valueOf(0), book.resources.get("hp").min);
        assertEquals(Integer.valueOf(20), book.resources.get("hp").max);
    }

    @Test
    public void testReadSheetResource_List() {
        String note = SheetParserService.PARSER.resource.name() + "=xp";
        values.put(new CellReference("A2"), "TRUE");
        notes.put(new CellReference("A2"), note);
        values.put(new CellReference("A3"), "TRUE");
        notes.put(new CellReference("A3"), note);
        values.put(new CellReference("A4"), "TRUE");
        notes.put(new CellReference("A4"), note);
        values.put(new CellReference("B3"), "TRUE");
        notes.put(new CellReference("B3"), note);
        values.put(new CellReference("B4"), "TRUE");
        notes.put(new CellReference("B4"), note);

        parser.parse(service, sheet, book, note, 1, 2);
        parser.parse(service, sheet, book, note, 1, 2);
        parser.parse(service, sheet, book, note, 1, 2);
        parser.parse(service, sheet, book, note, 1, 2);
        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals(5, book.resources.get("XP").size());
    }

    @Test
    public void testReadSheetResource_MovePenalty() {
        String note = SheetParserService.PARSER.resource.name() + "=hp;move_penalty=Cast A Spell";
        values.put(new CellReference("A2"), "12");
        notes.put(new CellReference("A2"), note);

        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals("A2", book.movePenalties.get("Cast A Spell").getCellRef());
    }

    @Test
    public void testReadSheetResourceStatPenalty() {
        String note = SheetParserService.PARSER.resource.name() + "=hp;stat_penalty=STR,dex";
        values.put(new CellReference("A2"), "12");
        notes.put(new CellReference("A2"), note);

        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals("A2", book.stat_penalties.get("str").getCellRef());
        assertEquals("A2", book.stat_penalties.get("dex").getCellRef());
    }

}