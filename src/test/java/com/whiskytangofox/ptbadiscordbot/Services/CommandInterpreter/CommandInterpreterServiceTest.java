package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.SetResourceResponse;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.StatResponse;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.CommandInterpreterService.RawToken;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CommandInterpreterServiceTest {

    @Mock
    Playbook mockBook;

    Move move = new Move("Move", "Move Text");
    StatResponse stat = new StatResponse("stat", 0, false).setDebilityTag("dis");
    SetResourceResponse resource = new SetResourceResponse("resource", 0, 1, 1);

    CommandInterpreterService underTest = new CommandInterpreterService();

    @Before
    public void before() throws KeyConflictException, IOException, DiscordBotException {
        MockitoAnnotations.initMocks(this);
        when(mockBook.isMove("move")).thenReturn(true);
        when(mockBook.getMove("move")).thenReturn(move);
        when(mockBook.isStat("stat")).thenReturn(true);
        when(mockBook.getStat("stat")).thenReturn(stat);
        when(mockBook.isResource("resource")).thenReturn(true);
        when(mockBook.modifyResource("resource", 1)).thenReturn(resource);
        when(mockBook.getSetting(GameSettings.KEY.default_system_dice)).thenReturn("2d6");
    }

    @Test
    public void mapToken() {
        RawToken raw = underTest.mapToken(mockBook, "roll", 0);
        assertEquals("roll", raw.string);
        assertTrue(raw.token instanceof RollDefaultToken);
    }

    @Test
    public void mapToken_RollIsRegisteredAsAMoveAndAStat() throws KeyConflictException {
        when(mockBook.isMove("roll")).thenReturn(true);
        when(mockBook.isStat("stat")).thenReturn(true);
        RawToken raw = underTest.mapToken(mockBook, "roll", 0);
        assertEquals("roll", raw.string);
        assertTrue(raw.token instanceof RollDefaultToken);
    }

    @Test
    public void mapToken_Exception() {
        Throwable e = null;
        try {
            underTest.mapToken(mockBook, "Unrecognizable", 0);
        } catch (Throwable exception) {
            e = exception;
        }
        assertTrue(e instanceof IllegalArgumentException);
        assertFalse(e instanceof IndexOutOfBoundsException);
    }

    @Test
    public void tokenizeStringCommand() {
        List<RawToken> result = underTest.tokenizeStringCommand(mockBook, "roll");
        assertEquals(1, result.size());
        assertTrue(result.get(0).token instanceof RollDefaultToken);
    }

    @Test
    public void tokenizeStringCommand_testIndex() throws KeyConflictException {
        when(mockBook.isMove("roll")).thenReturn(true);
        List<RawToken> result = underTest.tokenizeStringCommand(mockBook, "roll roll");
        assertEquals(2, result.size());
        assertTrue(result.get(0).token instanceof RollDefaultToken);
        assertTrue(result.get(1).token instanceof MoveToken);
    }

    @Test
    public void testFinalizeCommandString_SetDefault() throws KeyConflictException, DiscordBotException, IOException {
        Command command = new Command(mockBook, "testString");
        command.doRoll = true;
        underTest.finalizeCommand(mockBook, command);
        assertEquals(1, command.dice.size());
    }

    @Test
    public void testFinalizeCommandString_GetMoveStat() throws KeyConflictException, DiscordBotException, IOException {
        //TODO
        when(mockBook.getMoveStat("move")).thenReturn(stat);
        Command command = new Command(mockBook, "testString");
        command.doRoll = true;
        command.move = new Move("move", "roll +stat");
        underTest.finalizeCommand(mockBook, command);
        assertEquals(stat, command.stat);
    }

    @Test
    public void testFinalizeCommandString_GetMovePenalty() throws KeyConflictException, DiscordBotException, IOException {
        //TODO
        when(mockBook.getMovePenalty("move")).thenReturn(-100);
        Command command = new Command(mockBook, "testString");
        command.doRoll = true;
        command.move = new Move("move", "roll");
        underTest.finalizeCommand(mockBook, command);
        assertEquals(-100, command.mod);
    }


    @Test
    public void tokenizeStringCommand_long() {
        List<RawToken> result = underTest.tokenizeStringCommand(mockBook, "roll move stat +2 adv");
        assertEquals(5, result.size());
        assertTrue(result.get(0).token instanceof RollDefaultToken);
        assertTrue(result.get(1).token instanceof MoveToken);
        assertTrue(result.get(2).token instanceof StatToken);
        assertTrue(result.get(3).token instanceof IntegerModifierToken);
        assertTrue(result.get(4).token instanceof AdvantageToken);
    }

    @Test
    public void interpretCommandString_Roll() throws IOException, DiscordBotException, KeyConflictException {
        Command result = underTest.interpretCommandString(mockBook, "roll move stat -1 dis");
        assertTrue(result.doRoll);
        assertEquals(move, result.move);
        assertEquals(stat, result.stat);
        assertEquals(-1, result.mod);
        assertTrue(result.dice.get(0).dis);
    }

    @Test
    public void interpretCommandString_Move() throws IOException, DiscordBotException, KeyConflictException {
        Command result = underTest.interpretCommandString(mockBook, "move");
        assertFalse(result.doRoll);
        assertEquals(move, result.move);
        assertEquals(0, result.dice.size());
    }

    @Test
    public void interpretCommandString_Resource() throws IOException, DiscordBotException, KeyConflictException {
        Command result = underTest.interpretCommandString(mockBook, "resource +2");
        assertFalse(result.doRoll);
        assertEquals("resource", result.resource);
        assertEquals(2, result.mod);
        assertEquals(0, result.dice.size());
    }


}