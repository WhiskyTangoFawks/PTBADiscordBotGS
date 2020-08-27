package com.whiskytangofox.ptbadiscordbot.DataObjects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class MoveTest {

    static HashSet<String> stats = new HashSet<>();

    @BeforeAll
    public static void beforeClass() {
        stats.add("str");
    }

    @Test
    public void getMoveStat() {
        Move wrapper = new Move("test", "test text roll +str");
        assertEquals("str", wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatSpace() {
        Move wrapper = new Move("test", "test text roll + str");
        assertEquals("str", wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatCaps() {
        Move wrapper = new Move("test", "test text roll +STR");
        assertEquals("str", wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatNoStat() {
        Move wrapper = new Move("test", "test text roll +BLARG");
        assertNull(wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatNoRoll() {
        Move wrapper = new Move("test", "test text +str");
        assertNull(wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveReferenceName() {
        Move move = new Move("Move (Test)", "test text +str");
        assertEquals("move", move.getReferenceMoveName());
    }

    @Test
    public void testGetModifiedCopy() {
        Move basicMove = new Move("Move", "test text");
        Move childMove = new Move("Secondary Move (Basic Move)", "More move text");
        Move modifiedBasicMove = basicMove.getModifiedCopy(childMove);
        assertNotEquals(basicMove, modifiedBasicMove);
        assertEquals(modifiedBasicMove.parentMove, childMove);
    }

}