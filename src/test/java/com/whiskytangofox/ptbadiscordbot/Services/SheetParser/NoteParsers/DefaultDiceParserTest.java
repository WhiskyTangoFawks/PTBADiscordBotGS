package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

;

public class DefaultDiceParserTest extends INoteParserTest {

    DefaultDiceParser parser = new DefaultDiceParser();

    @Test
    public void testReadSheetDefaultDice() {
        String note = SheetParserService.PARSER.default_dice.name() + "=Move Name";
        values.put(new CellReference("A2"), "damage 1d10");
        notes.put(new CellReference("A2"), note);

        parser.parse(service, sheet, book, note, 1, 2);

        assertEquals("1d10", book.moveOverrideDice.get("movename"));
    }

}