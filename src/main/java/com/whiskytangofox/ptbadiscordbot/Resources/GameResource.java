package com.whiskytangofox.ptbadiscordbot.Resources;

import com.whiskytangofox.ptbadiscordbot.App;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

@Path("/game")
public class GameResource {

    public static final Logger logger = LoggerFactory.getLogger(GameResource.class);

    @Path("{sheetID}/{discordName}/{commandString}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response command(@PathParam("sheetID") String sheetID, @PathParam("discordName") String author, @PathParam("commandString") String commandString) {

        if (App.getGameForSheet(sheetID) == null){
            logger.info("Sheet not found: " +sheetID);
            return Response.status(204).entity("Sheet not found: " +sheetID).build();
        }
        try { //check if player is registered to a sheet
            App.getGameForSheet(sheetID).playbooks.getPlaybook(author);
        } catch (PlayerNotFoundException e) {
            logger.info("player not found: " +author);
            return Response.status(204).entity("player not found: " +author).build();
        }

        
        try {
            App.getGameForSheet(sheetID).runCommand(author, commandString);
        } catch (DiscordBotException e) {
            e.printStackTrace();
            return Response.serverError().build();
        } catch (KeyConflictException e) {
            e.printStackTrace();
            return Response.serverError().build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }

        return Response.ok().build();
    }

}
