package com.whiskytangofox.ptbadiscordbot.DataObjects;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MoveTest {

    static HashSet<String> stats = new HashSet<String>();

    @BeforeClass
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




}