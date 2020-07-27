package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultDiceParserTest extends INoteParserTest {

    DefaultDiceParser parser = new DefaultDiceParser();

    @Test
    public void testReadSheetDefaultDice() {
        String note = SheetReaderService.PARSER.default_dice.name() + "=Move Name";
        values.put(new CellReference("A2"), "damage 1d10");
        notes.put(new CellReference("A2"), note);

        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals("1d10", book.moveOverrideDice.get("movename"));
    }

}