package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.GameSheetService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReader;
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

    @Mock
    static GoogleSheetAPI mockApi;

    GameSheetService mockSheetService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    static RangeWrapper sheet;
    static HashMap<CellReference, String> values;
    static HashMap<CellReference, String> notes;

    static SheetReader reader;


    @Before
    public void before() throws IOException {
        values = new HashMap<CellReference, String>();
        notes = new HashMap<>();
        sheet = new RangeWrapper(values, notes, null, "A1:D4");
        MockitoAnnotations.initMocks(this);
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
        SheetReader.logger = mockLogger;
        game = new Game(null, mockChannel, null, false);
        game.basicMoves = new PatriciaTrieIgnoreCase<>();
        reader = new SheetReader(game);

    }

    @Test
    public void testReadSheetSecondNewPlaybookNoDiscord() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");
        values.put(new CellReference("A1"), "test playbook2");
        reader.parseSheet(sheet);
        //With no discord name, it should skip loading without
        assertEquals(0, game.playbooks.playbooks.size());
        verify(mockLogger, Mockito.times(0)).info(SheetReader.registered_msg + "test playbook1");
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
    }

    @Test
    public void testReadSheetSecondNewPlaybookBlankDiscord() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        values.put(new CellReference("B1"), "<type discord name>");
        values.put(new CellReference("C1"), "test playbook2");
        reader.parseSheet(sheet);
        //With no discord name, it should skip loading without
        assertEquals(0, game.playbooks.playbooks.size());
        verify(mockLogger, Mockito.times(0)).info(SheetReader.registered_msg + "test playbook1");
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
    }

    @Test
    public void testReadSheetDiscordNameSinglePlaybook() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");
        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);
        assertNotNull(game.playbooks.playbooks.get("discordName"));
    }

    @Test
    public void testReadSheetDiscordNameSecondPlaybook() {
        values.put(new CellReference("A1"), "test playbook1");
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        notes.put(new CellReference("C1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("C1"), "test playbook2");

        reader.parseSheet(sheet);
        assertNotNull(game.playbooks.playbooks.get("discordName"));
    }

    @Test
    public void testReadSheetBasicMove() {
        values.put(new CellReference("A1"), "test basic move");
        notes.put(new CellReference("A1"), SheetReader.Metadata.basic_move.name());
        values.put(new CellReference("A2"), "move text");
        reader.parseSheet(sheet);
        assertEquals("test basic move", game.basicMoves.get("test basic move").name);
        assertTrue(game.basicMoves.get("test basic move").text.contains("move text"));
    }

    @Test
    public void testReadSheetPlaybook() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "test move");
        notes.put(new CellReference("A2"), SheetReader.Metadata.playbook_move.name());
        values.put(new CellReference("A3"), "move text");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertTrue(book.moves.get("test move").text.contains("move text"));
    }

    @Test
    public void testReadSheetStat() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "+2");
        notes.put(new CellReference("A2"), SheetReader.Metadata.stat.name() + "=str");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.stats.get("str").getCellRef());
    }

    @Test
    public void testReadSheetStatPenalty() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "FALSE");
        notes.put(new CellReference("A2"), SheetReader.Metadata.stat_penalty.name() + "=str, dex");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.stat_penalties.get("str").getCellRef());
        assertEquals("A2", book.stat_penalties.get("dex").getCellRef());
    }

    @Test
    public void testReadSheetResource_Single() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "12");
        notes.put(new CellReference("A2"), SheetReader.Metadata.resource.name() + "=hp");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.resources.get("hp").get(0).getCellRef());
        assertNull(book.resources.get("hp").min);
        assertNull(book.resources.get("hp").max);
    }

    @Test
    public void testReadSheetResource_MinMax() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "12");
        notes.put(new CellReference("A2"), SheetReader.Metadata.resource.name() + "=hp;min=0;max=20");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.resources.get("hp").get(0).getCellRef());
        assertEquals(Integer.valueOf(0), book.resources.get("hp").min);
        assertEquals(Integer.valueOf(20), book.resources.get("hp").max);
    }

    @Test
    public void testReadSheetResource_List() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "TRUE");
        notes.put(new CellReference("A2"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellReference("A3"), "TRUE");
        notes.put(new CellReference("A3"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellReference("A4"), "TRUE");
        notes.put(new CellReference("A4"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellReference("B3"), "TRUE");
        notes.put(new CellReference("B3"), SheetReader.Metadata.resource.name() + "=xp");
        values.put(new CellReference("B4"), "TRUE");
        notes.put(new CellReference("B4"), SheetReader.Metadata.resource.name() + "=xp");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals(5, book.resources.get("XP").size());
    }

    @Test
    public void testReadSheetResource_MovePenalty() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "12");
        notes.put(new CellReference("A2"), SheetReader.Metadata.resource.name() + "=hp;move_penalty=Cast A Spell");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("A2", book.movePenalties.get("Cast A Spell").getCellRef());

    }

    @Test
    public void testReadSheetDefaultDice() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "damage 1d10");
        notes.put(new CellReference("A2"), SheetReader.Metadata.default_dice.name() + "=Move Name");

        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetReader.Metadata.discord_name.name());
        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertEquals("1d10", book.moveOverrideDice.get("movename"));
    }

    @Test
    public void testReadSheetBadNote(){
        notes.put(new CellReference("A1"), "this should throw an exception");
        reader.parseSheet(sheet);
        verify(mockChannel).sendMessage(anyString());
    }

    @Test
    public void testParseMoveSimple() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "move text");

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
    public void testParseMoveNoteOverrideTitle() {
        values.put(new CellReference("A1"), "move text");
        notes.put(new CellReference("A1"), "playbook_move=Move Name");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("Move Name", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveNoteOverrideText() {
        values.put(new CellReference("A1"), "Move Name");
        notes.put(new CellReference("A1"), "playbook_move;text=move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("Move Name", builder.get(0));
        assertEquals("move text", builder.get(1));
    }


    @Test
    public void testParseMoveNoteOverrideTitleAndName() {
        values.put(new CellReference("A1"), "TRUE");
        values.put(new CellReference("B1"), "Shield (+1 Armor; +1 Readiness when you roll Defend 7+)");
        notes.put(new CellReference("A1"), "playbook_move=Override;text=+1 Readiness when you roll Defend 7+");
        values.put(new CellReference("A2"), "Not part of the move");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("Override", builder.get(0));
        assertEquals("+1 Readiness when you roll Defend 7+", builder.get(1));
    }

    @Test
    public void testReadSheetForParseMoveNoteOverride() {
        notes.put(new CellReference("A1"), SheetReader.Metadata.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");

        values.put(new CellReference("A2"), "TRUE");
        values.put(new CellReference("B3"), "Shield (+1 Armor; +1 Readiness when you roll Defend 7+)");
        notes.put(new CellReference("A2"), SheetReader.Metadata.playbook_move.name() + "=Override;text=+1 Readiness when you roll Defend 7+");
        values.put(new CellReference("A3"), "Not part of the move");

        values.put(new CellReference("C1"), "discordName");
        notes.put(new CellReference("C1"), SheetReader.Metadata.discord_name.name());

        reader.parseSheet(sheet);

        Playbook book = game.playbooks.playbooks.get("discordName");
        assertNotNull(book);
        assertTrue(book.moves.get("Override").text.contains("+1 Readiness when you roll Defend 7+"));
    }


    @Test
    public void testParseMoveIndentedText() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), " ");
        values.put(new CellReference("B2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleTrue() {
        values.put(new CellReference("A1"), "TRUE");
        values.put(new CellReference("B1"), "test basic move");
        values.put(new CellReference("A2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
    }

    @Test
    public void testParseMoveBooleanTitleFalse() {
        values.put(new CellReference("A1"), "FALSE");
        values.put(new CellReference("B1"), "test basic move");
        values.put(new CellReference("A2"), "move text");

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertNull(builder);
    }

    @Test
    public void testParseMoveNotList() {
        values.put(new CellReference("A1"), "test basic move");
        values.put(new CellReference("A2"), "text 1");
        values.put(new CellReference("A3"), "test 2");
        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
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
        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
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

        MoveBuilder builder = reader.parseMove(sheet, 1, 1);
        assertEquals("test basic move", builder.get(0));
        assertEquals("move text", builder.get(1));
        assertFalse(builder.get(2).contains("load"));
    }

}