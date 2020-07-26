package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.App;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {

    static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final Random random = new Random();

    public static String roll(ArrayList<com.whiskytangofox.ptbadiscordbot.DataObjects.Dice> dice, int mod, String stat, int statMod, boolean failMsg) {
        String msg = "*Rolled*";
        ArrayList<Integer> rolls = new ArrayList<Integer>();

        for (com.whiskytangofox.ptbadiscordbot.DataObjects.Dice wrapper : dice) {
            if (wrapper.adv || wrapper.dis) {
                wrapper.num++;
            }
        }
        msg = msg + " " + getNotation(dice, mod, stat, statMod) + " :: ";
        for (com.whiskytangofox.ptbadiscordbot.DataObjects.Dice wrapper : dice) {

            for (int i = 0; i < wrapper.num; i++) {
                rolls.add(random.nextInt(wrapper.size) + 1);
                String emoji = "[" + rolls.get(rolls.size() - 1) + "]";
                msg = msg + emoji + ", ";
            }
            if (wrapper.adv || wrapper.dis) {
                msg = dropLowAndStrikeThru(rolls, msg, wrapper.adv, wrapper.dis);
            }
        }
       msg = msg.substring(0,msg.length()-2); //cut the last ", "

       int sum = getSum(rolls) + mod + statMod;

       String modSign = statMod < 1 ? "" : "+";

       if (stat != null){
           msg = msg+ "  +(" + modSign + statMod +")";
       }

       modSign = mod < 1 ? "" : "+";
       if (mod != 0){
           msg = msg+ " " + modSign + mod;
       }
       msg = msg+"  =  "+sum;
       if (failMsg && sum < 7){
           msg = msg + System.lineSeparator() + "*Don't forget to mark EXP on a 6-*";
       }
       return msg;
    }

    private static int getSum(ArrayList<Integer> nums){
        int sum = 0;
        for (Integer num : nums) {
            sum = sum + num;
        }
        return sum;
    }

    private static String dropLowAndStrikeThru(ArrayList<Integer> rolls, String msg, boolean adv, boolean dis){
        String drop = null;
        String replacement = null;
        Collections.sort(rolls);

        if (adv) {
            drop = "["+rolls.get(0)+"]";
            replacement = "~~["+ rolls.get(0) + "]~~";
            rolls.remove(0);
        }
        else if (dis) {
            drop = "["+rolls.get(rolls.size()-1)+"]";
            replacement = "~~["+ rolls.get(rolls.size()-1) + "]~~";
            rolls.remove(rolls.size()-1);
        }
         return replaceLast(msg, drop, replacement);
    }

    public static String replaceLast(String string, String toReplace, String replacement) {
        int pos = string.lastIndexOf(toReplace);
        if (pos > -1) {
            return string.substring(0, pos)
                    + replacement
                    + string.substring(pos + toReplace.length());
        } else {
            return string;
        }
    }

    private static String getNotation(ArrayList<com.whiskytangofox.ptbadiscordbot.DataObjects.Dice> dice, int mod, String stat, Integer statMod) {
        String msg = "*{";
        for (com.whiskytangofox.ptbadiscordbot.DataObjects.Dice die : dice) {
            String spaceOrNot = msg == "*{" ? "" : ", ";
            msg = msg + spaceOrNot + die.getNotation();
        }
        if (stat != null) {
            msg = msg + " +(" + stat + ")";
        }
        if (mod != 0) {
            String modSign = mod < 1 ? "" : "+";
            msg = msg + " " + modSign + mod;
        }
        msg = msg + "}* ";
        return msg;
    }

}
