package com.whiskytangofox.ptbadiscordbot.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class MoveWrapper {

    public final String name;
    public String text;
    public ArrayList<String> modifying = new ArrayList<String>();

    public MoveWrapper(String moveName, String moveText){
        name = moveName;
        text = moveText;
    }

     public MoveWrapper getModifiedCopy(MoveWrapper secondaryMove){
        MoveWrapper copy = new MoveWrapper(this.name, this.text);

        copy.text = copy.text + System.lineSeparator() + secondaryMove.text;
        return copy;
    }

    public String getReferenceMoveName(){
        return this.name.toLowerCase().replace(" ", "");
    }

    public String getMoveStat(Collection<String> stats){
        for (int i = modifying.size()-1; i >-1; i--){
            List<String> list = getContainedInText(text, "roll+", stats.toArray(new String[stats.size()]));
            if(list.size() == 1){
                return list.get(0);
            }
        }
        List<String> list = getContainedInText(text, "roll+", stats.toArray(new String[stats.size()]));
        if(list.size() == 1){
            return list.get(0);
        }
        return null;
    }

    private List<String> getContainedInText(String text, String prefix, String... var){
        final String preparedText = text.toLowerCase().replace(" ", "");
        final String preparedPrefix = prefix.toLowerCase().replace(" ", "");
        return Arrays.stream(var).map(v -> v.toLowerCase().replace(" ", ""))
                .filter(v -> preparedText.contains(preparedPrefix+v))
                .collect(Collectors.toList());
    }
}
