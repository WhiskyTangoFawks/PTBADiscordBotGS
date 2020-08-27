package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class MoveTokenTest extends ITokenTest {

    MoveToken underTest = new MoveToken();

    @Test
    public void testMatchesParameter() {
        when(mockBook.isMove("Move")).thenReturn(true);
        assertTrue(underTest.matchesParameter(mockBook, "Move", 0));
    }

    @Test
    public void testExecute() throws KeyConflictException {
        Move move = new Move("Move", "Move Text");
        when(mockBook.getMove("Move")).thenReturn(move);
        underTest.execute(mockBook, command, "Move");
        assertEquals(move, command.move);
    }

}