package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.App;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class DiceService {

    static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final Random random = new Random();

    public String roll(Command command) {
        String msg = "*Rolled*";
        ArrayList<Integer> rolls = new ArrayList<Integer>();

        for (com.whiskytangofox.ptbadiscordbot.DataObjects.Dice wrapper : command.dice) {
            if (wrapper.adv || wrapper.dis) {
                wrapper.num++;
            }
        }
        msg = msg + " " + getNotation(command) + " :: ";
        for (com.whiskytangofox.ptbadiscordbot.DataObjects.Dice wrapper : command.dice) {

            for (int i = 0; i < wrapper.num; i++) {
                rolls.add(random.nextInt(wrapper.size) + 1);
                String emoji = "[" + rolls.get(rolls.size() - 1) + "]";
                msg = msg + emoji + ", ";
            }
            if (wrapper.adv || wrapper.dis) {
                msg = dropLowAndStrikeThru(rolls, msg, wrapper.adv, wrapper.dis);
            }
        }
        msg = msg.substring(0, msg.length() - 2); //cut the last ", "

        int sum = getSum(rolls) + command.mod + (command.stat != null ? command.stat.modStat : 0);

        String modSign = (command.stat != null ? command.stat.modStat : 0) < 1 ? "" : "+";

        if (command.stat != null) {
            msg = msg + "  +(" + modSign + (command.stat != null ? command.stat.modStat : 0) + ")";
        }

        modSign = command.mod < 1 ? "" : "+";
        if (command.mod != 0) {
            msg = msg + " " + modSign + command.mod;
        }
        msg = msg + "  =  " + sum;
        if (command.failMsg && sum < 7) {
            msg = msg + System.lineSeparator() + "*Don't forget to mark EXP on a 6-*";
        }
        return msg;
    }

    private int getSum(ArrayList<Integer> nums) {
        int sum = 0;
        for (Integer num : nums) {
            sum = sum + num;
        }
        return sum;
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

    private String getNotation(Command command) {
        String msg = "*{";
        for (com.whiskytangofox.ptbadiscordbot.DataObjects.Dice die : command.dice) {
            String spaceOrNot = msg == "*{" ? "" : ", ";
            msg = msg + spaceOrNot + die.getNotation();
        }
        if (command.stat != null) {
            msg = msg + " +(" + command.stat.stat + ")";
        }
        if (command.mod != 0) {
            String modSign = command.mod < 1 ? "" : "+";
            msg = msg + " " + modSign + command.mod;
        }
        msg = msg + "}* ";
        return msg;
    }

}
