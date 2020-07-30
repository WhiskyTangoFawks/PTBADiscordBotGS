package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public abstract class AbstractMoveParser {

    public MoveBuilder parseMove(RangeWrapper sheet, int i, int j) {
        String note = sheet.getNote(i, j);

        MoveBuilder builder = new MoveBuilder();
        builder = setBuilderOverridesFromNote(builder, note);

        if ("false".equalsIgnoreCase(sheet.getValue(i, j))) {
            builder.skipLoad = true;
            sheet.setValue(i, i, "TRUE");
        }

        boolean breakColumn = false;
        for (int k = 0; !breakColumn; k++) {
            builder.addLine(getRow(sheet, i, j + k));
            if (builder.get(k).isEmpty() || (!builder.isList && k > 0)) {
                breakColumn = true;
            }
        }
        //return null if no title has been set
        return builder.get(0).isBlank() ? null : builder;
    }

    protected String getRow(RangeWrapper sheet, int i, int j) {
        boolean enabled = true;
        boolean breakRow = false;
        StringBuilder builder = new StringBuilder();

        for (int l = 0; !breakRow; l++) {
            String value = sheet.getValue(i + l, j);
            if (null == value || value.isEmpty()) {
                breakRow = true;
            } else if ("true".equalsIgnoreCase(value)) {
                enabled = true;
            } else if ("false".equalsIgnoreCase(value)) {
                if (builder.length() == 0) {
                    builder.append(" ");
                }
                enabled = false;
            } else if (enabled) {
                if (builder.toString().isBlank()) {
                    builder = new StringBuilder();
                }
                builder.append(value);
            }
        }
        return builder.toString();
    }

    public MoveBuilder setBuilderOverridesFromNote(MoveBuilder builder, String note) {
        for (String parameter : note.split(";")) {
            String[] split = parameter.split("=");
            String tag = split[0];
            if (tag.equalsIgnoreCase(SheetParserService.PARSER.playbook_move.name()) || tag.equalsIgnoreCase(SheetParserService.PARSER.basic_move.name())) {
                if (split.length > 1) {
                    builder.overrideName = split[1];
                }
            } else if (tag.equalsIgnoreCase("text")) {
                builder.overrideText = split[1];
            } else if (tag.equalsIgnoreCase("list")) {
                builder.isList = true;
            }
        }
        return builder;
    }


}
