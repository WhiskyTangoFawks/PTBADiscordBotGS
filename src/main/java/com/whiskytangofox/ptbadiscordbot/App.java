package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;


public class App extends ListenerAdapter {

    public static final Logger logger = LoggerFactory.getLogger(App.class);
    public static GoogleSheetAPI googleSheetAPI;
    static JDA jda;
    static HashMap<MessageChannel, Game> registeredGames = new HashMap<MessageChannel, Game>();

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        googleSheetAPI = new GoogleSheetAPI();
        String token = args[0];
        if (token == null){
            throw new IllegalArgumentException("Missing discord bot token");
        }
        try {
            jda = new JDABuilder(token)
                    .addEventListeners(new App())
                    .build();
            //TODO - fix custom status
            //jda.getPresence().setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "type !info for help"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onReady(ReadyEvent Event) {
        System.out.println("The Bot Is Ready");
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String msg = event.getMessage().getContentDisplay();

        if (registeredGames.containsKey(event.getChannel())) {
            registeredGames.get(event.getChannel()).OnMessageReceived(event);
        } else if (msg.startsWith("!")) {
            event.getChannel().sendMessage("No game registered for this channel, please post your google sheet URL in the chat if you want to use this bot");
        }

        if (msg.contains("docs.google.com/spreadsheets")) {
            registerGame(event.getGuild(), event.getChannel(), msg);
        }
    }

    public boolean registerGame(Guild guild, MessageChannel channel, String msg) {
        try {
            channel.sendMessage("Sheet Link Detected, attempting to register game").queue();
            String sheetID = getSheetID(msg);
            Game game = new Game(guild, channel, sheetID, msg.contains("debug"));
            channel.sendMessage("Game registered.").queue();
            registeredGames.put(channel, game);
            return true;
        } catch (Throwable e) {
            String response = switch (e.getMessage().substring(0, 3)) {
                case "403" -> "403 Forbidden: The sheet does not have the appropriate share permissions";
                case "404" -> "404 Not Found: SheetID does not have a google sheet";
                default -> "Unexpected error: " + e.getMessage();
            };
            logger.info(response);
            e.printStackTrace();
            channel.sendMessage(response);
            return false;
        }
    }

    public static String getSheetID(String msg) {
        try {
            int start = msg.indexOf("/d/")+3;
            int end = msg.lastIndexOf("/");
            String sheetID = msg.substring(start, end);
            return sheetID;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to extract sheet ID");
        }
    }

    //TODO - add a REST service which allows commands to be received

}
