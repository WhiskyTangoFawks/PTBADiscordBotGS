package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Game {

    public Properties sheet_definitions = new Properties();

    public final PatriciaTrieIgnoreCase<MoveWrapper> basicMoves = new PatriciaTrieIgnoreCase<MoveWrapper>();
    private final HashMap<String, Integer> playerOffsets = new HashMap<String, Integer>();
    public final HashMap<String, PatriciaTrieIgnoreCase<MoveWrapper>> playbookMovesPlayerMap = new HashMap<String, PatriciaTrieIgnoreCase<MoveWrapper>>();

    private final String sheetID;

    public RangeWrapper storedPlayerTab;
    private final MessageChannel channel;

    public Game(MessageChannel channel, String sheetID) throws IOException {
        this.channel = channel;
        this.sheetID = sheetID;
        if (channel != null) { //if channel == null, then we are running tests
            loadProperties();
            storePlayerTab();
            loadDiscordNamesFromStoredPlayerTab();
            loadBasicMoves(sheet_definitions.getProperty("basic_moves"));
            loadAllPlaybookMoves();
        }
    }

    public void storePlayerTab() throws IOException {
        this.storedPlayerTab = App.googleSheetAPI.getRange(sheetID, sheet_definitions.getProperty("playbook_tab"), sheet_definitions.getProperty("playbook_tab_range"));
    }

    public void OnMessageReceived(MessageReceivedEvent event) {

        //if (!isPlayerHasSheet(event.getAuthor().getName().toLowerCase())) {
        //    App.logger.warn("Command received, but no player registered for " + event.getAuthor().getName());
        //}

        String msg = event.getMessage().getContentDisplay();
        try {
            if (msg.startsWith(sheet_definitions.getProperty("commandchar"))) {// /alias string
                msg = msg.toLowerCase().replace(sheet_definitions.getProperty("commandchar"), "");
                ParsedCommand command = new ParsedCommand(this, event.getAuthor().getName(), msg);
                String response = "";
                if (command.move != null) {
                    response = response + command.move.text + System.lineSeparator();
                }
                if (command.rollResult != null) {
                    response = response + event.getAuthor().getAsMention() + " " + command.rollResult;
                }
                event.getMessage().getChannel().sendMessage(response).queue();
            }

        } catch (Throwable e) {
            event.getChannel().sendMessage("Exception: " + e.toString()).queue();
            e.printStackTrace();
        }
    }

    public void loadProperties() throws IOException {
        RangeWrapper range = App.googleSheetAPI.getRange(sheetID, "properties", "A1:A100");
        for (String prop : range.getValueSet()) {
            if (prop != null && prop.contains("=")) {
                sheet_definitions.put(prop.split("=")[0], prop.split("=")[1]);
            }
        }
    }

    public void loadDiscordNamesFromStoredPlayerTab() {
        String numSheets = sheet_definitions.getProperty("num_playbooks_to_load");
        int numSheetsToLoad = Integer.parseInt(numSheets);
        CellRef nameCell = new CellRef(sheet_definitions.getProperty("discord_player_name"));
        int sheetWidth = Integer.parseInt(sheet_definitions.getProperty("single_playbook_width"));
        for (int i = 0; i < numSheetsToLoad; i++) {
            int offset = sheetWidth * i;
            String discordPlayerName = storedPlayerTab.getColumnOffsetValue(nameCell.getCellRef(), offset);
            if (discordPlayerName != null && !discordPlayerName.isBlank() && !discordPlayerName.equalsIgnoreCase("<type player name>")) {
                playerOffsets.put(discordPlayerName.toLowerCase(), offset);
                App.logger.info("Loaded sheet for :" + discordPlayerName + ":");
            }
        }
    }

    public void loadBasicMoves(String rangeString) throws IOException {
        for (String rangeRef : rangeString.split(",")) {
            String tab = rangeRef.split("!")[0];
            String rangeToLoad = rangeRef.split("!")[1];
            RangeWrapper range = App.googleSheetAPI.getRange(sheetID, tab, rangeToLoad);
            ArrayList<MoveWrapper> moveList = MoveLoader.loadMovesFromRange(range, basicMoves);
            for (MoveWrapper move : moveList) {
                basicMoves.put(move.name, move);
            }
        }
    }

    public void loadAllPlaybookMoves() {
        for (Map.Entry<String, Integer> entry : this.playerOffsets.entrySet()) {
            String player = entry.getKey();
            int offset = entry.getValue();
            loadPlaybookMoves(player, offset);
        }
    }

    public void loadPlaybookMoves(String player, int offset){
        PatriciaTrieIgnoreCase<MoveWrapper> playerMoves = new PatriciaTrieIgnoreCase<MoveWrapper>();
        String play_book_moves_ranges = sheet_definitions.getProperty("play_book_moves_range");
        for (String range : play_book_moves_ranges.split(",")) {
            RangeWrapper rangeToLoad = new RangeWrapper(this.storedPlayerTab, range, offset);
            ArrayList<MoveWrapper> moveList = MoveLoader.loadMovesFromRange(rangeToLoad, basicMoves);
            //TODO currently a secondary Advanced move based on a playbook move won't load, because it
            //is only being fed teh basic moves, not the parsed secondary moves from the previous list
            for (MoveWrapper move : moveList) {
                playerMoves.put(move.name, move);
            }
            playbookMovesPlayerMap.put(player, playerMoves);
        }
    }

    public boolean isMove(String author, String string) {
        try {
            return getMove(author, string) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public MoveWrapper getMove(String player, String key) throws Exception {
        try {
            if (playbookMovesPlayerMap.get(player) != null && playbookMovesPlayerMap.get(player).getClosestMatch(key) != null) {
                return playbookMovesPlayerMap.get(player).getClosestMatch(key);
            }
        } catch (Exception e) {

        }
        return basicMoves.getClosestMatch(key);
    }

    public boolean isPlayerHasSheet(String player) {
        return playerOffsets.containsKey(player.toLowerCase());
    }

    public RangeWrapper getRange(String range) throws IOException {
        return App.googleSheetAPI.getRange(sheetID, sheet_definitions.getProperty("playbook_tab"), range);
    }

    public String getLiveCellValue(String cellRef) throws IOException {
        return App.googleSheetAPI.getCellValue(sheetID, sheet_definitions.getProperty("playbook_tab"), cellRef);
    }

    private String getColumnOffsetLiveValue(String cell, int columnOffset) throws IOException {
        CellRef cellref = new CellRef(cell);
        return App.googleSheetAPI.getCellValue(sheetID, sheet_definitions.getProperty("playbook_tab"), cellref.getColumnOffsetCellRef(columnOffset));
    }

    public String getLivePlayerValue(String player, String key) throws Exception {
        if (!playerOffsets.containsKey(player.toLowerCase())) {
            throw new Exception("No playbook found registered to " + player);
        }
        return getColumnOffsetLiveValue(sheet_definitions.getProperty(key), playerOffsets.get(player.toLowerCase()));
    }

    public int getStat(String author, String stat) throws Exception {
        String value = getLivePlayerValue(author, "stat_" + stat);
        boolean isDebilitated = Boolean.parseBoolean(getLivePlayerValue(author, "stat_" + stat + "_penalty"));
        int penalty = isDebilitated ? 1 : 0;
        return Integer.parseInt(value) - penalty;
    }

    public boolean isStat(String string) {
        return sheet_definitions.getProperty("stat_" + string) != null;
    }




}
