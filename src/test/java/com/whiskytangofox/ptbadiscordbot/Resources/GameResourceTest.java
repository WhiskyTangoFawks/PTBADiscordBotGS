package com.whiskytangofox.ptbadiscordbot.Resources;


import com.whiskytangofox.ptbadiscordbot.App;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.GameGoogle;
import com.whiskytangofox.ptbadiscordbot.Services.PlaybookService;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
public class GameResourceTest {

    final String sheetID = "1rRKQgMI8Q_0Xc-HohwGyH01hhViHdEGD-kebhwVxZN0";
    final String author = "player1";

    @Mock
    GameGoogle mockGame;

    @Mock
    PlaybookService mockPlaybookService;

    @Mock
    Playbook mockBook;

    @BeforeEach
    public void beforeAll(){
        MockitoAnnotations.initMocks(this);
        mockGame.playbooks = mockPlaybookService;
    }

   @Test
    public void testGameResourceCommand() throws DiscordBotException, IOException, KeyConflictException {
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        App.registeredGameSheets.put(sheetID, mockGame);
        when(mockGame.runCommand(any(), any())).thenReturn("Test command run");
        String command = "command";

        GameResource underTest = new GameResource();
        assertEquals(200, underTest.command(sheetID, author, command).getStatus());
    }

    @Test
    public void testGameResourceCommand_NoSheet404() throws DiscordBotException, IOException, KeyConflictException {
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        //App.registeredGameSheets.put(sheetID, mockGame);
        when(mockGame.runCommand(any(), any())).thenReturn("Test command run");
        String command = "command";

        GameResource underTest = new GameResource();
        assertEquals(204, underTest.command(sheetID, author, command).getStatus());
    }

    @Test
    public void testGameResourceCommand_NoPlayer403() throws DiscordBotException, IOException, KeyConflictException {
        when(mockPlaybookService.getPlaybook(any())).thenThrow(new PlayerNotFoundException("Test player not found"));
        App.registeredGameSheets.put(sheetID, mockGame);
        when(mockGame.runCommand(any(), any())).thenReturn("Test command run");
        String command = "command";

        GameResource underTest = new GameResource();
        assertEquals(204, underTest.command(sheetID, author, command).getStatus());
    }

    @Test
    public void testGameResourceCommand_REST() throws DiscordBotException, IOException, KeyConflictException {
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        App.registeredGameSheets.put(sheetID, mockGame);
        when(mockGame.runCommand(any(), any())).thenReturn("Test command run");
        String command = "command";

        String path = "game/"+sheetID+"/"+author+"/"+command;
        given()
            .when()
                .get(path)
            .then()
                .statusCode(200);
    }

}