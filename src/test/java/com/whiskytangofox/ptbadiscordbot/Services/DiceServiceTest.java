package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.StatResponse;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class DiceServiceTest {

    public static final Logger logger = LoggerFactory.getLogger(DiceService.class);

    DiceService underTest = new DiceService();
    Command command;

    @Mock
    Playbook mockBook;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        command = new Command(mockBook, "test roll");
        command.doRoll = true;
        command.dice.add(new Dice(2, 6));
    }

    @Test
    public void roll() {
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("2d6"));
    }

    @Test
    public void roll_adv() {
        command.dice.get(0).setAdv();
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("3d6"));
    }

    @Test
    public void roll_dis() {
        command.dice.get(0).setDis();
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("3d6"));
    }

    @Test
    public void roll_statPos() {
        command.stat = new StatResponse("stat", 2, false);
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("2d6"));
        assertTrue(result.contains("+2"));
    }

    @Test
    public void roll_statNeg() {
        command.stat = new StatResponse("stat", -2, false);
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("2d6"));
        assertTrue(result.contains("-2"));
    }
}