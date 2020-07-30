package com.whiskytangofox.ptbadiscordbot.DataObjects;

import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PatriciaTrieIgnoreCaseTest {

    PatriciaTrieIgnoreCase<String> test;

    @Before
    public void before() {
        test = new PatriciaTrieIgnoreCase<>();
    }

    @Test
    public void put() {
        test.put("key1", "value1");
        assertTrue(test.containsValue("value1"));
        assertTrue(test.containsKey("key1"));

    }

    @Test
    public void putUpperCase() {
        test.put("KEY1", "value1");
        assertTrue(test.containsValue("value1"));
        assertTrue(test.containsKey("key1"));

    }

    @Test
    public void putSpace() {
        test.put("KEY 1", "value1");
        assertTrue(test.containsValue("value1"));
        assertTrue(test.containsKey("key1"));
    }

    @Test
    public void containsKey() {
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
        test.put("key1", "value1");
        assertTrue(test.containsKey("KEY1"));
    }

    @Test
    public void get() {
        test.put("key1", "value1");
        assertTrue(test.get("KEY1").equals("value1"));
    }

    @Test
    public void getPartialKeySingle() throws Exception {
        test.put("key1", "value1");
        assertTrue(test.getClosestMatch("Key").equals("value1"));

    }

    @Test
    public void getPartialKeyConflict() {
        test.put("key1", "value1");
        test.put("Key2", "value2");
        boolean thrown = false;
        try {
            test.getClosestMatch("K");
        } catch (Exception e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void putPartialKeyConflict() {
        test.put("key11", "value1");
        test.put("Key12", "value2");
        boolean thrown = false;
        try {
            test.getClosestMatch("K");
        } catch (Exception e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testCleanKey() {
        assertEquals("move", test.cleanKey("Move (Basic Move)"));
    }


}