package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

;



public class AbstractMoveParserTest extends INoteParserTest {

    public static final Logger logger = LoggerFactory.getLogger(AbstractMoveParserTest.class);

    BasicMoveParser parser = new BasicMoveParser();

    @Test
    public void testParseMoveSimple() {
        values.put(new CellReference("A1"), "Move");
        notes.put(new CellReference("A1"), "playbook_move");
        values.put(new CellReference("A2"), "move text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("Move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testsetBuilderOverridesFromNote_NoOverrides() {
        String note = "playbook_move";
        MoveBuilder builder = parser.setBuilderOverridesFromNote(new MoveBuilder(), note);
        assertNull(builder.overrideName);
        assertNull(builder.overrideText);
    }

    @Test
    public void testsetBuilderOverridesFromNote_NameOnly() {
        String note = "playbook_move=Move";
        MoveBuilder builder = parser.setBuilderOverridesFromNote(new MoveBuilder(), note);
        assertEquals("Move", builder.overrideName);
        assertNull(builder.overrideText);
    }

    @Test
    public void testsetBuilderOverridesFromNote_TextOnly() {
        String note = "playbook_move;text=move text";
        MoveBuilder builder = parser.setBuilderOverridesFromNote(new MoveBuilder(), note);
        assertEquals("move text", builder.overrideText);
        assertNull(builder.overrideName);
    }

    @Test
    public void testsetBuilderOverridesFromNote() {
        String note = "playbook_move=Move;text=move text";
        MoveBuilder builder = parser.setBuilderOverridesFromNote(new MoveBuilder(), note);
        assertEquals("Move", builder.overrideName);
        assertEquals("move text", builder.overrideText);
    }

    @Test
    public void testsetBuilderOverridesFromNote_AllTags() {
        String note = "playbook_move=Move;text=move text;list";
        MoveBuilder builder = parser.setBuilderOverridesFromNote(new MoveBuilder(), note);
        assertEquals("Move", builder.overrideName);
        assertEquals("move text", builder.overrideText);
        assertTrue(builder.isList);
    }

    @Test
    public void testParseMoveNoteOverrideTitle_BooleanFalse() {
        values.put(new CellReference("A1"), "FALSE");
        values.put(new CellReference("A2"), "move text");
        notes.put(new CellReference("A1"), "playbook_move=Move Name");

        assertTrue(parser.parseMove(sheet, 1, 1).skipLoad);
    }

    @Test
    public void testParseMoveBooleanTitleTrue() {
        values.put(new CellReference("A1"), "TRUE");
        values.put(new CellReference("B1"), "test basic move");
        values.put(new CellReference("A2"), "move text");
        notes.put(new CellReference("A1"), "playbook_move");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleFalse() {
        values.put(new CellReference("A1"), "FALSE");
        values.put(new CellReference("B1"), "test basic move");
        values.put(new CellReference("A2"), "move text");
        notes.put(new CellReference("A1"), "playbook_move");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertTrue(builder.skipLoad);
    }

    @Test
    public void testParseMoveBooleanTitleFalseWithOverride() {
        values.put(new CellReference("A1"), "FALSE");
        notes.put(new CellReference("A1"), "playbook_move=Move");
        values.put(new CellReference("B1"), "Move Text");

        MoveBuilder builder = parser.parseMove(sheet, 1, 1);
        assertTrue(builder.skipLoad);
        assertEquals("Move", builder.getMove().name);
    }

    @Test
    public void testParseMoveNotList() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "text 1");
        values.put(new CellReference("A3"), "test 2");
        notes.put(new CellReference("A1"), "playbook_move");
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
        assertFalse(builder.getMove().text.contains("FALSE"));
        assertFalse(builder.getMove().text.contains("TRUE"));
        assertFalse(builder.getMove().text.contains("should not load"));
    }

    @Test
    public void testGetRow_Single() {
        values.put(new CellReference("A1"), "row text 1");
        String row = parser.getRow(sheet, 1, 1);

        assertEquals("row text 1", row);
    }

    @Test
    public void testGetRow_Multiple() {
        values.put(new CellReference("A1"), "1");
        values.put(new CellReference("B1"), "2");
        values.put(new CellReference("C1"), "3");
        String row = parser.getRow(sheet, 1, 1);

        assertEquals("123", row);
    }

    @Test
    public void testGetRow_False() {
        values.put(new CellReference("A1"), "FALSE");
        values.put(new CellReference("B1"), "Should Not Display");
        String row = parser.getRow(sheet, 1, 1);

        assertEquals(" ", row);
    }

    @Test
    public void testGetRow_True() {
        values.put(new CellReference("A1"), "TRUE");
        values.put(new CellReference("B1"), "Should Display");
        String row = parser.getRow(sheet, 1, 1);

        assertEquals("Should Display", row);
    }

    @Test
    public void testGetRow_Space() {
        values.put(new CellReference("A1"), " ");
        values.put(new CellReference("B1"), "Should Display");
        String row = parser.getRow(sheet, 1, 1);

        assertEquals("Should Display", row);
    }

}