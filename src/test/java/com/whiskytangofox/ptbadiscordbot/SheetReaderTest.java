package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
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
        game = new Game(mockChannel, null);
        game.playbooks = new HashMap<>();
        game.basicMoves = new PatriciaTrieIgnoreCase<>();
        reader = new SheetReader(game);
    }

    @Test
    public void testReadSheetNewPlaybook() {
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test");
        reader.parseSheet(sheet);
        verify(mockLogger, Mockito.times(1)).info(SheetReader.start_load_msg +"test");
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
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
        notes.put(new CellRef("A2"), SheetReader.Metadata.stat.name() + ":str");

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
        notes.put(new CellRef("A2"), SheetReader.Metadata.stat_penalty.name() + ":str, dex");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.stat_penalties.get("str").getCellRef());
        assertEquals("A2", book.stat_penalties.get("dex").getCellRef());
    }

    @Test
    public void testReadSheetResource(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "12");
        notes.put(new CellRef("A2"), SheetReader.Metadata.resource.name() + ":hp");

        values.put(new CellRef("B1"), "discordName");
        notes.put(new CellRef("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.resources.get("hp").getCellRef());
    }

    @Test
    public void testReadSheetDefaultDice(){
        notes.put(new CellRef("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellRef("A1"), "test playbook1");

        values.put(new CellRef("A2"), "damage 1d10");
        notes.put(new CellRef("A2"), SheetReader.Metadata.default_dice.name() + ":Move Name");

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
        verify(mockChannel).sendMessage("Unexpected exception trying to read cell A1, note= this should throw an exception");
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
    public void testParseMoveList(){
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
    public void testParseMoveBooleanTextTrueAndFalse(){
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