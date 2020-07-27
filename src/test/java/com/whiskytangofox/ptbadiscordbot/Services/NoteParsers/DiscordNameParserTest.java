package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiscordNameParserTest extends INoteParserTest {

    DiscordNameParser parser = new DiscordNameParser();

    @Test
    public void testParse() {
        String note = SheetReaderService.PARSER.discord_name.name();
        values.put(new CellReference("A1"), "discordName");
        notes.put(new CellReference("A1"), note);

        parser.parse(service, sheet, book, note, 1, 1);
        assertEquals("discordName", book.player);
    }
}