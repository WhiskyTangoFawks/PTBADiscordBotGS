package com.whiskytangofox.ptbadiscordbot.DataObjects;

import com.whiskytangofox.ptbadiscordbot.DataStructure.HashMapIgnoreCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HashMapIgnoreCaseTest {

    static HashMapIgnoreCase<String> map;

    @Before
    public void before(){
        map = new HashMapIgnoreCase<String>();
    }

    @Test
    public void get() {
        map.put("Test Key", "test value");
        assertTrue(map.containsKey("testkey"));
        assertTrue(map.get("Test Key") == "test value");
        assertTrue(map.get("TestKey") == "test value");
        assertTrue(map.get("Testkey") == "test value");
    }

    @Test
    public void put() {
        map.put("Test Key", "test value");
        assertTrue(map.containsKey("testkey"));
    }

    @Test
    public void containsKey() {
        map.put("Test Key", "test value");
        assertTrue(map.containsKey("testkey"));
        assertTrue(map.containsKey("test key"));
        assertTrue(map.containsKey("Test Key"));
    }

    @Test
    public void testCleanKey() {
        assertEquals("move", map.cleanKey("Move (Basic Move)"));
    }

}