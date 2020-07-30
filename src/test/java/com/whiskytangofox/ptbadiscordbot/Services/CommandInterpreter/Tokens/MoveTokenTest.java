package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class MoveTokenTest extends ITokenTest {

    MoveToken underTest = new MoveToken();

    @Test
    public void testMatchesParameter() throws KeyConflictException {
        when(mockBook.isMove("Move")).thenReturn(true);
        assertTrue(underTest.matchesParameter(mockBook, "Move", 0));
    }

    @Test
    public void testExecute() throws KeyConflictException, IOException {
        Move move = new Move("Move", "Move Text");
        when(mockBook.getMove("Move")).thenReturn(move);
        underTest.execute(mockBook, command, "Move");
        assertEquals(move, command.move);
    }

    @Test
    public void testExecute_WithMovePenalty() throws KeyConflictException, IOException {
        Move move = new Move("Move", "Move Text");
        when(mockBook.getMove("Move")).thenReturn(move);
        when(mockBook.getMovePenalty("move")).thenReturn(-1);

        underTest.execute(mockBook, command, "Move");
        assertEquals(move, command.move);
        assertEquals(-1, command.mod);
    }


}