package com.whiskytangofox.ptbadiscordbot.wrappers;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class HashMapIgnoreCaseTest {

    static HashMapIgnoreCase<String> map;

    @Before
    public void before(){
        map = new HashMapIgnoreCase<String>();
    }

    @Test
    public void get() {
        map.put("Test Key", "test value");
        assertTrue(map.keySet().contains("testkey"));
        assertTrue(map.get("Test Key") == "test value");
        assertTrue(map.get("TestKey") == "test value");
        assertTrue(map.get("Testkey") == "test value");
    }

    @Test
    public void put() {
        map.put("Test Key", "test value");
        assertTrue(map.keySet().contains("testkey"));
    }

    @Test
    public void containsKey() {
        map.put("Test Key", "test value");
        assertTrue(map.containsKey("testkey"));
        assertTrue(map.containsKey("test key"));
        assertTrue(map.containsKey("Test Key"));
    }
}