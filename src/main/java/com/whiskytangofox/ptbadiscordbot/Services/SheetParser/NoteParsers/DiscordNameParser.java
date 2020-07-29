package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public class DiscordNameParser implements INoteParser {


    @Override
    public String parse(SheetParserService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        playbook.player = sheet.getValue(i, j).split("#")[0];
        return playbook.player;
    }
}
