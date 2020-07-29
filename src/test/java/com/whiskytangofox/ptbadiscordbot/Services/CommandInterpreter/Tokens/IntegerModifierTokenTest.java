package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerModifierTokenTest extends ITokenTest {

    IntegerModifierToken underTest = new IntegerModifierToken();

    @Test
    public void testMatchParameter() {
        assertTrue(underTest.matchesParameter(null, "+1", 0));
        assertTrue(underTest.matchesParameter(null, "3", 0));
        assertTrue(underTest.matchesParameter(null, "-10", 0));
    }

    @Test
    public void testMatchParameterFalse() {
        assertFalse(underTest.matchesParameter(null, "one", 0));
        assertFalse(underTest.matchesParameter(null, "NotANumber", 0));
        assertFalse(underTest.matchesParameter(null, "1d10", 0));
    }

    @Test
    public void testExecuteDiceAlreadySet() {
        underTest.execute(mockBook, command, "+4");
        assertEquals(4, command.mod);
    }

}