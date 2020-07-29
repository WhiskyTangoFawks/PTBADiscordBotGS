package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import org.junit.Test;

import static org.junit.Assert.*;

public class AdvantageTokenTest extends ITokenTest {
    AdvantageToken underTest = new AdvantageToken();

    @Test
    public void testMatchParameter() {
        assertTrue(underTest.matchesParameter(null, "adv", 0));
    }

    @Test
    public void testMatchParameterFalse() {
        assertFalse(underTest.matchesParameter(null, "advanced", 0));
        assertFalse(underTest.matchesParameter(null, "ad", 0));
    }

    @Test
    public void testExecuteDiceAlreadySet() {
        command.dice.add(new Dice(1, 8));
        command.doRoll = true;
        underTest.execute(mockBook, command, "adv");
        assertTrue(command.doRoll);
        assertEquals(1, command.dice.size());
        assertEquals(1, command.dice.get(0).num);
        assertEquals(8, command.dice.get(0).size);
        assertTrue(command.dice.get(0).adv);
    }

    @Test
    public void testExecuteDiceNotSet() {
        underTest.execute(mockBook, command, "adv");
        assertTrue(command.doRoll);
        assertEquals(1, command.dice.size());
        assertEquals(2, command.dice.get(0).num);
        assertEquals(6, command.dice.get(0).size);
        assertTrue(command.dice.get(0).adv);
    }
}