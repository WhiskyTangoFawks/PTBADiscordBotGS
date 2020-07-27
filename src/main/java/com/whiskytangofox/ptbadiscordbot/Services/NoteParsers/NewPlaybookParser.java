package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;

public class NewPlaybookParser implements INoteParser {
    @Override
    public String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        service.checkAndRegisterPlayBook(playbook);
        service.playbook = new Playbook(service.game.sheet, sheet.tab);
        playbook.title = sheet.getValue(i, j);
        return playbook.title;
    }
}
