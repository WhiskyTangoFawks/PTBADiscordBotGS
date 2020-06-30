package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.junit.Assert.*;

public class ParsedCommandTest {

    public static final Logger logger = LoggerFactory.getLogger(MoveLoaderTest.class);
    static String sheetID = "1RR95L3cZUSJczoyHuevsZ8KFPNfOcNVQKcUKLJlvz5I";
    static App app;

    static Game game;
    static ParsedCommand tester;

    @Mock
    static MessageChannel mockChannel;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @BeforeClass
    public static void setupGame() throws Exception {
        logger.info("Running @BeforeClass Setup");
        app = new App();
        app.googleSheetAPI = new GoogleSheetAPI();
        game = new Game(mockChannel, sheetID);
    }

    @Before
    public void setupParseCommandForMethodTesting() throws Exception {
        tester = new ParsedCommand(game, null, null);
    }

    @Test
    public void testIsDieNotation(){
        assertTrue(tester.isDieNotation("1d2"));
        assertTrue(tester.isDieNotation("2d9"));
        assertTrue(tester.isDieNotation("10d6"));
        assertTrue(tester.isDieNotation("100d12"));

        assertFalse(tester.isDieNotation("d6"));
        assertFalse(tester.isDieNotation("ad12"));
        assertFalse(tester.isDieNotation("adv"));
        assertFalse(tester.isDieNotation("120ad12"));
        assertFalse(tester.isDieNotation("120"));
    }

    @Test public void testParseDieNotation() throws Exception {
        tester.parseDieNotation("2d6");
        assertEquals(1, tester.dice.size());
        assertEquals(2, tester.dice.get(0).num);
        assertEquals(6, tester.dice.get(0).size);

        tester.parseDieNotation("1d4");
        assertEquals(2, tester.dice.size());
        assertEquals(2, tester.dice.get(0).num);
        assertEquals(6, tester.dice.get(0).size);
        assertEquals(1, tester.dice.get(1).num);
        assertEquals(4, tester.dice.get(1).size);
    }

    @Test
    public void testGetRollResult1() throws Exception {
        tester.splitAndParseCommand("roll");
        String results = tester.getRollResults();
        logger.info(results);
        assertTrue(results.contains("[2d6]"));
    }

    @Test
    public void testGetRollResult2() throws Exception {
        tester.splitAndParseCommand("roll adv");
        String results = tester.getRollResults();
        logger.info(results);
        assertTrue(results.contains("[3d6]") && results.contains("~~"));
    }

    @Test
    public void testGetRollResult3() throws Exception {
        tester.splitAndParseCommand("roll dis");
        String results = tester.getRollResults();
        logger.info(results);
        assertTrue(results.contains("[3d6]") && results.contains("~~"));
    }

    @Test
    public void testGetRollResult4() throws Exception {
        tester.splitAndParseCommand("roll 2d6+4");
        String results = tester.getRollResults();
        logger.info(results);
        assertTrue(results.contains("[2d6 +4]"));
    }

    @Test
    public void testGetRollResult5() throws Exception {
        tester.splitAndParseCommand("roll 2d6 adv +1d4");
        String results = tester.getRollResults();
        assertTrue(results.contains("[3d6 +1d4]"));
    }

    @Test
    public void splitAndParseCommand1() throws Exception {
        tester.splitAndParseCommand("roll");
        assertEquals(true, tester.doRoll);
    }

    @Test
    public void splitAndParseCommand2() throws Exception {
        tester.splitAndParseCommand("roll adv");
        assertEquals(true, tester.doRoll);
        assertEquals(true, tester.dice.get(0).adv);
        assertEquals(false, tester.dice.get(0).dis);
    }

    @Test
    public void splitAndParseCommand3() throws Exception {
        tester.splitAndParseCommand("roll dis");
        assertEquals(true, tester.doRoll);
        assertEquals(false, tester.dice.get(0).adv);
        assertEquals(true, tester.dice.get(0).dis);
    }

    @Test
    public void splitAndParseCommand4() throws Exception {
        game.loadBasicMoves("Basic Moves!B2:AJ34,Violence & Recovery Moves!A1:BC27");
        tester.splitAndParseCommand("hack");
        assertEquals("Hack and Slash", tester.move.name);
        //assertEquals("STR", tester.stat);
    }

    @Test
    public void splitAndParseCommand5() throws Exception {
        tester = new ParsedCommand(game, "test1", null);
        game.loadBasicMoves("Violence & Recovery Moves!A1:BC27");
        game.loadProperties();
        game.storePlayerTab();
        game.loadDiscordNamesFromStoredPlayerTab();
        tester.splitAndParseCommand("roll hack +10");
        assertEquals("Hack and Slash", tester.move.name);

        String results = tester.getRollResults();
        logger.info(results);
        assertEquals(1, tester.dice.size());
        assertEquals(2, tester.dice.get(0).num);
        assertEquals(6, tester.dice.get(0).size);
        assertEquals("str", tester.stat);
        assertTrue(results.contains("[2d6 +(str)"));

    }


}