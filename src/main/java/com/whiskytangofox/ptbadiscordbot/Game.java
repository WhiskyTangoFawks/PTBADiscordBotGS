package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.MissingValueException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.whiskytangofox.ptbadiscordbot.App.googleSheetAPI;
import static com.whiskytangofox.ptbadiscordbot.App.logger;

public class Game {

    public Properties settings = new Properties();

    public PatriciaTrieIgnoreCase<MoveWrapper> basicMoves = new PatriciaTrieIgnoreCase<MoveWrapper>();
    public HashMapIgnoreCase<Playbook> playbooks = new HashMapIgnoreCase<Playbook>();

    private final String sheetID;

    public final Guild guild;
    public final MessageChannel channel;
    public SheetReader reader;

    private final boolean debugLog;

    public Game(Guild guild, MessageChannel channel, String sheetID, Boolean debug) throws IOException {
        this.guild = guild;
        this.debugLog = debug;
        this.channel = channel;
        this.sheetID = sheetID;
        this.reader = new SheetReader(this);
        if (sheetID != null) try { //if sheetID == null, then we are running tests
            readSheet();
        } catch (Exception e){
            sendGameMessage("Unexpected exception while trying to read the game sheet");
            throw e;
        }
    }

    public void readSheet() throws IOException {
        ArrayList<RangeWrapper> sheet = App.googleSheetAPI.getSheet(sheetID);
        sendDebugMsg("Google sheet retrieved");
        for (RangeWrapper tab : sheet){
            if (tab.tab.equalsIgnoreCase("properties"))  {
                readPropertiesTab(tab);
            } else {
                reader.parseSheet(tab);
            }
            sendDebugMsg("Parsed sheet: "+ tab.tab);
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

    public void copyAndStoreModifiedBasicMoves(){
        for (Playbook book : playbooks.values()){
            HashMap<String, MoveWrapper> buffer = new HashMap<String, MoveWrapper>();
            for (MoveWrapper advanced : book.moves.values()){
                for (String basicName : advanced.getModifiesMoves()){
                    try {
                        MoveWrapper basic = buffer.get(basicName);
                        if (basic == null){
                            basic = getMove(book.player, basicName);
                        }
                        if (basic != null) {
                            MoveWrapper copy = basic.getModifiedCopy(advanced);
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

    public void sendGameMessage(String msg){
        if (channel != null) {
            channel.sendMessage(msg).queue();
        } else {
            logger.info("Test result for sendGameMessage: " + msg);
        }
    }

    public void reloadGame() throws IOException {
        readSheet();
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
        Playbook book = playbooks.get(author);
        String statRef = book.stats.get(stat).getCellRef();
        String penaltyRef = book.stat_penalties.get(stat).getCellRef();
        ArrayList<String> list = new ArrayList<String>();
        list.add(statRef);
        list.add(penaltyRef);
        List<String> response = googleSheetAPI.getValues(sheetID, book.tab, list);

        Integer intValue = null;
        try {
            intValue = Integer.parseInt(response.get(0));
        } catch (NumberFormatException e){
            throw new MissingValueException("Player: " + author + ", Stat:" + stat+ ", returned Not A Number, please correct your sheet");
        }

        boolean isDebilitated = Boolean.parseBoolean(response.get(1));
        int penalty = isDebilitated ? 1 : 0;
        return intValue - penalty;
    }

    public boolean isStat(String player, String stat) throws PlayerNotFoundException {
        Playbook book = playbooks.get(player);
        if (book == null){
            throw new PlayerNotFoundException("Unable to find a registered playbook for: "+ player);
        }
        return book.stats.containsKey(stat);
    }

    public Collection<String> getRegisteredStatsForPlayer(String player) throws PlayerNotFoundException {
        Playbook book = playbooks.get(player);
        if (book == null){
            throw new PlayerNotFoundException("No playbook registered to "+ player);
        }
        return book.stats.keySet();
    }

   public boolean isResource(String player, String resource) throws PlayerNotFoundException {
        return getPlaybook(player).resources.containsKey(resource);
    }

    public SetResourceResult modifyResource(String player, String resource, int mod) throws PlayerNotFoundException, IOException {
        Playbook book = getPlaybook(player);
        List<CellRef> cells = book.resources.get(resource);
        List<String> refs = cells.stream().map(c -> c.getCellRef()).collect(Collectors.toList());
        List<String> values = googleSheetAPI.getValues(sheetID, book.tab, refs);
            int oldValue = 0;
        int newValue = 0;
        if (values.size() == 1 && ParsedCommand.isInteger(values.get(0))){
            oldValue = Integer.parseInt(values.get(0));
            newValue = oldValue + mod;
            values.set(0, String.valueOf(newValue));
            if (mod != 0) {
                googleSheetAPI.setValues(sheetID, book.tab, refs, values);
            }
        }else if (values.get(0).equalsIgnoreCase("true") || values.get(0).equalsIgnoreCase("true")){
            oldValue = (int)values.stream().filter(v -> v.equalsIgnoreCase("true")).count();
            if (mod != 0) {
                //for a positive, iterate up, for a negative, iterate down
                boolean isModPos = mod > 0;
                int counter = 0;
                if (isModPos) {
                    for (int i = 0; counter < mod && i < values.size(); i++) {
                        if (values.get(i) == "FALSE") {
                            values.set(i, "TRUE");
                            counter++;
                        }
                    }
                } else { //modIsNegative
                    for (int i = values.size()-1; counter < Math.abs(mod) && i >= 0; i--) {
                        if (values.get(i) == "TRUE") {
                            values.set(i, "FALSE");
                            counter++;
                        }
                    }
                }
                googleSheetAPI.setValues(sheetID, book.tab, refs, values);
            }
            newValue = (int)values.stream().filter(v -> v.equalsIgnoreCase("true")).count();
        }
        return new SetResourceResult(resource, oldValue, mod, newValue);
    }


    public Playbook getPlaybook(String player) throws PlayerNotFoundException {
        if (!playbooks.containsKey(player)){
            throw new PlayerNotFoundException("No playbook found registered to " + player);
        }
        return playbooks.get(player);
    }

    public void sendDebugMsg(String msg){
        if (debugLog){
            sendGameMessage(msg);
        }
    }

}
