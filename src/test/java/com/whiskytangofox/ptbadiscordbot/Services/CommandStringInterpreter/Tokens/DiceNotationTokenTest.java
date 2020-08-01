package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import org.junit.Test;

import static org.junit.Assert.*;

public class DiceNotationTokenTest extends ITokenTest {


    DiceNotationToken underTest = new DiceNotationToken();

    @Test
    public void testMatchParameter() {
        assertTrue(underTest.matchesParameter(null, "1d8", 0));
        assertTrue(underTest.matchesParameter(null, "2d2", 0));
        assertTrue(underTest.matchesParameter(null, "120d212", 0));
    }

    @Test
    public void testMatchParameterFalse() {
        assertFalse(underTest.matchesParameter(null, "1d", 0));
        assertFalse(underTest.matchesParameter(null, "rolled", 0));
        assertFalse(underTest.matchesParameter(null, "12dark", 0));
    }

    @Test
    public void testExecute() {
        underTest.execute(mockBook, command, "2d6");
        assertTrue(command.doRoll);
        assertEquals(1, command.dice.size());
        assertEquals(2, command.dice.get(0).num);
        assertEquals(6, command.dice.get(0).size);
    }
}