package com.whiskytangofox.ptbadiscordbot.DataObjects;

import java.util.ArrayList;


public class MoveBuilder {

    private final ArrayList<String> builder = new ArrayList<String>();

    public String overrideName = null;
    public String overrideText = null;
    public boolean isList = false;

    public MoveBuilder addLine() {
        builder.add("");
        return this;
    }

    public MoveBuilder addLine(String string) {
        builder.add(string);
        return this;
    }

    public String get(int index){
        return builder.get(index);
    }

    public void set(int index, String string){
        builder.set(index, string);
    }

    public void extend(int index, String string){
        builder.set(index, builder.get(index).isBlank() ? string : builder.get(index) + " " + string);
    }

    public Move getMove() {
        String moveName;
        if (overrideName != null) {
            moveName = overrideName;
        } else {
            moveName = builder.get(0);
            builder.remove(0);
        }

        StringBuffer moveText = new StringBuffer("**" + moveName + "**" + System.lineSeparator());
        if (overrideText != null) {
            moveText.append(overrideText);
        } else {
            for (int i = 0; i < builder.size(); i++) {
                if (i > 0) {
                    moveText.append(System.lineSeparator());
                }
                moveText.append(builder.get(i));
            }
        }

        Move move = new Move(moveName, moveText.toString());
        return move;
    }

    public int size(){
        return builder.size();
    }


}
