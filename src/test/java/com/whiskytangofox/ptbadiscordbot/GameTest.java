package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class GameTest {

    static Game game;
    public static final Logger logger = LoggerFactory.getLogger(GameTest.class);


    @BeforeClass
    public static void setupGame() throws Exception {
        logger.info("Running @BeforeClass Setup");
        game = new Game(null, "1RR95L3cZUSJczoyHuevsZ8KFPNfOcNVQKcUKLJlvz5I");
        App.googleSheetAPI = new GoogleSheetAPI();
    }

    @Test
    public void testOnMessageReceived() {
    }

    @Test
    public void testLoadProperties() throws IOException {
        game.loadProperties();
        assertEquals("M2", game.sheet_definitions.getProperty("discord_player_name"));
    }

    @Test
    public void testLoadPropertiesStats() throws IOException {
        game.loadProperties();
        List<String> stats = game.getAllStats();
        assertEquals(stats.size(), 6);
        assertTrue(stats.contains("str"));
        assertTrue(stats.contains("dex"));
        assertTrue(stats.contains("con"));
        assertTrue(stats.contains("int"));
        assertTrue(stats.contains("wis"));
        assertTrue(stats.contains("cha"));
    }

    @Test
    public void testStorePlayerTab() throws IOException {
        game.loadProperties();
        game.storePlayerTab();
        assertNotNull(game.storedPlayerTab);
    }

    @Test
    public void testLoadDiscordNames() throws Exception {
        game.loadProperties();
        game.storePlayerTab();
        game.loadDiscordNamesFromStoredPlayerTab();
        assertTrue(game.isPlayerHasSheet("test1"));
        assertTrue(game.isPlayerHasSheet("test2"));
    }

    @Test
    public void testLoadBasicMoves() throws IOException {
        game.loadBasicMoves("Basic Moves!B2:AJ34,Violence & Recovery Moves!A1:BC27");
        assertTrue(game.basicMoves.containsKey("Aid"));
        assertTrue(game.basicMoves.containsKey("Defy Danger"));
    }

    @Test
    public void testLoadPlaybookMoves() throws Exception {
        game.loadProperties();
        game.storePlayerTab();
        game.loadDiscordNamesFromStoredPlayerTab();
        game.loadBasicMoves("Basic Moves!B2:AJ34,Violence & Recovery Moves!A1:BC27");
        game.loadPlaybookMovesForPlayer("test", 0);
        assertTrue(game.playbookMovesPlayerMap.containsKey("test"));
        PatriciaTrieIgnoreCase<MoveWrapper> testMoves = game.playbookMovesPlayerMap.get("test");
        assertTrue(testMoves.containsKey("Armored"));
        assertTrue(testMoves.containsKey("Last Breath"));
    }

    @Test
    public void testGetMove() throws Exception {
        game.loadProperties();
        game.storePlayerTab();
        game.loadDiscordNamesFromStoredPlayerTab();
        game.loadBasicMoves("Basic Moves!B2:AJ34,Violence & Recovery Moves!A1:BC27");
        assertNotNull(game.getMove("test", "Aid"));

        game.loadPlaybookMovesForPlayer("test", 0);
        assertNotNull(game.getMove("test", "Aid"));
        assertNotNull(game.getMove("test", "Armored"));
        assertTrue(game.getMove("test", "Last Breath").text.contains("Hard to Kill"));
        assertTrue(game.getMove("test", "Parley").text.contains("Intimidating"));
        assertTrue(game.getMove("test", "Defy Danger").text.contains("Intimidating"));

    }

    @Test
    public void testGetStat() throws Exception {
        game.loadProperties();
        game.storePlayerTab();
        game.loadDiscordNamesFromStoredPlayerTab();
        for (Map.Entry<Object, Object> prop : game.sheet_definitions.entrySet()){
            String name = prop.getKey().toString();
            if (name.startsWith("stat_") && !name.contains("penalty")){
                name = name.replace("stat_", "");
                int stat = game.getStat("test1", name);
                logger.info(name + "=" + stat);

            }
        }
    }

    @Test
    public void testGetStatWithNoRegisteredSheet() throws IOException {
        game.loadProperties();
        game.storePlayerTab();
        game.loadDiscordNamesFromStoredPlayerTab();
        Exception ex = null;
        try {
            int stat = game.getStat("NotARealAuthor", "str");
        } catch (Exception e){
            ex = e;
        }
        assertTrue(ex instanceof PlayerNotFoundException);
    }

}