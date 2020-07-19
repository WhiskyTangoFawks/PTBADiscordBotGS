package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Properties;


public class App extends ListenerAdapter {

    public static final Logger logger = LoggerFactory.getLogger(App.class);
    public final static Properties config = new Properties();

    public static GoogleSheetAPI googleSheetAPI;

    static JDA jda;

    static HashMap<MessageChannel, Game> registeredGames = new HashMap<MessageChannel, Game>();

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        googleSheetAPI = new GoogleSheetAPI();
        loadConfig();

        try {
            jda = new JDABuilder(config.getProperty("token"))
                    .addEventListeners(new App())
                    .build();
            jda.getPresence().setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "type !info for help"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() throws IOException {
        try {
            config.load(new FileInputStream("properties/config.properties"));
        } catch (FileNotFoundException e) {
            File dir = new File("properties");
            if (!dir.exists()) {
                dir.mkdir();
            }
            config.store(new FileOutputStream("properties/config.properties"), null);
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
        //TODO- swap this over to accept just the google sheet URL

        if (msg.contains("docs.google.com/spreadsheets")) {
            registerGame(event.getGuild(), event.getChannel(), msg);
        }

        if (registeredGames.containsKey(event.getChannel())) {
            registeredGames.get(event.getChannel()).OnMessageReceived(event);
        }

    }

    public boolean registerGame(Guild guild, MessageChannel channel, String msg) {
        try {
            channel.sendMessage("Sheet Link Detected, attempting to register game").queue();
            String sheetID = getSheetID(msg);
            Game game = new Game(guild, channel, sheetID, msg.contains("debug"));
            channel.sendMessage("Game registered").queue();
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
