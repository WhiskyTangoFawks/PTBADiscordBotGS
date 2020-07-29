package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbstractMoveParserTest extends INoteParserTest {

    BasicMoveParser parser = new BasicMoveParser();

    @Test
    public void testParseMoveSimple() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "move text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveFromNote() {
        String note = "playbook_move=Override;text=+1 Readiness when you roll Defend 7+";
        MoveBuilder builder = parser.parseMoveFromNote(note);
        assertEquals("Override", builder.get(0));
        assertEquals("+1 Readiness when you roll Defend 7+", builder.get(1));
    }

    @Test
    public void testParseMoveNoteOverrideTitle() {
        values.put(new CellReference("A1"), "move text");
        notes.put(new CellReference("A1"), "playbook_move=Move Name");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("Move Name", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveNoteOverrideText() {
        values.put(new CellReference("A1"), "Move Name");
        notes.put(new CellReference("A1"), "playbook_move;text=move text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("Move Name", builder.get(0));
        assertEquals("move text", builder.get(1));
    }


    @Test
    public void testParseMoveNoteOverrideTitleAndName() {
        values.put(new CellReference("A1"), "TRUE");
        values.put(new CellReference("B1"), "Shield (+1 Armor; +1 Readiness when you roll Defend 7+)");
        notes.put(new CellReference("A1"), "playbook_move=Override;text=+1 Readiness when you roll Defend 7+");
        values.put(new CellReference("A2"), "Not part of the move");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("Override", builder.get(0));
        assertEquals("+1 Readiness when you roll Defend 7+", builder.get(1));
    }


    @Test
    public void testParseMoveIndentedText() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), " ");
        values.put(new CellReference("B2"), "move text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleTrue() {
        values.put(new CellReference("A1"), "TRUE");
        values.put(new CellReference("B1"), "test basic move");
        values.put(new CellReference("A2"), "move text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleFalse() {
        values.put(new CellReference("A1"), "FALSE");
        values.put(new CellReference("B1"), "test basic move");
        values.put(new CellReference("A2"), "move text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertNull(builder);
    }

    @Test
    public void testParseMoveNotList() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "text 1");
        values.put(new CellReference("A3"), "test 2");
        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals(2, builder.size());
        assertEquals("test basic move", builder.get(0));
        assertEquals("text 1", builder.get(1));
    }

    @Test
    public void testParseMoveList() {
        notes.put(new CellReference("A1"), "basic_move;list");
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "text 1");
        values.put(new CellReference("A3"), "text 2");
        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals(4, builder.size());
        assertEquals("test basic move", builder.get(0));
        assertEquals("text 1", builder.get(1));
        assertEquals("text 2", builder.get(2));

    }

    @Test
    public void testParseMoveBooleanTextTrueAndFalse() {
        notes.put(new CellReference("A1"), "basic_move;list");
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "TRUE");
        values.put(new CellReference("B2"), "move text");
        values.put(new CellReference("A3"), "FALSE");
        values.put(new CellReference("B3"), "should not load");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
        assertFalse(builder.get(2).contains("load"));
    }

}