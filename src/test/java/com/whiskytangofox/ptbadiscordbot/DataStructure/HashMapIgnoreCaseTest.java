package com.whiskytangofox.ptbadiscordbot.DataStructure;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class HashMapIgnoreCaseTest {

    static HashMapIgnoreCase<String> map;

    @Before
    public void before(){
        map = new HashMapIgnoreCase<>();
    }

    @Test
    public void get() {
        map.put("Test Key", "test value");
        assertTrue(map.containsKey("testkey"));
        assertEquals("test value", map.get("Test Key"));
        assertEquals("test value", map.get("TestKey"));
        assertEquals("test value", map.get("Testkey"));
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


}