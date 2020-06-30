package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MoveLoader {
    static final Logger logger = LoggerFactory.getLogger(App.class);

    public static ArrayList<MoveWrapper> loadMovesFromRange(RangeWrapper sheet, PatriciaTrieIgnoreCase<MoveWrapper> basicMoves) {
        ArrayList list = new ArrayList<MoveWrapper>();
        for (int i = sheet.firstCell.getColumnInt(); i < sheet.lastCell.getColumnInt() + 1; i++) {
            for (int j = sheet.firstCell.getRow(); j < sheet.lastCell.getRow() + 1; j++) {

                String moveName = sheet.getValue(i, j);
                if ("true".equalsIgnoreCase(moveName)) {
                    moveName = sheet.getValue(i + 1, j);
                } else if ("false".equalsIgnoreCase(moveName)) {
                    moveName = null;
                }
                if (moveName == null || moveName.isEmpty()) {
                    continue;
                }

                String moveText = "";
                boolean breakColumn = false;
                for (int k = 1; !breakColumn; k++) {

                    String row = "";
                    boolean enabled = true;
                    boolean breakRow = false;
                    for (int l = 0; !breakRow; l++) {

                        String temp = sheet.getValue(i + l, j + k);
                        if (null == temp || temp.isEmpty()) {
                            breakRow = true;
                        } else if ("true".equalsIgnoreCase(temp)) {
                            enabled = true;
                        } else if ("false".equalsIgnoreCase(temp)) {
                            if (row.isEmpty()) {
                                row = " ";
                            }
                            enabled = false;
                        } else if (enabled) {
                            row = row.isBlank() ? temp : row + " " + temp;
                        }
                    }
                    if (row.isEmpty()) {
                        breakColumn = true;
                    } else if (!row.isBlank()) {
                        moveText = moveText + System.lineSeparator() + row;
                    }
                }
                if (!moveText.isBlank() && !moveName.isBlank()) {
                    MoveWrapper move = new MoveWrapper(moveName, moveName + moveText);

                    //Secondary moves
                    if (move.name.contains("(")) {
                        int beginIndex = move.name.indexOf("(");
                        int endIndex = move.name.indexOf(")");
                        MoveWrapper basicMove = getMoveFromList(move.name.substring(beginIndex + 1, endIndex), list);
                        if (basicMove == null){
                            basicMove = basicMoves.get(move.name.substring(beginIndex + 1, endIndex));
                        }
                        if (basicMove == null) {
                            throw new IllegalArgumentException("During load Secondary move, basic move load failed: " + move.name.substring(beginIndex + 1, endIndex));
                        }
                        MoveWrapper basicMove2 = new MoveWrapper(basicMove.name, basicMove.text);
                        basicMove2.appendSecondaryMove(move);
                        list.add(basicMove2);
                        logger.info("RELoaded basic move: " + basicMove2.name + " (" + move.name.substring(0, beginIndex - 1) + ") : default stat=" + move.stat);
                    }
                    list.add(move);
                    logger.info("Loaded move: " + moveName + ": default stat=" + move.stat);
                    //logger.info(moveText);
                }
            }
        }
        return list;
    }

    private static MoveWrapper getMoveFromList(String moveName, ArrayList<MoveWrapper> moves){
        for (MoveWrapper move : moves){
            if (moveName.equalsIgnoreCase(move.name)){
                return move;
            }
        }
        return null;
    }
}