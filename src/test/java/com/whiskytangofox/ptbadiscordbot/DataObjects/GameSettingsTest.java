package com.whiskytangofox.ptbadiscordbot.DataObjects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameSettingsTest {

    GameSettings underTest;

    @BeforeEach
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