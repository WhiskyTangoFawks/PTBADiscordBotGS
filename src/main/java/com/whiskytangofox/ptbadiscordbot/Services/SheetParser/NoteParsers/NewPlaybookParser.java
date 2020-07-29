package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;

public class NewPlaybookParser implements INoteParser {
    @Override
    public String parse(SheetParserService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
        if (playbook != null) {
            service.checkAndRegisterPlayBook(playbook);
        }
        service.playbook = new Playbook(service.game.sheet, sheet.tab);
        service.playbook.title = sheet.getValue(i, j);
        return service.playbook.title;
    }
}
