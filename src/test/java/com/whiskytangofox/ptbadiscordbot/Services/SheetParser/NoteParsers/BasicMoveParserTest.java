package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

public class BasicMoveParserTest extends INoteParserTest {

    BasicMoveParser parser = new BasicMoveParser();

    @Test
    public void testParse() {
        String note = SheetParserService.PARSER.basic_move.name();
        values.put(new CellReference("A1"), "test basic move");
        notes.put(new CellReference("A1"), note);
        values.put(new CellReference("A2"), "move text");

        parser.parse(service, sheet, book, note, 1, 1);

        ArgumentCaptor<Move> captor = ArgumentCaptor.forClass(Move.class);
        verify(service).registerBasicMove(captor.capture());
        assertEquals("test basic move", captor.getValue().name);
        assertTrue(captor.getValue().text.contains("move text"));
    }

    @Test
    public void testParseSkippedMove() {
        String note = SheetParserService.PARSER.basic_move.name();
        values.put(new CellReference("A1"), "FALSE");
        notes.put(new CellReference("A1"), note);
        values.put(new CellReference("B1"), "Skipped Move");
        values.put(new CellReference("A2"), "move text");

        parser.parse(service, sheet, book, note, 1, 1);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(service).registerSkippedMove(captor.capture());
        assertEquals("skippedmove", captor.getValue());
    }

}