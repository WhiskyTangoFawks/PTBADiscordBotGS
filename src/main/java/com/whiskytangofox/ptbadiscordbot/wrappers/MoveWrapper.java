package com.whiskytangofox.ptbadiscordbot.wrappers;

public class MoveWrapper {

    public final String name;
    public String text;
    public String stat;


    public MoveWrapper(String moveName, String moveText){
        name = moveName;
        text = moveText;
    }

    public MoveWrapper(String moveName, String moveText, String stat){
        name = moveName;
        text = moveText;
        this.stat = stat;
    }

     public MoveWrapper getModifiedCopy(MoveWrapper secondaryMove){
        MoveWrapper copy = new MoveWrapper(this.name, this.text,
                secondaryMove.stat == null ? this.stat : secondaryMove.stat);

        copy.text = copy.text + System.lineSeparator() + secondaryMove.text;
        return copy;
    }

    public String getReferenceMoveName(){
        return this.name.toLowerCase().replace(" ", "");
    }
}
