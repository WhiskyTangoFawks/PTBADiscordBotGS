package com.whiskytangofox.ptbadiscordbot.Services.NoteParsers;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Game;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.GameSheetService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetReaderService;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.HashMap;

public abstract class INoteParserTest {

    @Mock
    SheetReaderService service;

    @Mock
    GameSheetService gameSheetService;

    @Mock
    Game mockGame;

    Playbook book;


    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    static RangeWrapper sheet;
    static HashMap<CellReference, String> values;
    static HashMap<CellReference, String> notes;

    @Before
    public void before() {
        values = new HashMap<>();
        notes = new HashMap<>();
        sheet = new RangeWrapper(values, notes, null, "A1:D4");
        book = new Playbook(gameSheetService, null);
        MockitoAnnotations.initMocks(this);
    }

}
