package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public class DefaultDiceParser implements INoteParser {

    @Override
    public String parse(SheetParserService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        String value = sheet.getValue(i, j);
        for (String parameter : value.split(" ")) {
            if (parameter.matches(".*\\dd\\d.*")) {
                playbook.moveOverrideDice.put(note.split("=")[1], parameter);
                return note.split("=")[1];
            }
        }
        return null;
    }
}
