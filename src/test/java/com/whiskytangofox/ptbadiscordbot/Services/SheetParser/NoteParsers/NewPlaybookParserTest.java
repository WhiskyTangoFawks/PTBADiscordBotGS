package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class NewPlaybookParserTest extends INoteParserTest {

    NewPlaybookParser parser = new NewPlaybookParser();

    @Test
    public void testParse() {
        String note = SheetParserService.PARSER.new_playbook.name();
        book.title = "old";
        values.put(new CellReference("A1"), "New Playbook Title");
        notes.put(new CellReference("A1"), note);

        parser.parse(service, sheet, book, note, 1, 1);
        assertEquals("New Playbook Title", service.playbook.title);
    }

}