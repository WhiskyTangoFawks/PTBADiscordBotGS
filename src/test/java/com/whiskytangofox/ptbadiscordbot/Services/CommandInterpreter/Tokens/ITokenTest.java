package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.when;

public abstract class ITokenTest {

    @Mock
    Playbook mockBook;

    @Mock
    SheetAPIService mockSheet;

    Command command;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        command = new Command(mockBook, "");
        command.rawToParse.poll();
        mockBook.sheet = mockSheet;
        when(mockBook.getSetting(GameSettings.KEY.default_system_dice)).thenReturn("2d6");
    }
}
