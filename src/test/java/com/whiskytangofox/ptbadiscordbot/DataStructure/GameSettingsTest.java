package com.whiskytangofox.ptbadiscordbot.DataStructure;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GameSettingsTest {

    GameSettings underTest;

    @Before
    public void before() {
        underTest = new GameSettings();
    }

    @Test
    public void testSet() {
        for (GameSettings.KEY key : GameSettings.KEY.values()) {
            underTest.set(key, "test " + key.name());
        }
        assertEquals(4, underTest.properties.size());
    }

    @Test
    public void get() {
        for (GameSettings.KEY key : GameSettings.KEY.values()) {
            underTest.set(key, "test " + key.name());
        }
        for (GameSettings.KEY key : GameSettings.KEY.values()) {
            assertEquals("test " + key.name(), underTest.get(key));
        }
    }


}