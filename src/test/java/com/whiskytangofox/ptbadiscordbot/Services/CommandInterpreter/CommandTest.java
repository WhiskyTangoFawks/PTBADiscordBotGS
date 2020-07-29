package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class CommandTest {

    Command command;

    @Mock
    Playbook mockBook;

    @Mock
    GoogleSheetAPI mockApi;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        mockBook.sheet = new SheetAPIService(null, mockApi, new GameSettings());
        when(mockBook.getSetting(GameSettings.KEY.default_system_dice)).thenReturn("2d6");
        when(mockBook.getSetting(GameSettings.KEY.fail_xp)).thenReturn("true");
        command = new Command(mockBook, null);
    }

    @Test
    public void testParseDieNotation() {
        command.parseDieNotation("2d6");
        assertEquals(1, command.dice.size());
        assertEquals(2, command.dice.get(0).num);
        assertEquals(6, command.dice.get(0).size);

        command.parseDieNotation("1d4");
        assertEquals(2, command.dice.size());
        assertEquals(2, command.dice.get(0).num);
        assertEquals(6, command.dice.get(0).size);
        assertEquals(1, command.dice.get(1).num);
        assertEquals(4, command.dice.get(1).size);
    }

    @Test
    public void testSetDefaultDiceNoMove() {
        command.setDefaultDice();
        assertEquals(1, command.dice.size());
        assertEquals(2, command.dice.get(0).num);
        assertEquals(6, command.dice.get(0).size);
        assertTrue(command.failMsg);
    }

    @Test
    public void testSetDefaultDiceWithMoveNoOverride() {
        command.move = new Move("Move", "move text");
        command.setDefaultDice();
        assertEquals(1, command.dice.size());
        assertEquals(2, command.dice.get(0).num);
        assertEquals(6, command.dice.get(0).size);
        assertTrue(command.failMsg);
    }

    @Test
    public void testSetDefaultDiceWithMoveWithOverride() {
        command.move = new Move("Move", "move text");
        when(mockBook.getMoveDice("Move")).thenReturn("1d10");
        command.setDefaultDice();
        assertEquals(1, command.dice.size());
        assertEquals(1, command.dice.get(0).num);
        assertEquals(10, command.dice.get(0).size);
        assertFalse(command.failMsg);
    }
}