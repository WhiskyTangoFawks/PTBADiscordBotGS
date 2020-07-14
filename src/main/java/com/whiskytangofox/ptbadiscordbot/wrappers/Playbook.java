package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;

import java.util.HashMap;

public class Playbook {
    public String player;
    public HashMap<String, CellRef> stats = new HashMap<String, CellRef>();
    public HashMap<String, CellRef> stat_penalties = new HashMap<String, CellRef>();
    public HashMap<String, CellRef> resources = new HashMap<String, CellRef>();
    public HashMap<String, String> moveOverrideDice = new HashMap<String, String>();
    public PatriciaTrieIgnoreCase<MoveWrapper> moves = new PatriciaTrieIgnoreCase<MoveWrapper>();
    public String tab;

    public Playbook(String tab){
        this.tab = tab;
    }

    public boolean isValid(){
        return player != null && !player.isBlank() && !player.contains("<");
    }

}
