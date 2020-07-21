package com.whiskytangofox.ptbadiscordbot.wrappers;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PatriciaTrieIgnoreCaseTest {



    @Test
    public void put() {
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<>();
        test.put("key1", "value1");
        assertTrue(test.containsValue("value1"));
        assertTrue(test.containsKey("key1"));

    }

    @Test
    public void putUpperCase() {
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
        test.put("KEY1", "value1");
        assertTrue(test.containsValue("value1"));
        assertTrue(test.containsKey("key1"));

    }

    @Test
    public void putSpace() {
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
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
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
        test.put("key1", "value1");
        assertTrue(test.get("KEY1").equals("value1"));
    }

    @Test
    public void getPartialKeySingle() throws Exception {
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
        test.put("key1", "value1");
        assertTrue(test.getClosestMatch("Key").equals("value1"));

    }

    @Test
    public void getPartialKeyConflict() {
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
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
        PatriciaTrieIgnoreCase<String> test = new PatriciaTrieIgnoreCase<String>();
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



}