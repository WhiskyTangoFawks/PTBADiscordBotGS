package com.whiskytangofox.ptbadiscordbot.DataObjects;

import java.util.*;
import java.util.stream.Collectors;

public class Move {

    public final String name;
    public String text;
    public ArrayList<String> modifying = new ArrayList<String>();

    public Move(String moveName, String moveText) {
        name = moveName;
        text = moveText;
    }

    public Move getModifiedCopy(Move secondaryMove) {
        Move copy = new Move(this.name, this.text);

        copy.text = copy.text + System.lineSeparator()
                + System.lineSeparator()
                + secondaryMove.text;
        return copy;
    }

    public String getReferenceMoveName() {
        String result = this.name.toLowerCase().replace(" ", "");
        if (result.contains("(")) {
            result = result.substring(0, result.indexOf("("));
        }
        return result;
    }

    public String getMoveStat(Collection<String> stats) {
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

    public Collection<String> getParentMoves() {
        HashSet<String> set = new HashSet<String>();
        if (name.contains("(")) {
            String list = name.substring(name.indexOf("(") + 1, name.indexOf(")"));
            //list = list.replace(" ", "").toLowerCase();
            return Arrays.stream(list.split(",")).collect(Collectors.toList());
        }
        return set;
    }

}
