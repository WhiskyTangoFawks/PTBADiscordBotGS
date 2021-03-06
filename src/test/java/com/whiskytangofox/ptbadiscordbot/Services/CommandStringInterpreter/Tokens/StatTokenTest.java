package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.StatResponse;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class StatTokenTest extends ITokenTest {

    StatToken underTest = new StatToken();

    @Test
    public void testMatchesParameter() {
        when(mockBook.isStat("stat")).thenReturn(true);
        assertTrue(underTest.matchesParameter(mockBook, "stat", 1));
    }

    @Test
    public void testMatchesParameterPlus() {
        when(mockBook.isStat("stat")).thenReturn(true);
        assertTrue(underTest.matchesParameter(mockBook, "+stat", 1));
    }

    @Test
    public void testMatchesParameterMinus() {
        when(mockBook.isStat("stat")).thenReturn(true);
        assertTrue(underTest.matchesParameter(mockBook, "-stat", 1));
    }

    @Test
    public void testExecute() throws IOException, DiscordBotException {
        StatResponse response = new StatResponse("stat", 2, false);
        when(mockBook.getStat("stat")).thenReturn(response);
        underTest.execute(mockBook, command, "stat");
        assertEquals(1, command.modifiers.stream()
                .filter(m -> m.type == Command.TYPE.STAT)
                .filter(m -> m.mod == 2)
                .count());
        assertEquals(0, command.rawToParse.size());
    }

    @Test
    public void testExecuteDebility() throws IOException, DiscordBotException {
        StatResponse response = new StatResponse("stat", 2, true);
        response.debilityTag = "dis";
        when(mockBook.getStat("stat")).thenReturn(response);
        underTest.execute(mockBook, command, "stat");
        assertEquals(1, command.modifiers.stream()
                .filter(m -> m.type == Command.TYPE.STAT)
                .filter(m -> m.mod == 2)
                .count());
        assertEquals(1, command.rawToParse.size());
        assertEquals("dis", command.rawToParse.peek());
    }
}