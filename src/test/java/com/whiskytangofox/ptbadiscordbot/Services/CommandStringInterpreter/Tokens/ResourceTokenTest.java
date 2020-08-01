package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ResourceTokenTest extends ITokenTest {

    ResourceToken underTest = new ResourceToken();

    @Test
    public void testMatchesParameter() {
        when(mockBook.isResource("resource")).thenReturn(true);
        assertTrue(underTest.matchesParameter(mockBook, "resource", 0));
    }

    @Test
    public void testMatchesParameterNotIndex0() {
        when(mockBook.isResource("resource")).thenReturn(true);
        assertFalse(underTest.matchesParameter(mockBook, "resource", 1));
    }

    @Test
    public void testMatchesParameterFalse() {
        when(mockBook.isResource("resource")).thenReturn(true);
        assertFalse(underTest.matchesParameter(mockBook, "foo", 0));
    }

    public void testExecute() {
        underTest.execute(mockBook, command, "resource");
        assertEquals(command.resource, "resource");
    }

}