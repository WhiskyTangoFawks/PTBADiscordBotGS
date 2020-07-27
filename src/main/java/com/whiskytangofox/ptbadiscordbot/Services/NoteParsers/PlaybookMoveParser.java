package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;

public class PlaybookMoveParser extends AbstractMoveParser implements INoteParser {

    @Override
    public String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        MoveBuilder builder = parseMove(sheet, i, j);
        if (builder != null) {
            Move move = builder.getMove();
            playbook.moves.put(move.name, move);
            return move.name;
        }
        return null;
    }
}
