package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.Game;
import com.whiskytangofox.ptbadiscordbot.PlayerNotFoundException;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class MoveBuilderTest {

    static MoveBuilder builder;
    static String[] stats = {"str", "dex", "con", "int", "wis", "cha"};
    static String[] hack = {"Hack and Slash", "When you fight in melee or close quarters, roll +STR: on a 10+, your maneuver works as expected (Deal Damage) and pick 1:"+
            "• Evade, prevent, or counter the enemy’s attack • Strike hard and fast, for 1d6 extra damage, but suffer the enemy’s attack" +
            "On a 7-9, your maneuver works, mostly. Deal Damage but suffer the enemy’s attack."};

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    static Game mockGame;


    @BeforeClass
    public static void buildHackAndSlash(){
        builder = new MoveBuilder();
        builder.addLine();
        builder.set(0, hack[0]);
        builder.addLine();
        builder.set(1, hack[1]);
    }

    @Before
    public void setupMocks() throws PlayerNotFoundException {
        MockitoAnnotations.initMocks(this);
        when(mockGame.getStatsForPlayer(anyString())).thenReturn(Arrays.asList(stats));
    }

    @Test
    public void getModifiesMoves() {
    }

    @Test
    public void testBuild(){
        MoveWrapper move = builder.getMove();
        assertEquals(hack[0], move.name);
        assertEquals("**"+hack[0]+"**"+System.lineSeparator()+hack[1], move.text);
    }



}