package com.whiskytangofox.ptbadiscordbot;


import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
            jda.getPresence().setActivity(Activity.playing("Dungeon World"));
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
        if (msg.contains("register new game") && msg.contains("/")) {
            registerGame(event.getChannel(), msg);
        }

        if (registeredGames.containsKey(event.getChannel())) {
            registeredGames.get(event.getChannel()).OnMessageReceived(event);
        }

    }

    public boolean registerGame(MessageChannel channel, String msg) {
        try {
            channel.sendMessage("Registration request received, attempting to create game").queue();
            String sheetID = getSheetID(msg);
            Game game = new Game(channel, sheetID);
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

    public String getSheetID(String msg) {
        try {
            String sheetID = msg.substring(msg.indexOf("/") + 1, msg.lastIndexOf("/"));
            return sheetID;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to extract sheet ID");
        }
    }


}
