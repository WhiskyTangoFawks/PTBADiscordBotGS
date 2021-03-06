package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter;

import com.whiskytangofox.ptbadiscordbot.DataObjects.GameSettings;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class CommandTest {

    Command command;

    @Mock
    Playbook mockBook;

    @Mock
    GoogleSheetAPI mockApi;

    @BeforeEach
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
        when(mockBook.getMoveDice("move")).thenReturn("1d10");
        command.setDefaultDice();
        assertEquals(1, command.dice.size());
        assertEquals(1, command.dice.get(0).num);
        assertEquals(10, command.dice.get(0).size);
        assertFalse(command.failMsg);
    }

    @Test
    public void testHasStatFalse() {
        command.addModifier("test1", Command.TYPE.INTEGER, "-1");
        assertFalse(command.hasStat());
    }

    @Test
    public void testHasStatTrue() {
        command.addModifier("test1", Command.TYPE.STAT, "-1");
        command.addModifier("test2", Command.TYPE.INTEGER, "-1");
        assertTrue(command.hasStat());
    }

    Character neg = '-';
    Character pos = '+';

    @Test
    public void testInstantiateModifier() {
        Command.Modifier mod = new Command.Modifier("name", Command.TYPE.STAT, "-3");
        assertEquals("name", mod.name);
        assertEquals(Command.TYPE.STAT, mod.type);
        assertEquals(pos, mod.commandSign);
        assertEquals(-3, mod.mod);
    }

    @Test
    public void testInstantiateModifier_DoubleNegative() {
        Command.Modifier mod = new Command.Modifier("name", Command.TYPE.STAT, "--3");
        assertEquals("name", mod.name);
        assertEquals(Command.TYPE.STAT, mod.type);
        assertEquals(neg, mod.commandSign);
        assertEquals(-3, mod.mod);
    }

    @Test
    public void testInstantiateModifier_DoublePositive() {
        Command.Modifier mod = new Command.Modifier("name", Command.TYPE.STAT, "++3");
        assertEquals("name", mod.name);
        assertEquals(Command.TYPE.STAT, mod.type);
        assertEquals(pos, mod.commandSign);
        assertEquals(3, mod.mod);
    }

    @Test
    public void testInstantiateModifier_NegPos() {
        Command.Modifier mod = new Command.Modifier("name", Command.TYPE.STAT, "-+3");
        assertEquals("name", mod.name);
        assertEquals(Command.TYPE.STAT, mod.type);
        assertEquals(neg, mod.commandSign);
        assertEquals(-3, mod.mod);
    }

    @Test
    public void testInstantiateModifier_PosNeg() {
        Command.Modifier mod = new Command.Modifier("name", Command.TYPE.STAT, "+-3");
        assertEquals("name", mod.name);
        assertEquals(Command.TYPE.STAT, mod.type);
        assertEquals(pos, mod.commandSign);
        assertEquals(-3, mod.mod);
    }
}