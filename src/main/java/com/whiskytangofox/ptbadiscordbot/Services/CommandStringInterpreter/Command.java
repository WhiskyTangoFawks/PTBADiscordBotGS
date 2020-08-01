package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.DataObjects.GameSettings;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;

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

    public Queue<String> rawToParse = new LinkedList<>();
    public LinkedList<Modifier> modifiers = new LinkedList<>();

    public Move move = null;
    public String resource = null;

    public ArrayList<Dice> dice = new ArrayList<>();

    public boolean doRoll = false;

    public void setDefaultDice() {
        if (move != null && book.getMoveDice(move.getReferenceMoveName()) != null) {
            parseDieNotation(book.getMoveDice(move.getReferenceMoveName()));
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

    public void addModifier(String name, TYPE type, String mod) {
        modifiers.add(new Modifier(name, type, mod));
    }

    public boolean hasStat() {
        return modifiers.stream().anyMatch(m -> m.type == TYPE.STAT);
    }

    public enum TYPE {INTEGER, STAT, PENALTY}

    public static class Modifier {
        public TYPE type;
        public String name;
        public String sign;
        public int mod;

        public Modifier(String name, TYPE type, String mod) {
            this.type = type;
            this.name = name;
            this.mod = Integer.parseInt(mod);
            sign = this.mod > -1 ? "+" : "";
        }
    }
}
