package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DiceService {

    private static final Random random = new Random();

    public String roll(Command command) {
        StringBuilder msg = new StringBuilder("*Rolled*");
        ArrayList<Integer> rolls = new ArrayList<>();

        command.dice.stream()
                .filter(d -> d.adv || d.dis)
                .forEach(d -> d.num++);

        msg.append(" ").append(getDescriptor(command)).append(" :: ");

        command.dice.forEach(dice ->
                msg.append(doRollingAndGetRollString(dice, rolls)));

        msg.append(getModifiersValues(command));

        int rollSum = rolls.stream().mapToInt(Integer::intValue).sum();
        int modifierSum = command.modifiers.stream().mapToInt(m -> m.mod).sum();
        int sum = rollSum + modifierSum;

        msg.append("  =  ").append(sum);
        if (command.failMsg && sum < 7) {
            msg.append(System.lineSeparator()).append("*Don't forget to mark EXP on a 6-*");
        }
        return msg.toString();
    }

    public String doRollingAndGetRollString(Dice dice, ArrayList<Integer> rolls) {
        StringBuilder rollString = new StringBuilder();
        for (int i = 0; i < dice.num; i++) {
            rolls.add(random.nextInt(dice.size) + 1);
            rollString.append("[").append(rolls.get(rolls.size() - 1)).append("]");
            if (i < dice.num - 1) {
                rollString.append(", ");
            }
        }
        if (dice.adv || dice.dis) {
            return dropLowAndStrikeThru(rolls, rollString.toString(), dice.adv, dice.dis);
        } else {
            return rollString.toString();
        }
    }

    private String dropLowAndStrikeThru(ArrayList<Integer> rolls, String msg, boolean adv, boolean dis) {
        String drop = null;
        String replacement = null;
        Collections.sort(rolls);

        if (adv) {
            drop = "[" + rolls.get(0) + "]";
            replacement = "~~[" + rolls.get(0) + "]~~";
            rolls.remove(0);
        } else if (dis) {
            drop = "["+rolls.get(rolls.size()-1)+"]";
            replacement = "~~["+ rolls.get(rolls.size()-1) + "]~~";
            rolls.remove(rolls.size()-1);
        }
         return replaceLast(msg, drop, replacement);
    }

    public String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }

    public String getDescriptor(Command command) {
        StringBuilder msg = new StringBuilder("*{");
        for (int i = 0; i < command.dice.size(); i++) {
            if (i > 0 && i < command.dice.size() - 1) {
                msg.append(", ");
            }
            msg.append(command.dice.get(i).getNotation());
        }
        command.modifiers.forEach(m -> msg.append(" ").append(m.commandSign).append("(").append(m.name).append(")"));

        msg.append("}* ");
        return msg.toString();
    }

    public String getModifiersValues(Command command) {
        StringBuilder values = new StringBuilder();
        command.modifiers.forEach(m -> values.append(" ").append(m.commandSign).append("(").append(m.mod).append(")"));
        return values.toString();
    }

}
