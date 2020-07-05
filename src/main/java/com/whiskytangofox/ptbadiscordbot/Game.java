package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.whiskytangofox.ptbadiscordbot.App.logger;

public class Game {

    public Properties sheet_definitions = new Properties();

    public final PatriciaTrieIgnoreCase<MoveWrapper> basicMoves = new PatriciaTrieIgnoreCase<MoveWrapper>();
    private final HashMap<String, Integer> playerOffsets = new HashMap<String, Integer>();
    public final HashMap<String, PatriciaTrieIgnoreCase<MoveWrapper>> playbookMovesPlayerMap = new HashMap<String, PatriciaTrieIgnoreCase<MoveWrapper>>();

    private final String sheetID;

    public RangeWrapper storedPlayerTab;
    private final MessageChannel channel;

    public Game(MessageChannel channel, String sheetID) throws IOException, KeyConflictException {
        this.channel = channel;
        this.sheetID = sheetID;
        if (channel != null) try { //if channel == null, then we are running tests
            loadProperties();
            storePlayerTab();
            loadDiscordNamesFromStoredPlayerTab();
            loadBasicMoves(sheet_definitions.getProperty("basic_moves"));
            loadAllPlaybookMoves();
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to register game");
            throw e;
        }
    }

    public void storePlayerTab() throws IOException {
        this.storedPlayerTab = App.googleSheetAPI.getRange(sheetID, sheet_definitions.getProperty("playbook_tab"), sheet_definitions.getProperty("playbook_tab_range"));
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
        channel.sendMessage(msg).queue();
    }

    public void reloadGame() throws IOException, KeyConflictException {
        loadProperties();
        storePlayerTab();
        loadDiscordNamesFromStoredPlayerTab();
        loadBasicMoves(sheet_definitions.getProperty("basic_moves"));
        loadAllPlaybookMoves();
    }

    public void loadProperties() throws IOException {
        try {
            RangeWrapper range = App.googleSheetAPI.getRange(sheetID, "properties", "A1:A100");
            range.getValueSet().stream()
                    .filter(prop -> prop != null)
                    .filter(prop -> prop.contains("="))
                    .forEach(prop -> sheet_definitions.put(prop.split("=")[0], prop.split("=")[1]));
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to load properties");
            throw e;
        }
    }

    public void loadDiscordNamesFromStoredPlayerTab() {
        try {
        String numSheets = sheet_definitions.getProperty("num_playbooks_to_load");
        int numSheetsToLoad = Integer.parseInt(numSheets);
        CellRef nameCell = new CellRef(sheet_definitions.getProperty("discord_player_name"));
        int sheetWidth = Integer.parseInt(sheet_definitions.getProperty("single_playbook_width"));
        for (int i = 0; i < numSheetsToLoad; i++) {
            int offset = sheetWidth * i;
            String discordPlayerName = storedPlayerTab.getColumnOffsetValue(nameCell.getCellRef(), offset);
            discordPlayerName = discordPlayerName.toLowerCase().replace(" ","");
            if (discordPlayerName != null && !discordPlayerName.isBlank() && !discordPlayerName.contains("<type")) {
                playerOffsets.put(discordPlayerName.toLowerCase(), offset);
                logger.info("Detected assigned sheet for :" + discordPlayerName + ":");
            }
        }
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to loading player discord names");
            throw e;
        }
    }

    public void loadBasicMoves(String rangeString) throws IOException {
        try {
            for (String rangeRef : rangeString.split(",")) {
                String tab = rangeRef.split("!")[0];
                String rangeToLoad = rangeRef.split("!")[1];
                RangeWrapper range = App.googleSheetAPI.getRange(sheetID, tab, rangeToLoad);
                ArrayList<MoveBuilder> moveList = MoveLoader.loadMovesFromRange(range);
                for (MoveBuilder builder : moveList) {
                    MoveWrapper move = builder.getMoveForGame(this);
                    basicMoves.put(move.name, move);
                    logger.info("Loaded basic move: " + move.name);
                }
            }
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to load properties");
            throw e;
        }
    }

    public void loadAllPlaybookMoves() throws KeyConflictException {
        for (Map.Entry<String, Integer> entry : this.playerOffsets.entrySet()) {
            try {
                String player = entry.getKey();
                int offset = entry.getValue();
                loadPlaybookMovesForPlayer(player, offset);
            } catch (Exception e){
                sendGameMessage("Unexpected error loading playbooks for " + entry.getKey());
                logger.info("Error loading playbooks for " + entry.getKey());
            }
        }
    }

    public void loadPlaybookMovesForPlayer(String player, int offset) throws KeyConflictException {
        PatriciaTrieIgnoreCase<MoveWrapper> playerMoves = new PatriciaTrieIgnoreCase<MoveWrapper>();
        playbookMovesPlayerMap.put(player, playerMoves);
        String play_book_moves_ranges = sheet_definitions.getProperty("play_book_moves_range");
        for (String range : play_book_moves_ranges.split(",")) {
            RangeWrapper rangeToLoad = new RangeWrapper(this.storedPlayerTab, range, offset);
            ArrayList<MoveBuilder> moveList = MoveLoader.loadMovesFromRange(rangeToLoad);
            for (MoveBuilder builder : moveList) {
                try {
                    //TODO - add any metadata processing here
                    MoveWrapper move = builder.getMoveForGame(this);
                    playerMoves.put(move.name, move);
                    logger.info(player + ": loaded playbook move: " + move.name);
                    for (String moveName : builder.getModifiesMoves()) {
                        if (isMove(player, moveName)){
                            MoveWrapper copy = getMove(player, moveName).getModifiedCopy(move);
                            playerMoves.put(copy.name, copy);
                            logger.info(player + ": loaded playbook move: " + copy.name);
                        } else {
                            sendGameMessage("Could not find " + moveName + " while loading " + move.name);
                            throw new IllegalArgumentException("Could not find " + moveName + " while loading " + move.name);
                        }
                    }
                } catch (Exception e){
                    sendGameMessage("Error building move " + builder.get(0));
                    logger.info("Error building move " + builder.get(0));
                }
            }
        }

    }

    public boolean isMove(String author, String string) throws KeyConflictException {
        return getMove(author, string) != null;
    }

    public MoveWrapper getMove(String player, String key) throws KeyConflictException {
        if (playbookMovesPlayerMap.get(player) != null && playbookMovesPlayerMap.get(player).getClosestMatch(key) != null) {
            return playbookMovesPlayerMap.get(player).getClosestMatch(key);
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
        return getLiveCellValue(cellref.getColumnOffsetCellRef(columnOffset));
    }

    public String getLivePlayerValue(String player, String key) throws PlayerNotFoundException, IOException {
        if (!playerOffsets.containsKey(player.toLowerCase())) {
            throw new PlayerNotFoundException("No playbook found registered to " + player);
        }
        return getColumnOffsetLiveValue(sheet_definitions.getProperty(key), playerOffsets.get(player.toLowerCase()));
    }

    public int getStat(String author, String stat) throws IOException, DiscordBotException {
        String value = getLivePlayerValue(author, "stat_" + stat);
        Integer intValue = null;
        try {
            intValue = Integer.parseInt(value);
        } catch (NumberFormatException e){
            throw new MissingValueException("Player: " + author + ", Stat:" + stat+ ", returned Not A Number, please correct your sheet");
        }
        boolean isDebilitated = Boolean.parseBoolean(getLivePlayerValue(author, "stat_" + stat + "_penalty"));
        int penalty = isDebilitated ? 1 : 0;
        return Integer.parseInt(value) - penalty;
    }

    public List<String> getAllStats(){
        return sheet_definitions.keySet().stream()
                .map(prop -> prop.toString())
                .filter(prop -> prop.startsWith("stat_"))
                .filter(prop -> !prop.contains("_penalty"))
                .map(prop -> prop.substring(5))
                .collect(Collectors.toList());
    }

    public boolean isStat(String string) {
        return sheet_definitions.getProperty("stat_" + string) != null;
    }




}
