package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.wrappers.DieWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Dice {

    static final Logger logger = LoggerFactory.getLogger(App.class);
    private static final Random random = new Random();

   public static String roll(ArrayList<DieWrapper> dice, int mod, String stat, int statMod){
       String msg="Rolled";
       ArrayList<Integer> rolls = new ArrayList<Integer>();

       for (DieWrapper wrapper : dice) {
           if (wrapper.adv || wrapper.dis) {
               wrapper.num++;
           }
       }

       for (DieWrapper wrapper : dice) {
           msg = msg + " " + getNotation(dice, mod, stat, statMod);
           for (int i = 0; i < wrapper.num; i++) {
               rolls.add(random.nextInt(wrapper.size) + 1);
               msg = msg + rolls.get(rolls.size()-1)+ ", ";
           }
           if (wrapper.adv || wrapper.dis) {
               msg = dropLowAndStrikeThru(rolls, msg, wrapper.adv, wrapper.dis);
           }
       }
       msg = msg.substring(0,msg.length()-2); //cut the last ", "

       int sum = getSum(rolls) + mod + statMod;

       String modSign = statMod < 1 ? "" : "+";
       if (statMod != 0){
           msg = msg+ " (" + modSign + statMod +")";
       }

       modSign = mod < 1 ? "" : "+";
       if (mod != 0){
           msg = msg+ " " + modSign + mod;
       }
       msg = msg+" = "+sum;
       //TODO - implement exp message ONLY for rolls with a MOVE
       //TODO - implement property to disable reminder for non-PTBA games
       /*if (sum < 7){
           msg = msg + System.lineSeparator() + "Mark EXP on a 6-";
       }*/
       return msg;
    }

    private static int getSum(ArrayList<Integer> nums){
       int sum = 0;
       for (int i = 0; i< nums.size(); i++){
           sum=sum+nums.get(i);
       }
       return sum;
    }

    private static String dropLowAndStrikeThru(ArrayList<Integer> rolls, String msg, boolean adv, boolean dis){
        String drop = null;
        Collections.sort(rolls);

        if (adv) {
            drop = rolls.get(0).toString();
            rolls.remove(0);
        }
        else if (dis) {
            drop = rolls.get(rolls.size()-1).toString();
            rolls.remove(rolls.size()-1);
        }
         return replaceLast(msg, drop, "~~" + drop + "~~");
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

    private static String getNotation(ArrayList<DieWrapper> dice, int mod, String stat, Integer statMod){
        String msg = "[";
        for (DieWrapper die : dice){
            String spaceOrNot = msg == "[" ? "" : " +";
            msg=msg + spaceOrNot + die.getNotation();
        }
        if (stat != null) {
            msg = msg + " +(" + stat+")" ;
        }
        if (mod != 0) {
            String modSign = mod < 1 ? "" : "+";
            msg = msg + " " + modSign + mod;
        }
        msg = msg + "] ";
        return msg;
    }

}
