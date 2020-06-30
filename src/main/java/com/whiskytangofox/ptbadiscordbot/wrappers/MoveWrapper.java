package com.whiskytangofox.ptbadiscordbot.wrappers;

import java.util.ArrayList;

public class MoveWrapper {

    public final String name;
    public String text;
    public String stat;
    private final ArrayList<MoveWrapper> secondaryMoves = new ArrayList<MoveWrapper>();

    public MoveWrapper(String moveName, String moveText){
        name = moveName;
        text = moveText;
        stat = getMoveStat(moveText);
    }

    private String getMoveStat(String text){
        String[] stats = {"str", "dex", "con", "int", "wis", "cha"};
        String result = null;
        for (String stat : stats){
            if (text.toLowerCase().contains("roll +"+stat)){
                if (result == null){
                    result = stat;
                } else {
                    return null;
                }
            }
        }
        return result;
    }

    public void appendSecondaryMove(MoveWrapper secondaryMove){
        secondaryMoves.add(secondaryMove);
        String overrideStat = getMoveStat(secondaryMove.text);
        if(overrideStat != null){
            this.stat = overrideStat;
        }
        text = text + System.lineSeparator() + secondaryMove.text;
    }

}
