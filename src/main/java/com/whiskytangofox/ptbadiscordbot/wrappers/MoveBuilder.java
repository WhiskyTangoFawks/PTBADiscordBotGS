package com.whiskytangofox.ptbadiscordbot.wrappers;

import java.util.ArrayList;


public class MoveBuilder {

    private ArrayList<String> builder = new ArrayList<String>();

    public MoveBuilder addLine(){
        builder.add("");
        return this;
    }

    public MoveBuilder addLine(String string){
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

    public boolean isValid(){
            return builder.size() > 1 &&
                    !builder.get(0).isBlank() &&
                    !builder.get(1).isBlank();
    }

    public MoveWrapper getMove(){
        StringBuffer moveText = new StringBuffer();
        for (int i = 0; i < builder.size(); i++){
            if (i > 0){
                moveText.append(System.lineSeparator());
            }
            if (i == 0){
                moveText.append("**" + builder.get(i)+ "**");
            } else {
                moveText.append(builder.get(i));
            }

        }

        MoveWrapper move = new MoveWrapper(builder.get(0), moveText.toString());
        return move;
    }

    public int size(){
        return builder.size();
    }


}
