package com.whiskytangofox.ptbadiscordbot.DataObjects;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MoveBuilderTest {

    static MoveBuilder builder;
    static String[] stats = {"str", "dex", "con", "int", "wis", "cha"};
    static String[] hack = {"Hack and Slash", "When you fight in melee or close quarters, roll +STR: on a 10+, your maneuver works as expected (Deal Damage) and pick 1:"+
            "• Evade, prevent, or counter the enemy’s attack • Strike hard and fast, for 1d6 extra damage, but suffer the enemy’s attack" +
            "On a 7-9, your maneuver works, mostly. Deal Damage but suffer the enemy’s attack."};

    //@Rule
    //public MockitoRule mockitoRule = MockitoJUnit.rule();

    @BeforeAll
    public static void buildHackAndSlash(){

    }

    @BeforeEach
    public void setupMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuild() {
        builder = new MoveBuilder();
        builder.addLine(hack[0]);
        builder.addLine(hack[1]);

        Move move = builder.getMove();
        assertEquals(hack[0], move.name);
        assertEquals("**" + hack[0] + "**" + System.lineSeparator() + hack[1], move.text);
    }

    @Test
    public void testBuild_overrideName() {
        builder = new MoveBuilder();
        builder.overrideName = "Move";
        builder.addLine("move text");

        Move move = builder.getMove();
        assertEquals("Move", move.name);
        assertEquals("**Move**" + System.lineSeparator() + "move text", move.text);
    }

    @Test
    public void testBuild_overrideText() {
        builder = new MoveBuilder();
        builder.addLine("Move");
        builder.overrideText = "move text";

        Move move = builder.getMove();
        assertEquals("Move", move.name);
        assertEquals("**Move**" + System.lineSeparator() + "move text", move.text);
    }


}