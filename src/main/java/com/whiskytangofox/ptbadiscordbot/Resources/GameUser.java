package com.whiskytangofox.ptbadiscordbot.Resources;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataStructure.HashSetIgnoreCase;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.sort;

@RegisterForReflection
public class GameUser {

    public GameUser(){

    }

    public List<String> moves = new ArrayList();
    public List<String> stats = new ArrayList();

    public GameUser mapPlaybookValues(Playbook book){
        HashSetIgnoreCase<String> set = new HashSetIgnoreCase();
        set.addAll(book.basicMoves.keySet());
        set.addAll(book.moves.keySet());
        moves.addAll(set);
        sort(moves);

        stats.addAll(book.stats.keySet());
        sort(stats);
        return this;
    }

    public static GameUser testUser(){
        GameUser user = new GameUser();
        user.moves.add("Move 1");
        user.moves.add("Move 2");
        user.moves.add("Move 3");
        user.moves.add("Move 4");
        user.moves.add("Move 5");
        user.moves.add("Move 6");
        user.stats.add("Stat 1");
        user.stats.add("Stat 2");
        user.stats.add("Stat 3");
        return user;
    }


}
