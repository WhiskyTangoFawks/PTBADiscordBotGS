package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.StatResponse;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Command {

    protected final Playbook book;
    public boolean failMsg;

    public Command(Playbook book, String raw) {
        this.book = book;
        rawToParse.add(raw);
    }

    public Queue<String> rawToParse = new LinkedList<String>();

    public Move move = null;
    public StatResponse stat = null;
    public String resource = null;

    public int mod = 0;

    public ArrayList<Dice> dice = new ArrayList<>();

    public boolean doRoll = false;

    public void setDefaultDice() {
        if (move != null && book.getMoveDice(move.name) != null) {
            parseDieNotation(book.getMoveDice(move.name));
        } else {
            parseDieNotation(book.getSetting(GameSettings.KEY.default_system_dice));
            failMsg = Boolean.parseBoolean(book.getSetting(GameSettings.KEY.fail_xp));
        }
        doRoll = true;
    }

    public void parseDieNotation(String token) {
        String[] rollParameters = token.split("d");

        int num = 0;
        int size = 0;
        if (rollParameters.length == 2) {
            num = Integer.parseInt(rollParameters[0]);
            size = Integer.parseInt(rollParameters[1]);
            dice.add(new Dice(num, size));
        }
    }
}
