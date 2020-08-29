package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;


public class App extends ListenerAdapter {

    public static final Logger logger = LoggerFactory.getLogger(App.class);
    public static GoogleSheetAPI googleSheetAPI;
    static JDA jda;
    static HashMap<MessageChannel, GameGoogle> registeredGameChannels = new HashMap<>();
    public static HashMap<String, GameGoogle> registeredGameSheets = new HashMap<>();

    public static void mainApp(String... args) throws GeneralSecurityException, IOException {
        App.logger.info("************************************Running main method");
        googleSheetAPI = new GoogleSheetAPI();
        if (System.getenv("bot_token") == null){
            throw new NullPointerException("No Discord token supplied - please set the discord_bot environmental variable");
        }
        startJDA();
    }


    public static void startJDA() throws IOException, GeneralSecurityException {
        try {
            jda = new JDABuilder(System.getenv("bot_token"))
                    .addEventListeners(new App())
                    .build();
            //TODO - fix custom status
            //jda.getPresence().setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, "type !info for help"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onReady(@NotNull ReadyEvent Event) {
        System.out.println("The Bot Is Ready");
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }
        String msg = event.getMessage().getContentDisplay();

        if (registeredGameChannels.containsKey(event.getChannel())) {
            registeredGameChannels.get(event.getChannel()).OnMessageReceived(event);
        } else if (msg.startsWith("!")) {
            event.getChannel().sendMessage("No game registered for this channel, please post your google sheet URL in the chat if you want to use this bot").queue();
        }

        if (msg.contains("docs.google.com/spreadsheets")) {
            registerGame(event.getGuild(), event.getChannel(), msg);
        }
    }

    public boolean registerGame(Guild guild, MessageChannel channel, String msg) {
        try {
            channel.sendMessage("Sheet Link Detected, attempting to register game").queue();
            String sheetID = getSheetID(msg);
            GameGoogle game = new GameGoogle(guild, channel, sheetID, msg.contains("debug"));
            channel.sendMessage("Game registered.").queue();
            registeredGameChannels.put(channel, game);
            registeredGameSheets.put(sheetID, game);
            logger.info("Registered game for: " + sheetID);
            return true;
        } catch (Throwable e) {
            String response = switch (e.getMessage().substring(0, 3)) {
                case "403" -> "403 Forbidden: The sheet does not have the appropriate share permissions";
                case "404" -> "404 Not Found: SheetID does not have a google sheet";
                default -> "Unexpected error: " + e.getMessage();
            };
            logger.info(response);
            e.printStackTrace();
            channel.sendMessage(response).queue();
            return false;
        }
    }

    public static String getSheetID(String msg) {
        try {
            int start = msg.indexOf("/d/")+3;
            int end = msg.lastIndexOf("/");
            return msg.substring(start, end);

        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to extract sheet ID");
        }
    }

    public static GameGoogle getGameForSheet(String sheetID){
        return registeredGameSheets.get(sheetID);
    }


}
