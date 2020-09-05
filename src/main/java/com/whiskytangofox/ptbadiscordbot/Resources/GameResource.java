package com.whiskytangofox.ptbadiscordbot.Resources;

import com.whiskytangofox.ptbadiscordbot.App;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.GameGoogle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/game")
public class GameResource {

    public static final Logger logger = LoggerFactory.getLogger(GameResource.class);
    public static final String GAME_NOT_FOUND = "Exception: This google sheet has not been registered with the Discord Bot";
    public static final String PLAYER_NOT_FOUND = "Exception: Your google account is not linked to a sheet, or the game has not been reloaded since you linked your sheet";
    public static final String GAME_RELOADED = "Google Sheet Reloaded";

    @Path("{sheetID}/user/{user}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response playbook(@PathParam("sheetID") String sheetID, @PathParam("user") String discordName) {
        try{

        GameGoogle game =App.getGameForSheet(sheetID);
        Playbook book;
        if (game == null){
            return Response.ok(GAME_NOT_FOUND).build();
        }
        try {
            book = game.playbooks.getPlaybook(discordName);
        } catch (PlayerNotFoundException e) {
            return Response.ok(PLAYER_NOT_FOUND).build();
        }

        GameUser data = new GameUser().mapPlaybookValues(book);
        return Response.ok(data).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.serverError().entity(e).build();
        }
    }

    @Path("{sheetID}/user/{user}/commandform")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response commandForm(@PathParam("sheetID") String sheetID, @PathParam("user") String discordName,
                                @FormParam("move") String move, @FormParam("stat") String stat, @FormParam("mod") String mod,
                                @FormParam("advdis") String advdis) throws DiscordBotException, IOException, KeyConflictException {

        try{
        GameGoogle game =App.getGameForSheet(sheetID);
        if (game == null){
            return Response.ok(GAME_NOT_FOUND).build();
        }
        try {
            game.playbooks.getPlaybook(discordName);
        } catch (PlayerNotFoundException e) {
            return Response.ok(PLAYER_NOT_FOUND).build();
        }

        String response = game.runCommand(discordName, "roll " + move + " " + stat + " " + mod + " " + advdis);
        return Response.ok(response).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.serverError().entity(e).build();
        }
    }

    @Path("{sheetID}/reload")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response reload(@PathParam("sheetID") String sheetID) {
        try {
            GameGoogle game = App.getGameForSheet(sheetID);
            if (game == null) {
                return Response.ok(GAME_NOT_FOUND).build();
            }
            App.registerGame(game.guild, game.channel, sheetID, false);
            return Response.ok(GAME_RELOADED).build();
        } catch (Exception e){
            e.printStackTrace();
            return Response.serverError().entity(e).build();
        }
    }


}
