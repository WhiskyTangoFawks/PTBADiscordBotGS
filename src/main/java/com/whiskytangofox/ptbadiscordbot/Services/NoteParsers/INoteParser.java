package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;

public interface INoteParser {

    String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j);

}
