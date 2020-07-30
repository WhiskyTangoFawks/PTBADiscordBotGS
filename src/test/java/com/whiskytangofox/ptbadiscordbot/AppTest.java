package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppTest {

    public static final Logger logger = LoggerFactory.getLogger(AppTest.class);
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
        App.googleSheetAPI = new GoogleSheetAPI();
    }

    @Before
    public void setupMocks(){
        MockitoAnnotations.initMocks(this);
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
    }

    //@Test TODO
    public void testRegisterGame() {
        app.registerGame(null, mockChannel, "https://docs.google.com/spreadsheets/d/1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/edit#gid=899958736");
        assertTrue(App.registeredGames.containsKey(mockChannel));
    }

    //@Test TODO
    public void testRegisterGameBadLink() {
        app.registerGame(null, mockChannel, "register new game https://docs.google.com/spreadsheets/d/1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYq/edit#899958736");
        //assertTrue(app.registeredGames.containsKey(mockChannel));
        verify(mockChannel).sendMessage("404 Not Found: SheetID does not have a google sheet");
    }

    //@Test TODO
    public void testNoChannelMessagePermissionForBot(){
        //todo
    }

    //@Test TODO
    public void testNoSharePermissionsForGoogleAPI(){
        app.registerGame(null, mockChannel, "https://docs.google.com/spreadsheets/d/1EA68XZczUOl6lntgl1MklN27O9NHmzLsZejcMTGgl2A/edit#gid=899958736");
        verify(mockChannel).sendMessage("403 Forbidden: The sheet does not have the appropriate share permissions");
    }

    //@Test TODO
    public void testGetSheetID(){
       String msg = "https://docs.google.com/spreadsheets/d/1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY/edit#gid=0";
       String id = App.getSheetID(msg);
       assertEquals("1JOmnd9jvw4CV24f7zMvfI1otPNNT-UvKExzCYqHPgaY", id);
    }

}