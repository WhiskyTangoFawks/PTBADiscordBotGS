package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.GameGoogle;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SheetParserServiceTest {

    static GameGoogle game;

    @Mock
    static Logger mockLogger;

    @Mock
    static MessageChannel mockChannel;

    @Mock
    static MessageAction mockMessageAction;

    //@Rule
    //public MockitoRule mockitoRule = MockitoJUnit.rule();

    static RangeWrapper sheet;
    static HashMap<CellReference, String> values;
    static HashMap<CellReference, String> notes;

    static SheetParserService reader;

    @BeforeEach
    public void before() throws IOException {
        values = new HashMap<>();
        notes = new HashMap<>();
        sheet = new RangeWrapper(values, notes, null, "A1:D4");
        MockitoAnnotations.initMocks(this);
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
        SheetParserService.logger = mockLogger;
        game = new GameGoogle(null, mockChannel, null, false);
        game.basicMoves = new PatriciaTrieIgnoreCase<>();
        reader = new SheetParserService(game);
    }

    @Test
    public void testReadSheetSecondNewPlaybookNoDiscord() {
        notes.put(new CellReference("A1"), SheetParserService.PARSER.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");
        values.put(new CellReference("A1"), "test playbook2");
        reader.parseSheet(sheet);
        //With no discord name, it should skip loading without
        assertEquals(0, game.playbooks.playbooks.size());
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
    }

    @Test
    public void testReadSheetSecondNewPlaybookBlankDiscord() {
        notes.put(new CellReference("A1"), SheetParserService.PARSER.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");
        notes.put(new CellReference("B1"), SheetParserService.PARSER.discord_name.name());
        values.put(new CellReference("B1"), "<type discord name>");
        values.put(new CellReference("C1"), "test playbook2");
        reader.parseSheet(sheet);
        //With no discord name, it should skip loading without
        assertEquals(0, game.playbooks.playbooks.size());
        verify(mockLogger, Mockito.times(0)).warn("Unexpected exception reading sheet");
    }

    @Test
    public void testReadSheetDiscordNameSinglePlaybook() {
        notes.put(new CellReference("A1"), SheetParserService.PARSER.new_playbook.name());
        values.put(new CellReference("A1"), "test playbook1");
        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetParserService.PARSER.discord_name.name());
        reader.parseSheet(sheet);
        assertNotNull(game.playbooks.playbooks.get("discordName"));
    }

    @Test
    public void testReadSheetDiscordNameSecondPlaybook() {
        values.put(new CellReference("A1"), "test playbook1");
        notes.put(new CellReference("A1"), SheetParserService.PARSER.new_playbook.name());
        values.put(new CellReference("B1"), "discordName");
        notes.put(new CellReference("B1"), SheetParserService.PARSER.discord_name.name());
        notes.put(new CellReference("C1"), SheetParserService.PARSER.new_playbook.name());
        values.put(new CellReference("C1"), "test playbook2");

        reader.parseSheet(sheet);
        assertNotNull(game.playbooks.playbooks.get("discordName"));
    }

    @Test
    public void testReadSheetBadNote() {
        notes.put(new CellReference("A1"), "this should throw an exception");
        reader.parseSheet(sheet);
        verify(mockChannel).sendMessage(anyString());
    }

    @Test
    public void testReplaceCellReference() {
        values.put(new CellReference("A1"), "new");
        String note = "playbook_move=[A1]";
        //notes.put(new CellReference("A2"), note);
        assertEquals("playbook_move=new", reader.replaceCellReferences(sheet, null, note));
    }

    @Test
    public void testReplaceCellReference_Relative() {
        values.put(new CellReference(1, 3), "new");
        String note = "playbook_move=[-1,+1]";
        CellReference cell = new CellReference(2, 2);
        assertEquals("playbook_move=new", reader.replaceCellReferences(sheet, cell, note));
    }


    @Test
    public void testReplaceCellReference_exception() {
        values.put(new CellReference("A1"), "new");
        String note = "playbook_move=[NotACellReference]";
        //notes.put(new CellReference("A1"), note);
        Throwable error = null;
        try {
            reader.replaceCellReferences(sheet, null, note);
        } catch (Throwable e) {
            error = e;
        }
        assertTrue(error instanceof IllegalArgumentException);
    }




}