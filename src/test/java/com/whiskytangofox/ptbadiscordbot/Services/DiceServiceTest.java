package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static org.junit.Assert.*;

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
        command.addModifier("stat", Command.TYPE.STAT, "2");
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("2d6"));
        assertTrue(result.contains("2"));
    }

    @Test
    public void roll_statNeg() {
        command.addModifier("stat", Command.TYPE.STAT, "-2");
        String result = underTest.roll(command);
        logger.info(result);
        assertTrue(result.contains("2d6"));
        assertTrue(result.contains("-2"));
    }

    @Test
    public void testDoRollingAndGetRollString() {
        ArrayList<Integer> resultList = new ArrayList<>();
        Dice dice = new Dice(3, 4);
        String result = underTest.doRollingAndGetRollString(dice, resultList);
        logger.info(result);
        assertEquals(3, resultList.size());

    }

    @Test
    public void testGetDescriptor() {
        command.addModifier("stat", Command.TYPE.STAT, "+2");
        command.addModifier("-1", Command.TYPE.INTEGER, "-1");
        String result = underTest.getDescriptor(command);
        logger.info(result);
        assertTrue(result.contains("2d6"));
        assertTrue(result.contains("(stat)"));
        assertFalse(result.contains("+2"));
        assertTrue(result.contains("-1"));
    }

    @Test
    public void testGetModifiersValues() {
        command.addModifier("stat", Command.TYPE.STAT, "+2");
        command.addModifier("", Command.TYPE.INTEGER, "-1");
        String result = underTest.getModifiersValues(command);
        logger.info(result);
        assertFalse(result.contains("2d6"));
        assertFalse(result.contains("(stat)"));
        assertTrue(result.contains("2"));
        assertTrue(result.contains("-1"));
    }
}