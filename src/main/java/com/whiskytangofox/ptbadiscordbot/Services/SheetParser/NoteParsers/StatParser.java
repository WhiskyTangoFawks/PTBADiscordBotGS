package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public class StatParser implements INoteParser {

    @Override
    public String parse(SheetParserService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        String statName;
        statName = note.replace(SheetParserService.PARSER.stat.name() + "=", "").toLowerCase();
        playbook.stats.put(statName, new CellReference(i, j));
        return statName;
    }

}
