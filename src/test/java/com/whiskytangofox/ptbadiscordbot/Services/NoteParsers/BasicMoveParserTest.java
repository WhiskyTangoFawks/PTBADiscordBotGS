package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

public class BasicMoveParserTest extends INoteParserTest {

    BasicMoveParser parser = new BasicMoveParser();

    @Test
    public void testParse() {
        String note = SheetReaderService.PARSER.basic_move.name();
        values.put(new CellReference("A1"), "test basic move");
        notes.put(new CellReference("A1"), note);
        values.put(new CellReference("A2"), "move text");

        parser.parse(service, sheet, book, note, 1, 1);

        ArgumentCaptor<Move> captor = ArgumentCaptor.forClass(Move.class);
        verify(service).registerBasicMove(captor.capture());
        assertEquals("test basic move", captor.getValue().name);
        assertTrue(captor.getValue().text.contains("move text"));
    }

}