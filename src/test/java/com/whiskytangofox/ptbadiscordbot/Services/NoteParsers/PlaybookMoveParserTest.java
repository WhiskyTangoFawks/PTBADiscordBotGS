package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PlaybookMoveParserTest extends INoteParserTest {

    PlaybookMoveParser parser = new PlaybookMoveParser();

    @Test
    public void testReadSheetPlaybook() {
        String note = SheetReaderService.PARSER.playbook_move.name();

        values.put(new CellReference("A2"), "test move");
        notes.put(new CellReference("A2"), note);
        values.put(new CellReference("A3"), "move text");

        parser.parse(service, sheet, book, note, 1, 2);

        assertTrue(book.moves.get("test move").text.contains("move text"));
    }


    @Test
    public void testReadSheetForParseMoveNoteOverride() {
        String note = SheetReaderService.PARSER.playbook_move.name();

        values.put(new CellReference("A2"), "TRUE");
        values.put(new CellReference("B3"), "Shield (+1 Armor; +1 Readiness when you roll Defend 7+)");
        notes.put(new CellReference("A2"), SheetReaderService.PARSER.playbook_move.name() + "=Override;text=+1 Readiness when you roll Defend 7+");
        values.put(new CellReference("A3"), "Not part of the move");

        parser.parse(service, sheet, book, note, 1, 2);

        assertTrue(book.moves.get("Override").text.contains("+1 Readiness when you roll Defend 7+"));
    }


}