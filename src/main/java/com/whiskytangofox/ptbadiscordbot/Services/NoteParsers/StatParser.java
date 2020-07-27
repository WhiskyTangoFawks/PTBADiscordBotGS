package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;

public class StatParser implements INoteParser {

    @Override
    public String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        String statName;
        statName = note.replace(SheetReaderService.PARSER.stat.name() + "=", "").toLowerCase();
        playbook.stats.put(statName, new CellReference(i, j));
        return statName;
    }

}
