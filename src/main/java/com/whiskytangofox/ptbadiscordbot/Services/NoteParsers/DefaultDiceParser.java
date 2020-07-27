package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.ParsedCommand;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;

public class DefaultDiceParser implements INoteParser {

    @Override
    public String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        String value = sheet.getValue(i, j);
        for (String parameter : value.split(" ")) {
            if (ParsedCommand.isDieNotation(parameter)) {
                playbook.moveOverrideDice.put(note.split("=")[1], parameter);
                return note.split("=")[1];
            }
        }
        return null;
    }
}
