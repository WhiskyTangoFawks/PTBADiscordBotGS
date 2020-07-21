package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.wrappers.*;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ParsedCommandTest {

    public static final Logger logger = LoggerFactory.getLogger(ParsedCommandTest.class);

    static ParsedCommand tester;
    static MoveWrapper move;
    static MoveBuilder builder;

    static final String testPlayer = "test1";

    @Mock
    Game mockGame;

    @Mock
    static MessageChannel mockChannel;

    @Mock
    static MessageAction mockMessageAction;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @BeforeClass
    public static void setupGame() throws Exception {
        logger.info("Running @BeforeClass Setup");

        builder = new MoveBuilder();
        builder.addLine();
        builder.set(0, "Hack and Slash");
        builder.addLine();
        builder.set(1, "When you fight in melee or close quarters, roll +STR: on a 10+, your maneuver works as expected (Deal Damage) and pick 1:"+
                "• Evade, prevent, or counter the enemy’s attack • Strike hard and fast, for 1d6 extra damage, but suffer the enemy’s attack" +
                "On a 7-9, your maneuver works, mostly. Deal Damage but suffer the enemy’s attack.");

    }

    @Before
    public void setupParseCommandForMethodTesting() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);

        String[] stats = {"str", "dex", "con", "int", "wis", "cha"};
        mockGame.playbooks = new HashMapIgnoreCase();
        when(mockGame.getRegisteredStatsForPlayer(anyString())).thenReturn(Arrays.asList(stats));
        when(mockGame.isStat("test", "STR")).thenReturn(true);
        when(mockGame.isStat(anyString(), anyString())).thenReturn(false);
        StatWrapper stat = new StatWrapper("str", 1, false, "dis");
        when(mockGame.getStat(anyString(), anyString())).thenReturn(stat);
        MoveWrapper move = builder.getMove();
        when(mockGame.getMove(anyString(), anyString())).thenReturn(move);

        when(mockGame.isMove(anyString(), anyString())).thenAnswer(invocation ->
                "hackandslash".contains(invocation.getArgument(1, String.class).toLowerCase()));

        mockGame.settings = new Properties();
        mockGame.settings.put("default_system_dice", "2d6");
        mockGame.settings.put("stat_debility_tag", "dis");

        tester = new ParsedCommand(mockGame, testPlayer, null);
    }

    @Test
    public void testIsDieNotation() {
        assertTrue(ParsedCommand.isDieNotation("1d2"));
        assertTrue(ParsedCommand.isDieNotation("2d9"));
        assertTrue(ParsedCommand.isDieNotation("10d6"));
        assertTrue(ParsedCommand.isDieNotation("100d12"));

        assertFalse(ParsedCommand.isDieNotation("d6"));
        assertFalse(ParsedCommand.isDieNotation("ad12"));
        assertFalse(ParsedCommand.isDieNotation("adv"));
        assertFalse(ParsedCommand.isDieNotation("120ad12"));
        assertFalse(ParsedCommand.isDieNotation("120"));
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
    public void testMultiDice() throws Exception {
        tester.splitAndParseCommand("roll 1d6  1d8 +2 ");
        String results = tester.getRollResults(true);
        logger.info(results);
        assertEquals(2, tester.mod);
        assertEquals(1, tester.dice.get(0).num);
        assertEquals(6, tester.dice.get(0).size);
        assertEquals(1, tester.dice.get(1).num);
        assertEquals(8, tester.dice.get(1).size);
    }

    @Test
    public void testGetRollResult1() throws Exception {
        tester.splitAndParseCommand("roll");
        String results = tester.getRollResults(true);
        logger.info(results);
        assertTrue(results.contains("2d6"));
    }

    @Test
    public void testGetRollResult2() throws Exception {
        tester.splitAndParseCommand("roll adv");
        String results = tester.getRollResults(true);
        logger.info(results);
        assertTrue(results.contains("3d6") && results.contains("~~"));
    }

    @Test
    public void testGetRollResult3() throws Exception {
        tester.splitAndParseCommand("roll dis");
        String results = tester.getRollResults(true);
        logger.info(results);
        assertTrue(results.contains("3d6") && results.contains("~~"));
    }

    @Test
    public void testGetRollResult4() throws Exception {
        tester.splitAndParseCommand("roll 2d6+4");
        String results = tester.getRollResults(true);
        logger.info(results);
        assertTrue(results.contains("2d6 +4"));

    }

    @Test
    public void testGetRollResult5() throws Exception {
        tester.splitAndParseCommand("roll 2d6 adv +1d4");
        String results = tester.getRollResults(true);
        assertTrue(results.contains("3d6"));
        assertTrue(results.contains("1d4"));
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
        tester.splitAndParseCommand("hack");
        assertEquals(builder.get(0), tester.move.name);
    }

    @Test
    public void testGetMoveArrayPosition() throws KeyConflictException {
        String[] test1 = {"hack"};
        String[] test2 = {"hack", "adv"};
        String[] test3 = {"hack", "and", "slash"};
        String[] test4 = {"hack", "and", "slash", "+2"};
        assertEquals(0, tester.getMoveArrayPositions(testPlayer, 0, Arrays.asList(test1)));
        assertEquals(0, tester.getMoveArrayPositions(testPlayer, 0, Arrays.asList(test2)));
        assertEquals(2, tester.getMoveArrayPositions(testPlayer, 0, Arrays.asList(test3)));
        assertEquals(2, tester.getMoveArrayPositions(testPlayer, 0, Arrays.asList(test4)));

        String[] test11 = {"bob", "hack"};
        String[] test21 = {"bob", "hack", "adv"};
        String[] test31 = {"bob", "hack", "and", "slash"};
        String[] test41 = {"bob", "hack", "and", "slash", "+2"};
        assertEquals(0, tester.getMoveArrayPositions(testPlayer, 1, Arrays.asList(test11)));
        assertEquals(0, tester.getMoveArrayPositions(testPlayer, 1, Arrays.asList(test21)));
        assertEquals(2, tester.getMoveArrayPositions(testPlayer, 1, Arrays.asList(test31)));
        assertEquals(2, tester.getMoveArrayPositions(testPlayer, 1, Arrays.asList(test41)));

    }

    @Test
    public void splitAndParseCommand4Plus() throws Exception {
        tester.splitAndParseCommand("hack and slash");
        assertEquals("Hack and Slash", tester.move.name);
    }

    @Test
    public void splitAndParseCommand5() throws Exception {
        tester = new ParsedCommand(mockGame, testPlayer, null);

        MoveWrapper move = mockGame.getMove(testPlayer, "Hack and Slash");
        assertEquals("Hack and Slash", move.name);

        tester.splitAndParseCommand("roll hack +10");
        assertEquals("Hack and Slash", tester.move.name);

        String results = tester.getRollResults(true);
        logger.info(results);
        assertEquals(1, tester.dice.size());
        assertEquals(2, tester.dice.get(0).num);
        assertEquals(6, tester.dice.get(0).size);
        assertEquals("str", tester.stat);
        assertTrue(results.contains("2d6"));
        assertTrue(results.contains("str"));
    }

    @Test
    public void testSetDefaultDice() throws IOException, DiscordBotException, KeyConflictException {
        tester.move = new MoveWrapper("Deal Damage", "Test deal damage text");
        Playbook book = new Playbook(null);
        book.player =
        book.moveOverrideDice.put("Deal Damage", "1d8");
        mockGame.playbooks.put(testPlayer, book);
        tester.setDefaultDice();
        assertEquals(1, tester.dice.get(0).num);
        assertEquals(8, tester.dice.get(0).size);
    }

    @Test
    public void testResultWithDealDamageOverrideDice() throws IOException, DiscordBotException {
        tester.move = new MoveWrapper("Deal Damage", "Test deal damage text");
        Playbook book = new Playbook(null);
        book.moveOverrideDice.put("Deal Damage", "1d8");
        mockGame.playbooks.put(testPlayer, book);
        String result = tester.getRollResults(false);
        logger.info(result);
        assertTrue(result.contains( "1d8"));
    }

    @Test
    public void testRollWithNoSheet() throws KeyConflictException, DiscordBotException, IOException {
        tester = new ParsedCommand(mockGame, "no_sheet", "roll");
    }

    @Test
    public void testHandleResourceRequest() throws IOException, PlayerNotFoundException {
        when(mockGame.modifyResource(testPlayer, "hp", 1))
                .thenReturn(new SetResourceResult("hp", 10, 1, 11));
        tester.mod = 1;
        tester.resource = "hp";

        String result = tester.handleResourceRequest();
        assertTrue(result.contains("11"));
    }

    @Test
    public void testHandleResourceRequestSubtract() throws IOException, PlayerNotFoundException {
        when(mockGame.modifyResource(testPlayer, "hp", -1))
                .thenReturn(new SetResourceResult("hp", 10, -1, 9));
        tester.mod = -1;
        tester.resource = "hp";
        String result = tester.handleResourceRequest();
        logger.info(result);
        assertTrue(result.contains("9"));
    }

    @Test
    public void testParseResourceRequest() throws IOException, DiscordBotException, KeyConflictException {
        when(mockGame.isResource(testPlayer, "hp")).thenReturn(true);
        when(mockGame.modifyResource(testPlayer, "hp", 1))
                .thenReturn(new SetResourceResult("hp", 10, 1, 11));
        tester.splitAndParseCommand("hp +1");
        assertEquals(tester.resource, "hp");
        assertEquals(tester.mod, 1);
    }

    @Test
    public void testParseStatDebilityTag() throws IOException, DiscordBotException, KeyConflictException {
        StatWrapper stat = new StatWrapper("str", 2, true, "dis");
        when(mockGame.getStat(testPlayer, "str")).thenReturn(stat);
        when(mockGame.isStat(testPlayer, "str")).thenReturn(true);
        tester.splitAndParseCommand("roll str");
        assertTrue(tester.dice.get(0).dis);
    }


}