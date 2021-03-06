package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public class BasicMoveParser extends AbstractMoveParser implements INoteParser {
    @Override
    public String parse(SheetParserService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        MoveBuilder builder = parseMove(sheet, i, j);
        if (builder != null) {
            Move move = builder.getMove();
            if (!builder.skipLoad) {
                service.registerBasicMove(move);
            } else {
                service.registerSkippedMove(move.getReferenceMoveName());
            }
            return move.name;
        }
        return null;
    }
}
