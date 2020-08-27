package com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GameGoogle;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.SheetParserService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

public abstract class INoteParserTest {

    @Mock
    SheetParserService service;

    @Mock
    SheetAPIService gameSheetService;

    @Mock
    GameGoogle mockGame;

    Playbook book;


    //@Rule
    //public MockitoRule mockitoRule = MockitoJUnit.rule();

    static RangeWrapper sheet;
    static HashMap<CellReference, String> values;
    static HashMap<CellReference, String> notes;

    @BeforeEach
    public void before() {
        MockitoAnnotations.initMocks(this);
        values = new HashMap<>();
        notes = new HashMap<>();
        sheet = new RangeWrapper(values, notes, null, "A1:D4");
        book = new Playbook(gameSheetService, null);
        service.game = mockGame;

    }

}
