package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DiscordNameParserTest extends INoteParserTest {

    DiscordNameParser parser = new DiscordNameParser();

    @Test
    public void testParse() {
        String note = SheetParserService.PARSER.discord_name.name();
        values.put(new CellReference("A1"), "discordName");
        notes.put(new CellReference("A1"), note);

        parser.parse(service, sheet, book, note, 1, 1);
        assertEquals("discordName", book.player);
    }
}