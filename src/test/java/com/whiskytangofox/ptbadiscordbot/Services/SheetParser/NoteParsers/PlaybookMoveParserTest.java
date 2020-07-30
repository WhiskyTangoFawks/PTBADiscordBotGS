package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

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

    @Test
    public void testParseSkippedMove() {
        String note = SheetParserService.PARSER.playbook_move.name();
        values.put(new CellReference("A1"), "FALSE");
        notes.put(new CellReference("A1"), note);
        values.put(new CellReference("B1"), "Skipped Move");
        values.put(new CellReference("A2"), "move text");

        parser.parse(service, sheet, book, note, 1, 1);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service).registerSkippedMove(captor.capture());
        assertEquals("Skipped Move", captor.getValue());
    }

}