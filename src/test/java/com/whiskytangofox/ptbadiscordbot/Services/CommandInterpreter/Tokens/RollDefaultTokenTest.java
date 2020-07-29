package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RollDefaultTokenTest extends ITokenTest {

    RollDefaultToken underTest = new RollDefaultToken();

    @Test
    public void testMatchParameter() {
        assertTrue(underTest.matchesParameter(null, "roll", 0));
    }

    @Test
    public void testMatchParameterButNotIndex0() {
        assertFalse(underTest.matchesParameter(null, "roll", 1));
    }

    @Test
    public void testMatchParameterCase() {
        assertTrue(underTest.matchesParameter(null, "ROLL", 0));
    }

    @Test
    public void testExecute() {
        underTest.execute(mockBook, command, "roll");
        assertTrue(command.doRoll);
    }

}