package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;

import java.util.ArrayList;

public class Playbook {
    public String player;
    public String title;
    public HashMapIgnoreCase<CellRef> stats = new HashMapIgnoreCase<CellRef>();
    public HashMapIgnoreCase<CellRef> stat_penalties = new HashMapIgnoreCase<CellRef>();
    public HashMapIgnoreCase<ArrayList<CellRef>> resources = new HashMapIgnoreCase<ArrayList<CellRef>>();
    public HashMapIgnoreCase<String> moveOverrideDice = new HashMapIgnoreCase<String>();
    public PatriciaTrieIgnoreCase<MoveWrapper> moves = new PatriciaTrieIgnoreCase<MoveWrapper>();
    public String tab;

    public Playbook(String tab){
        this.tab = tab;
    }

    public boolean isValid(){
        return player != null && !player.isBlank() && !player.contains("<");
    }

}
