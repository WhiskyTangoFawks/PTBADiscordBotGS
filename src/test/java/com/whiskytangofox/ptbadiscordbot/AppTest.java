package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
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

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppTest {

    public static final Logger logger = LoggerFactory.getLogger(MoveLoaderTest.class);
    static App app;

    @Mock
    static MessageChannel mockChannel;

    @Mock
    static MessageAction mockMessageAction;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();



    @BeforeClass
    public static void setupGame() throws Exception {
        logger.info("Running @BeforeClass Setup");
        app = new App();
        app.googleSheetAPI = new GoogleSheetAPI();
    }

    @Before
    public void setupMocks(){
        MockitoAnnotations.initMocks(this);
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
    }

    @Test
    public void testRegisterGame() {
        app.registerGame(mockChannel, "register new game /1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/");
        assertTrue(app.registeredGames.containsKey(mockChannel));
    }

    @Test
    public void testRegisterGameBadLink() {
        app.registerGame(mockChannel, "register new game /1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqH/");
        //assertTrue(app.registeredGames.containsKey(mockChannel));
        verify(mockChannel).sendMessage("404 Not Found: SheetID does not have a google sheet");
    }

    @Test
    public void testRegisterGameBadFormat() {
        app.registerGame(mockChannel, "register new game /1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqH");
        //assertTrue(app.registeredGames.containsKey(mockChannel));
        verify(mockChannel).sendMessage("Unexpected error: Unable to extract sheet ID");

    }

    @Test
    public void testNoChannelMessagePermissionForBot(){
        //todo
    }

    @Test
    public void testNoSharePermissionsForGoogleAPI(){
        app.registerGame(mockChannel, "/1EA68XZczUOl6lntgl1MklN27O9NHmzLsZejcMTGgl2A/");
        verify(mockChannel).sendMessage("403 Forbidden: The sheet does not have the appropriate share permissions");
    }


}