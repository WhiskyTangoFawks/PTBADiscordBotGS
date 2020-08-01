package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;
import com.whiskytangofox.ptbadiscordbot.DataStructure.HashSetIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.PlaybookService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static com.whiskytangofox.ptbadiscordbot.App.googleSheetAPI;

public abstract class AbstractGameSheetMethods {

    public GameSettings settings = new GameSettings();
    public PatriciaTrieIgnoreCase<Move> basicMoves = new PatriciaTrieIgnoreCase<>();
    public PlaybookService playbooks;
    public final SheetParserService parser;
    public SheetAPIService sheet;
    public HashSetIgnoreCase<String> skippedMoves;

    public AbstractGameSheetMethods(String sheetID) {
        sheet = new SheetAPIService(sheetID, googleSheetAPI, settings);
        this.parser = new SheetParserService(this);
        this.playbooks = new PlaybookService(sheet);
        this.skippedMoves = new HashSetIgnoreCase<>();
    }

    public abstract void sendDebugMsg(String msg);

    public abstract void sendGameMsg(String msg);

    public abstract void sendGameMsg(String player, String msg);

    public void initGame() throws IOException {
        ArrayList<RangeWrapper> sheetData = sheet.getSheet();
        sendDebugMsg("Google sheet retrieved");
        parseSheets(sheetData);
    }

    public void postInit() {
        try {
            sendDebugMsg("Sheets loaded, finalizing");
            copyAndStoreModifiedBasicMoves();
            for (Playbook book : playbooks.playbooks.values()) {
                String msg = book.getValidationMsg();
                if (msg != null) {
                    sendGameMsg(msg);
                } else {
                    sendDebugMsg(book.title + " validated");
                }

            }
        } catch (Exception e) {
            sendGameMsg("Unexpected exception while finalizing load: " + e.toString());
        }
    }

    public void parseSheets(ArrayList<RangeWrapper> sheetData) {
        for (RangeWrapper tab : sheetData) {
            if (tab.tab.equalsIgnoreCase("properties")) {
                readPropertiesTab(tab);
            } else {
                parser.parseSheet(tab);
            }
            sendDebugMsg("Parsed sheet: " + tab.tab);
        }
    }

    public void readPropertiesTab(RangeWrapper tab) {
        try {
            tab.getValueSet().stream()
                    .filter(Objects::nonNull)
                    .filter(prop -> prop.contains("="))
                    .forEach(prop -> settings.set(GameSettings.KEY.valueOf(prop.split("=")[0]), prop.split("=")[1]));
            sendDebugMsg("Properties loaded");
        } catch (Exception e) {
            sendGameMsg("Unexpected exception while trying to load properties");
            throw e;
        }
    }

    public void copyAndStoreModifiedBasicMoves() {
        for (Playbook book : playbooks.playbooks.values()) {
            HashMap<String, Move> buffer = new HashMap<>();
            for (Move advanced : book.moves.values()) {
                for (String basicName : advanced.getChildMoveNames()) {
                    try {
                        Move basic = buffer.get(basicName);
                        if (basic == null) {
                            basic = book.getMove(basicName);
                        }
                        if (basic != null) {
                            Move copy = basic.getModifiedCopy(advanced);
                            buffer.put(copy.name, copy);
                        } else if (!isMoveSkipped(basicName)) {
                            sendGameMsg("Unable to find: " + basicName + " while loading " + advanced.name);
                        }
                    } catch (Exception e) {
                        sendGameMsg("Exception occurred while trying to load modified basic move: " + advanced.name);
                        e.printStackTrace();
                    }
                }
            }
            book.moves.putAll(buffer);
        }
    }

    public void registerSkippedMove(String moveName) {
        skippedMoves.add(moveName);
    }

    public boolean isMoveSkipped(String moveName) {
        return skippedMoves.contains(moveName);
    }

}
