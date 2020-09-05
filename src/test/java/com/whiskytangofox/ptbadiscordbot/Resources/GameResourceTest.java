package com.whiskytangofox.ptbadiscordbot.Resources;


import com.whiskytangofox.ptbadiscordbot.App;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.HashMapIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.GameGoogle;
import com.whiskytangofox.ptbadiscordbot.Services.PlaybookService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
public class GameResourceTest {

    private static final Logger logger = LoggerFactory.getLogger(GameResourceTest.class);
    private static final Jsonb jsonb = JsonbBuilder.create();

    private static final String sheetID = "test";
    private static final String author = "player1";
    private static final String PATH_COMMANDFORM = "game/"+sheetID+"/user/"+author+"/commandform";
    private static final String PATH_USER = "game/"+sheetID+"/user/"+author;
    private static final String PATH_RELOAD = "game/"+sheetID+"/reload";

    @Mock
    GameGoogle mockGame;

    @Mock
    PlaybookService mockPlaybookService;

    @Mock
    Playbook mockBook;

    @Mock
    MessageChannel mockChannel;

    @Mock
    Guild mockGuild;

    @Mock
    static MessageAction mockMessageAction;

    @BeforeEach
    public void beforeAll(){
        MockitoAnnotations.initMocks(this);
        mockGame.playbooks = mockPlaybookService;
        mockGame.channel = mockChannel;
        when(mockChannel.sendMessage(anyString())).thenReturn(mockMessageAction);
        mockGame.guild = mockGuild;

        mockBook.basicMoves = new PatriciaTrieIgnoreCase<>();
        mockBook.basicMoves.put("basicmove", new Move("Basic move", "test"));

        mockBook.moves = new PatriciaTrieIgnoreCase<>();
        mockBook.moves.put("playbookmove", new Move("Playbook Move", "test"));

        mockBook.stats = new HashMapIgnoreCase<>();
        mockBook.stats.put("STR", null);

        App.registeredGameSheets.clear();
    }

    public Response getCommandFormResponse(String move, String stat, String mod, String advdis){
        Response response = given()
                .header("Accept", "application/json")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .formParam("move", move)
                .formParam("stat", stat)
                .formParam("mod", mod)
                .formParam("advdis", advdis)
                .config(RestAssured.config().encoderConfig(encoderConfig().encodeContentTypeAs("multipart/form-data", ContentType.URLENC)))
                .when()
                .post(PATH_COMMANDFORM)
                .then()
                .extract().response();
        logger.info(response.prettyPrint());
        return response;
    }


    @Test
    public void testGameResourceCommand_REST() throws DiscordBotException, IOException, KeyConflictException {
        String commandResult = "testing: runCommand was called successfully";
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        when(mockGame.runCommand(any(), any())).thenReturn(commandResult);
        App.registeredGameSheets.put(sheetID, mockGame);
        Response response = getCommandFormResponse("test move", "test stat", "0", "adv");
        assertEquals(200, response.getStatusCode());
        assertEquals(commandResult, response.getBody().print());
    }

    @Test
    public void testGameResourceCommand_REST_NoSheetFound() throws DiscordBotException, IOException, KeyConflictException {
        String commandResult = "testing: runCommand was called successfully";
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        when(mockGame.runCommand(any(), any())).thenReturn(commandResult);
        //App.registeredGameSheets.put(sheetID, mockGame); //SHEET NOT REGISTERED
        Response response = getCommandFormResponse("test move", "test stat", "0", "adv");
        assertEquals(200, response.getStatusCode());
        assertEquals(GameResource.GAME_NOT_FOUND, response.getBody().print());
    }

    @Test
    public void testGameResourceCommand_REST_NoPlayerFound() throws DiscordBotException, IOException, KeyConflictException {
        String commandResult = "testing: runCommand was called successfully";
        when(mockPlaybookService.getPlaybook(any())).thenThrow(new PlayerNotFoundException("Player not found"));
        when(mockGame.runCommand(any(), any())).thenReturn(commandResult);
        App.registeredGameSheets.put(sheetID, mockGame); //SHEET NOT REGISTERED
        Response response = getCommandFormResponse("test move", "test stat", "0", "adv");
        assertEquals(200, response.getStatusCode());
        assertEquals(GameResource.PLAYER_NOT_FOUND, response.getBody().print());
    }


    public Response getUserResponse(){
        Response response = given()
                .when()
                .get(PATH_USER)
                .then()
                .extract().response();
        response.prettyPrint();
        return response;
    }

    @Test
    public void testGameResourceUser() throws DiscordBotException {
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        App.registeredGameSheets.put(sheetID, mockGame);

        Response response = getUserResponse();

        assertEquals(200, response.getStatusCode());
        assertEquals(jsonb.toJson(new GameUser().mapPlaybookValues(mockBook)), response.getBody().print().replace(System.lineSeparator(), "").replace(" ", ""));
    }

    @Test
    public void testGameResourceUser_GameNotFound() throws DiscordBotException {
        when(mockPlaybookService.getPlaybook(any())).thenReturn(mockBook);
        //App.registeredGameSheets.put(sheetID, mockGame);

        Response response = getUserResponse();

        assertEquals(200, response.getStatusCode());
        assertEquals(GameResource.GAME_NOT_FOUND, response.getBody().print());
    }

    @Test
    public void testGameResourceUser_PlayerNotFound() throws DiscordBotException {
        when(mockPlaybookService.getPlaybook(any())).thenThrow(new PlayerNotFoundException("Player not found"));
        App.registeredGameSheets.put(sheetID, mockGame);

        Response response = getUserResponse();

        assertEquals(200, response.getStatusCode());
        assertEquals(GameResource.PLAYER_NOT_FOUND, response.getBody().print());
    }

    @Test
    public void TestGameUserSerialization(){
        GameUser user = new GameUser().mapPlaybookValues(mockBook);
        Jsonb jsonb = JsonbBuilder.create();
        String result = jsonb.toJson(user);
        logger.info(result);
        assertTrue(result.contains("playbookmove"));
        assertTrue(result.contains("basicmove"));
        assertTrue(result.contains("str"));
    }

    public Response getReloadResponse(){
        Response response = given()
                .when()
                .get(PATH_RELOAD)
                .then()
                .extract().response();
        response.prettyPrint();
        return response;
    }

    @Test
    public void TestReload(){
        App.registeredGameSheets.put(sheetID, mockGame);

        Response response = getReloadResponse();

        assertEquals(200, response.getStatusCode());
        assertEquals(GameResource.GAME_RELOADED, response.getBody().print());
    }

}