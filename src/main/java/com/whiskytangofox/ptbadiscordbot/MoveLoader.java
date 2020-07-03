package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MoveLoader {
    static final Logger logger = LoggerFactory.getLogger(App.class);

    public static ArrayList<MoveBuilder> loadMovesFromRange(RangeWrapper sheet) {
        ArrayList<MoveBuilder> list = new ArrayList<MoveBuilder>();
        for (int i = sheet.firstCell.getColumnInt(); i < sheet.lastCell.getColumnInt() + 1; i++) {
            for (int j = sheet.firstCell.getRow(); j < sheet.lastCell.getRow() + 1; j++) {

                MoveBuilder builder = new MoveBuilder();
                boolean breakColumn = false;
                for (int k = 0; !breakColumn; k++) {
                    builder.addLine();

                    String tempAbove = sheet.getValue(i, j-1);
                    String tempLeft = sheet.getValue(i-1, j);

                    boolean okToStart = (tempAbove == null || tempAbove.isEmpty()) &&
                            (tempLeft == null || tempLeft.isEmpty());

                    boolean enabled = true;
                    boolean breakRow = false;
                    if (!okToStart){
                        breakRow = true; // don't start without blank above and to the left
                    }
                    for (int l = 0; !breakRow; l++) {

                        String value = sheet.getValue(i+l, j+k);
                        if (null == value || value.isEmpty()) {
                            breakRow = true;
                        } else if ("true".equalsIgnoreCase(value)) {
                            enabled = true;
                        } else if ("false".equalsIgnoreCase(value)) {
                            if (builder.get(k).isEmpty()) {
                                builder.set(k, " ");
                            }
                            enabled = false;
                        } else if (enabled) {
                             builder.extend(k, value);
                        }
                        //TODO - enable note usage
                        //String note = sheet.getNote(i+l, j+k);
                        //builder.addNote(note);
                    }
                    if (builder.get(k).isEmpty()) {
                        breakColumn = true;
                    }
                }
                if (builder.isValid()) {
                    list.add(builder);
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