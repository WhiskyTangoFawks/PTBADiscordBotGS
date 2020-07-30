package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PlaybookMoveParserTest extends INoteParserTest {

    PlaybookMoveParser parser = new PlaybookMoveParser();

    @Test
    public void testReadSheetPlaybook() {
        String note = SheetParserService.PARSER.playbook_move.name();

        values.put(new CellReference("A2"), "test move");
        notes.put(new CellReference("A2"), note);
        values.put(new CellReference("A3"), "move text");

        parser.parse(service, sheet, book, note, 1, 2);

        assertTrue(book.moves.get("test move").text.contains("move text"));
    }

}