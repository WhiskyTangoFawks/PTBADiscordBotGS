package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;

public class Playbook {
    public String player;
    public String title;
    public HashMapIgnoreCase<CellRef> stats = new HashMapIgnoreCase<CellRef>();
    public HashMapIgnoreCase<CellRef> stat_penalties = new HashMapIgnoreCase<CellRef>();
    public HashMapIgnoreCase<ResourceWrapper> resources = new HashMapIgnoreCase<>();
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
