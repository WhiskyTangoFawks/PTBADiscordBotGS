package com.whiskytangofox.ptbadiscordbot.wrappers;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class MoveWrapperTest {

    static HashSet<String> stats = new HashSet<String>();

    @BeforeClass
    public static void beforeClass(){
        stats.add("str");
    }

    @Test
    public void getMoveStat() {
        MoveWrapper wrapper = new MoveWrapper("test", "test text roll +str");
        assertEquals("str", wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatSpace() {
        MoveWrapper wrapper = new MoveWrapper("test", "test text roll + str");
        assertEquals("str", wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatCaps() {
        MoveWrapper wrapper = new MoveWrapper("test", "test text roll +STR");
        assertEquals("str", wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatNoStat() {
        MoveWrapper wrapper = new MoveWrapper("test", "test text roll +BLARG");
        assertNull(wrapper.getMoveStat(stats));
    }

    @Test
    public void getMoveStatNoRoll() {
        MoveWrapper wrapper = new MoveWrapper("test", "test text +str");
        assertNull(wrapper.getMoveStat(stats));
    }




}