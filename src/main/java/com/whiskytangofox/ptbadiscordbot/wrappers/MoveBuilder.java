package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.Game;

import java.util.*;
import java.util.stream.Collectors;

public class MoveBuilder {


    private ArrayList<String> builder = new ArrayList<String>();

    public void addLine(){
        builder.add("");
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

    public MoveWrapper getMoveForGame(Game game){
        StringBuffer moveText = new StringBuffer();
        for (int i = 0; i < builder.size(); i++){
            if (i > 0){
                moveText.append(System.lineSeparator());
            }
            moveText.append(builder.get(i));
        }

        String stat = getRollStatFromText(moveText.toString(), game.getAllStats());
        MoveWrapper move = new MoveWrapper(builder.get(0), moveText.toString(), stat);
        return move;
    }

    public void addNote(String note){
        if (note != null && !note.isBlank()){
            //TODO implement a list parser when I have more uses for metadata

        }
    }

    public Collection<String> getModifiesMoves(){
        HashSet<String> set = new HashSet<String>();
        String name = builder.get(0);
        if (name.contains("(")){
            String list = name.substring(name.indexOf("(")+1,name.indexOf(")"));
            for (String basicMoveName : list.split(",")){
                set.add(basicMoveName);
            }
        }
        return set;
    }

    public int lastIndex(){
        return this.builder.size()-1;
    }

    public List<String> getContainedInText(String text, String prefix, String... var){
        final String preparedText = text.toLowerCase().replace(" ", "");
        final String preparedPrefix = prefix.toLowerCase().replace(" ", "");
        return Arrays.stream(var).map(v -> v.toLowerCase().replace(" ", ""))
                .filter(v -> preparedText.contains(preparedPrefix+v))
                .collect(Collectors.toList());
    }

    public String getRollStatFromText(String text, Collection<String> gameStats){
        List<String> list = getContainedInText(text, "roll+", gameStats.toArray(new String[gameStats.size()]));
        if(list.size() == 1){
            return list.get(0);
        }
        return null;
    }

}
