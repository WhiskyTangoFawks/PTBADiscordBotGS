package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.wrappers.Playbook;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.*;

import static com.whiskytangofox.ptbadiscordbot.App.logger;

public class Game {

    public Properties sheet_definitions = new Properties();

    public PatriciaTrieIgnoreCase<MoveWrapper> basicMoves = new PatriciaTrieIgnoreCase<MoveWrapper>();
    public HashMap<String, Playbook> playbooks = new HashMap<String, Playbook>();

    private final String sheetID;

    public final MessageChannel channel;
    public SheetReader reader;

    public Game(MessageChannel channel, String sheetID) throws IOException {
        this.channel = channel;
        this.sheetID = sheetID;
        this.reader = new SheetReader(this);
        if (sheetID != null) try { //if sheetID == null, then we are running tests
            loadProperties();
            readSheet();
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to register game");
            throw e;
        }
    }

    public void readSheet() throws IOException {
        for (RangeWrapper tab : App.googleSheetAPI.getSheet(sheetID)){
            reader.parseSheet(tab);
        }
    }

    public void OnMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        String player = event.getAuthor().getName().toLowerCase().replace(" ","");
        try {
            if (msg.startsWith(sheet_definitions.getProperty("commandchar"))) {// /alias string
                msg = msg.toLowerCase().replace(sheet_definitions.getProperty("commandchar"), "");
                if (msg.replace(" ", "").equalsIgnoreCase("reloadgame")){
                    reloadGame();
                    sendGameMessage("Game successfully reloaded");
                } else {
                    ParsedCommand command = new ParsedCommand(this, player, msg);
                    String response = "";
                    if (command.move != null) {
                        response = response + command.move.text + System.lineSeparator();
                    }
                    if (command.rollResult != null) {
                        response = response + event.getAuthor().getAsMention() + " " + command.rollResult;
                    }
                    sendGameMessage(response);
                }
            }

        } catch (Throwable e) {
            sendGameMessage("Exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void sendGameMessage(String msg){
        if (channel != null) {
            channel.sendMessage(msg).queue();
        } else {
            logger.info("Test result for sendGameMessage: " + msg);
        }
    }

    public void reloadGame() throws IOException {
        loadProperties();
        readSheet();
    }

    public void loadProperties() throws IOException {
        try {
            RangeWrapper range = App.googleSheetAPI.getRange(sheetID, "properties", "A1:A100");
            range.getValueSet().stream()
                    .filter(Objects::nonNull)
                    .filter(prop -> prop.contains("="))
                    .forEach(prop -> sheet_definitions.put(prop.split("=")[0], prop.split("=")[1]));
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to load properties");
            throw e;
        }
    }

    public boolean isMove(String author, String string) throws KeyConflictException {
        return getMove(author, string) != null;
    }

    public MoveWrapper getMove(String player, String key) throws KeyConflictException {
        if (playbooks.get(player) != null && playbooks.get(player).moves.getClosestMatch(key) != null) {
            return playbooks.get(player).moves.getClosestMatch(key);
        }
        return basicMoves.getClosestMatch(key);
    }

    public String getLiveCellValue(String tab, String cellRef) throws IOException {
        return App.googleSheetAPI.getCellValue(sheetID, tab, cellRef);
    }

    public int getStat(String author, String stat) throws IOException, DiscordBotException {
        //TODO- use either getBatch or getArea to make a single call instead of 2
        Playbook book = playbooks.get(author);
        CellRef cellref = book.stats.get(stat);
        String value = getLiveCellValue(book.tab, cellref.getCellRef());
        Integer intValue = null;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e){
            throw new MissingValueException("Player: " + author + ", Stat:" + stat+ ", returned Not A Number, please correct your sheet");
        }
        //TODO - add penalty property instead of using a -1
        //TODO - try parsing as both integer and boolean

        cellref = book.stat_penalties.get(stat);

        value = getLiveCellValue(book.tab, cellref.getCellRef());
        boolean isDebilitated = Boolean.parseBoolean(value);
        int penalty = isDebilitated ? 1 : 0;
        return intValue - penalty;
    }

    public boolean isStat(String player, String stat) throws PlayerNotFoundException {
        Playbook book = playbooks.get(player);
        if (book == null){
            throw new PlayerNotFoundException("Unable to find a registered playbook for: "+ player);
        }
        return book.stats.containsKey(stat.toLowerCase());
    }

    public Collection<String> getStatsForPlayer(String player) throws PlayerNotFoundException {
        Playbook book = playbooks.get(player);
        if (book == null){
            throw new PlayerNotFoundException("No playbook registered to "+ player);
        }
        return book.stats.keySet();
    }

    public void setValue(String cellRef, String value){
        //TODO
    }

    public Playbook getPlaybook(String player) throws PlayerNotFoundException {
        if (!playbooks.containsKey(player)){
            throw new PlayerNotFoundException("No playbook found registered to " + player);
        }
        return playbooks.get(player);
    }

}
