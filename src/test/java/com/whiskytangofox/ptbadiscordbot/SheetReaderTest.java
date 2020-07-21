package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.HashMapIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.wrappers.Playbook;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SheetReaderTest {

    static Game game;

    @Mock
    static Logger mockLogger;

    @Mock
    static MessageChannel mockChannel;

    @Mock
    static MessageAction mockMessageAction;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    static RangeWrapper sheet;
    static HashMap<CellRef, String> values;
    static HashMap<CellRef, String> notes;

    static SheetReader reader;


    @Before
    public void before() throws IOException {
        values = new HashMap<CellRef, String>();
        notes = new HashMap<>();
        sheet = new RangeWrapper(values, notes, null, "A1:D4");
        MockitoAnnotations.initMocks(this);
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
        SheetReader.logger = mockLogger;
        game = new Game(null, mockChannel, null, false);
        game.playbooks = new HashMapIgnoreCase<>();
        game.basicMoves = new PatriciaTrieIgnoreCase<>();
        reader = new SheetReader(game);

    }

    @Test
    public void testReadSheetSecondNewPlaybookNoDiscord() {
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");
        values.put(new CellRef("A1"), "test playbook2");
        reader.parseSheet(sheet);
        //With no discord name, it should skip loading without
        assertEquals(0, game.playbooks.size());
        verify(mockLogger, Mockito.times(0)).info(SheetReader.registered_msg +"test playbook1");
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
    }

    @Test
    public void testReadSheetSecondNewPlaybookBlankDiscord() {
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        values.put(new CellRef("B1"), "<type discord name>");
        values.put(new CellRef("C1"), "test playbook2");
        reader.parseSheet(sheet);
        //With no discord name, it should skip loading without
        assertEquals(0, game.playbooks.size());
        verify(mockLogger, Mockito.times(0)).info(SheetReader.registered_msg +"test playbook1");
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
    }

    @Test
    public void testReadSheetDiscordNameSinglePlaybook(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");
        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);
        assertNotNull(game.playbooks.get("discordName"));
    }

    @Test
    public void testReadSheetDiscordNameSecondPlaybook(){
        values.put(new CellRef("A1"), "test playbook1");
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        notes.put(new CellRef("C1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("C1"), "test playbook2");

        reader.parseSheet(sheet);
        assertNotNull(game.playbooks.get("discordName"));
    }

    @Test
    public void testReadSheetBasicMove(){
        values.put(new CellRef("A1"), "test basic move");
        notes.put(new CellRef("A1"), SheetReader.Metadata.basic_move.name());
        values.put(new CellRef("A2"), "move text");
        reader.parseSheet(sheet);
        assertEquals("test basic move", game.basicMoves.get("test basic move").name);
        assertTrue(game.basicMoves.get("test basic move").text.contains("move text"));
    }

    @Test
    public void testReadSheetPlaybook(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "test move");
        notes.put(new CellRef("A2"), SheetReader.Metadata.playbook_move.name());
        values.put(new CellRef("A3"), "move text");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertTrue(book.moves.get("test move").text.contains("move text"));
    }

    @Test
    public void testReadSheetStat(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "+2");
        notes.put(new CellRef("A2"), SheetReader.Metadata.stat.name() + "=str");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.stats.get("str").getCellRef());
    }

    @Test
    public void testReadSheetStatPenalty(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "FALSE");
        notes.put(new CellRef("A2"), SheetReader.Metadata.stat_penalty.name() + "=str, dex");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.stat_penalties.get("str").getCellRef());
        assertEquals("A2", book.stat_penalties.get("dex").getCellRef());
    }

    @Test
    public void testReadSheetResource_Single() {
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "12");
        notes.put(new CellRef("A2"), SheetReader.Metadata.resource.name() + "=hp");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.resources.get("hp").get(0).getCellRef());
        assertNull(book.resources.get("hp").min);
        assertNull(book.resources.get("hp").max);
    }

    @Test
    public void testReadSheetResource_MinMax() {
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "12");
        notes.put(new CellRef("A2"), SheetReader.Metadata.resource.name() + "=hp;min=0;max=20");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.resources.get("hp").get(0).getCellRef());
        assertEquals(Integer.valueOf(0), book.resources.get("hp").min);
        assertEquals(Integer.valueOf(20), book.resources.get("hp").max);
    }

    @Test
    public void testReadSheetResource_List() {
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "TRUE");
        notes.put(new CellRef("A2"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellRef("A3"), "TRUE");
        notes.put(new CellRef("A3"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellRef("A4"), "TRUE");
        notes.put(new CellRef("A4"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellRef("B3"), "TRUE");
        notes.put(new CellRef("B3"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellRef("B4"), "TRUE");
        notes.put(new CellRef("B4"), SheetReader.Metadata.resource.name() + "=xp");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals(5, book.resources.get("XP").size());
    }

    @Test
    public void testReadSheetDefaultDice(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "damage 1d10");
        notes.put(new CellRef("A2"), SheetReader.Metadata.default_dice.name() + "=Move Name");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("1d10", book.moveOverrideDice.get("movename"));
    }

    @Test
    public void testReadSheetBadNote(){
        notes.put(new CellRef("A1"), "this should throw an exception");
        reader.parseSheet(sheet);
        verify(mockChannel).sendMessage(anyString());
    }

    @Test
    public void testParseMoveSimple(){
        values.put(new CellRef("A1"), "test basic move");
        values.put(new CellRef("A2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }
    @Test
    public void testParseMoveFromNote(){
        String note = "playbook_move=Override;text=+1 Readiness when you roll Defend 7+";
        MoveBuilder builder = reader.parseMoveFromNote(note);
        assertEquals("Override", builder.get(0));
        assertEquals("+1 Readiness when you roll Defend 7+", builder.get(1));
    }

    @Test
    public void testParseMoveNoteOverrideTitle(){
        values.put(new CellRef("A1"), "move text");
        notes.put(new CellRef("A1"), "playbook_move=Move Name");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("Move Name", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveNoteOverrideText(){
        values.put(new CellRef("A1"), "Move Name");
        notes.put(new CellRef("A1"), "playbook_move;text=move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("Move Name", builder.get(0));
        assertEquals("move text", builder.get(1));
    }


    @Test
    public void testParseMoveNoteOverrideTitleAndName(){
        values.put(new CellRef("A1"), "TRUE");
        values.put(new CellRef("B1"), "Shield (+1 Armor; +1 Readiness when you roll Defend 7+)");
        notes.put(new CellRef("A1"), "playbook_move=Override;text=+1 Readiness when you roll Defend 7+");
        values.put(new CellRef("A2"), "Not part of the move");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("Override", builder.get(0));
        assertEquals("+1 Readiness when you roll Defend 7+", builder.get(1));
    }

    @Test
    public void testReadSheetForParseMoveNoteOverride(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "TRUE");
        values.put(new CellRef("B3"), "Shield (+1 Armor; +1 Readiness when you roll Defend 7+)");
        notes.put(new CellRef("A2"), SheetReader.Metadata.playbook_move.name() + "=Override;text=+1 Readiness when you roll Defend 7+");
        values.put(new CellRef("A3"), "Not part of the move");

        values.put(new CellRef("C1"), "discordName");
        notes.put(new CellRef("C1"), SheetReader.Metadata.discord_name.name());

        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertTrue(book.moves.get("Override").text.contains("+1 Readiness when you roll Defend 7+"));
    }


    @Test
    public void testParseMoveIndentedText(){
        values.put(new CellRef("A1"), "test basic move");
        values.put(new CellRef("A2"), " ");
        values.put(new CellRef("B2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleTrue(){
        values.put(new CellRef("A1"), "TRUE");
        values.put(new CellRef("B1"), "test basic move");
        values.put(new CellRef("A2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleFalse(){
        values.put(new CellRef("A1"), "FALSE");
        values.put(new CellRef("B1"), "test basic move");
        values.put(new CellRef("A2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertNull(builder);
    }

    @Test
    public void testParseMoveNotList(){
        values.put(new CellRef("A1"), "test basic move");
        values.put(new CellRef("A2"), "text 1");
        values.put(new CellRef("A3"), "test 2");
        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals(2, builder.size());
        assertEquals("test basic move", builder.get(0));
        assertEquals("text 1", builder.get(1));
    }

    @Test
    public void testParseMoveList(){
        notes.put(new CellRef("A1"), "basic_move;list");
        values.put(new CellRef("A1"), "test basic move");
        values.put(new CellRef("A2"), "text 1");
        values.put(new CellRef("A3"), "text 2");
        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals(4, builder.size());
        assertEquals("test basic move", builder.get(0));
        assertEquals("text 1", builder.get(1));
        assertEquals("text 2", builder.get(2));

    }

    @Test
    public void testParseMoveBooleanTextTrueAndFalse(){
        notes.put(new CellRef("A1"), "basic_move;list");
        values.put(new CellRef("A1"), "test basic move");
        values.put(new CellRef("A2"), "TRUE");
        values.put(new CellRef("B2"), "move text");
        values.put(new CellRef("A3"), "FALSE");
        values.put(new CellRef("B3"), "should not load");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
        assertFalse(builder.get(2).contains("load"));
    }

}