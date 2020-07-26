package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.GameSheetService;
import com.whiskytangofox.ptbadiscordbot.Services.PlaybookService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReader;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Properties;

import static com.whiskytangofox.ptbadiscordbot.App.googleSheetAPI;

public class Game extends ChannelInstance {

    public Properties settings = new Properties();
    public PatriciaTrieIgnoreCase<Move> basicMoves = new PatriciaTrieIgnoreCase<>();
    public PlaybookService playbooks;
    public final SheetReader reader;
    public final GameSheetService sheet;

    public Game(Guild guild, MessageChannel channel, String sheetID, Boolean debug) throws IOException {
        super(guild, channel, debug);
        sheet = new GameSheetService(sheetID, googleSheetAPI, settings);
        this.reader = new SheetReader(this);
        this.playbooks = new PlaybookService(sheet);
        if (sheetID != null) try { //if sheetID == null, then we are running tests
            readSheet();
        } catch (Exception e) {
            sendGameMessage("Unexpected exception while trying to read the game sheet");
            throw e;
        }
    }

    public void readSheet() throws IOException {
        ArrayList<RangeWrapper> sheetData = sheet.getSheet();
        sendDebugMsg("Google sheet retrieved");
        for (RangeWrapper tab : sheetData) {
            if (tab.tab.equalsIgnoreCase("properties")) {
                readPropertiesTab(tab);
            } else {
                reader.parseSheet(tab);
            }
            sendDebugMsg("Parsed sheet: " + tab.tab);
        }
        //Post-load stuff goes here
        try {
            sendDebugMsg("Sheets loaded, finalizing");
            copyAndStoreModifiedBasicMoves();
        } catch (Exception e){
            sendGameMessage("Unexpected exception while finalizing load: " + e.toString());
        }
    }

    public void readPropertiesTab(RangeWrapper tab){
        //TODO - refactor move this to sheet reader, have it return a collection instead of a setting the value
        try {
            tab.getValueSet().stream()
                    .filter(Objects::nonNull)
                    .filter(prop -> prop.contains("="))
                    .forEach(prop -> settings.put(prop.split("=")[0], prop.split("=")[1]));
            sendDebugMsg("Properties loaded");
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to load properties");
            throw e;
        }
    }

    public void copyAndStoreModifiedBasicMoves() {
        for (Playbook book : playbooks.playbooks.values()) {
            HashMap<String, Move> buffer = new HashMap<>();
            for (Move advanced : book.moves.values()) {
                for (String basicName : advanced.getModifiesMoves()) {
                    try {
                        Move basic = buffer.get(basicName);
                        if (basic == null) {
                            basic = getMove(book.player, basicName);
                        }
                        if (basic != null) {
                            Move copy = basic.getModifiedCopy(advanced);
                            buffer.put(copy.name, copy);
                        } else {
                            sendGameMessage("Unable to find: "+ basicName + " while loading "+ advanced.name);
                        }
                    } catch (Exception e){
                        sendGameMessage("Exception occurred while trying to load modified basic move: " + advanced.name);
                        e.printStackTrace();
                    }
                }
            }
            book.moves.putAll(buffer);
        }
    }

    @Override
    public void OnMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        String player = event.getAuthor().getName();
        try {
            if (msg.startsWith(settings.getProperty("commandchar"))) {// /alias string
                msg = msg.toLowerCase().replace(settings.getProperty("commandchar"), "");
                if (msg.replace(" ", "").equalsIgnoreCase("reloadgame")){
                    reloadGame();
                    sendGameMessage("Game successfully reloaded");
                } else {
                    ParsedCommand command = new ParsedCommand(this, player, msg);
                    String response = "";
                    if (command.move != null) {
                        response = response + command.move.text + System.lineSeparator();
                    }
                    if (command.resultText != null) {
                        response = response + event.getAuthor().getAsMention() + " " + command.resultText;
                    }
                    sendGameMessage(response);
                }
            }

        } catch (Throwable e) {
            sendGameMessage("Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void reloadGame() throws IOException {
        readSheet();
    }

    public boolean isMove(String author, String string) throws KeyConflictException {
        return getMove(author, string) != null;
    }

    public Move getMove(String player, String move) throws KeyConflictException {
        if (playbooks.isPlaybookMove(player, move)) {
            return playbooks.getMove(player, move);
        }
        return basicMoves.getClosestMatch(move);
    }


}
