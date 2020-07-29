package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public abstract class AbstractMoveParser {

    public MoveBuilder parseMove(RangeWrapper sheet, int i, int j) {
        String note = sheet.getNote(i, j);
        boolean isList = false;
        if (sheet.getNote(i, j) != null && !sheet.getNote(i, j).isBlank()) {
            String noteName = null;
            String noteText = null;
            for (String parameter : note.split(";")) {
                String tag = parameter.split("=")[0];
                if (tag.equalsIgnoreCase(SheetParserService.PARSER.playbook_move.name()) || tag.equalsIgnoreCase(SheetParserService.PARSER.basic_move.name())) {
                    if (parameter.contains("=")) {
                        noteName = parameter.split("=")[1];
                    }
                } else if (tag.equalsIgnoreCase("text")) {
                    noteText = parameter.split("=")[1];
                } else if (tag.equalsIgnoreCase("list")) {
                    isList = true;
                }
            }
            if (noteName != null || noteText != null) {
                if (isList) {
                    throw new IllegalArgumentException("list moves cannot be combined with override text");
                }
                return new MoveBuilder().addLine(noteName != null ? noteName : sheet.getValue(i, j))
                        .addLine(noteText != null ? noteText : sheet.getValue(i, j));
            }
        }

        MoveBuilder builder = new MoveBuilder();
        boolean breakColumn = false;
        for (int k = 0; !breakColumn; k++) {
            builder.addLine();

            boolean enabled = true;
            boolean breakRow = false;

            for (int l = 0; !breakRow; l++) {

                String value = sheet.getValue(i + l, j + k);
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
            }
            if (builder.get(k).isEmpty() || (!isList && k > 0)) {
                breakColumn = true;
            }
        }
        //return null if no title has been set
        return builder.get(0).isBlank() ? null : builder;
    }

    public MoveBuilder parseMoveFromNote(String note) {
        MoveBuilder builder = new MoveBuilder();
        String name = null;
        String text = null;
        for (String parameter : note.split(";")) {
            String[] split = parameter.split("=");
            if (split[0].equalsIgnoreCase(SheetParserService.PARSER.playbook_move.name()) || split[0].equalsIgnoreCase(SheetParserService.PARSER.basic_move.name())) {
                name = split[1];
            } else if (split[0].equalsIgnoreCase("text")) {
                text = split[1];
            }
        }
        builder.addLine();
        builder.set(0, name);
        builder.addLine();
        builder.set(1, text);
        return builder;
    }


}
