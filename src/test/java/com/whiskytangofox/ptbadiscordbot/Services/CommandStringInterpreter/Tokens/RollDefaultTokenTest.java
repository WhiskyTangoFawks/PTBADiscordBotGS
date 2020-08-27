package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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