package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.CommandInterpreterService;
import com.whiskytangofox.ptbadiscordbot.Services.DiceService;
import com.whiskytangofox.ptbadiscordbot.Services.PlaybookService;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GameTest_GameFunctions {

    public static final Logger logger = LoggerFactory.getLogger(GameTest_GameFunctions.class);

    @Mock
    CommandInterpreterService mockInterpreter;

    @Mock
    DiceService mockDiceService;

    @Mock
    PlaybookService mockBookService;

    @Mock
    Playbook mockBook;

    @Mock
    SheetAPIService mockSheet;

    @Mock
    GameSettings mockSettings;

    @Mock
    MessageReceivedEvent mockEvent;

    @Mock
    Message mockMessage;

    @Mock
    User mockUser;

    Game underTest;

    @Before
    public void setupGame() throws Exception {
        underTest = new Game(null, null, null, false);
        MockitoAnnotations.initMocks(this);

        underTest.settings = mockSettings;
        when(mockSettings.get(GameSettings.KEY.commandchar)).thenReturn("!");
        when(mockSettings.get(GameSettings.KEY.default_system_dice)).thenReturn("2d6");
        when(mockSettings.get(GameSettings.KEY.stat_debility_tag)).thenReturn("dis");
        when(mockSettings.get(GameSettings.KEY.fail_xp)).thenReturn("true");

        underTest.playbooks = mockBookService;

        underTest.sheet = mockSheet;

        underTest.interpreter = mockInterpreter;

        underTest.rollerService = mockDiceService;
        when(mockDiceService.roll(any())).thenReturn("*Rolled* *{2d6}* :: [4], [5] =  9");

        when(mockEvent.getMessage()).thenReturn(mockMessage);
        when(mockEvent.getAuthor()).thenReturn(mockUser);
        when(mockUser.getName()).thenReturn("TestUser");
    }

    @Test
    public void testOnMessageReceived() throws IOException, DiscordBotException, KeyConflictException {
        when(mockMessage.getContentDisplay()).thenReturn("!roll");
        Command command = new Command(mockBook, "test");
        command.doRoll = true;
        command.dice.add(new Dice(2, 6));
        when(mockInterpreter.interpretCommandString(any(), any())).thenReturn(command);
        logger.info(underTest.OnMessageReceived(mockEvent));
    }

}