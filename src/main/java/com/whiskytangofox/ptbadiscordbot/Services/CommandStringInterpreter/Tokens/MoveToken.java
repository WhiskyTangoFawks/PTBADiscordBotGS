package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;

public class MoveToken implements IToken {

    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return book.isMove(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        try {
            if (command.move != null) {
                //If a move is already assigned, this will prevent it from being overwritten
                //throw new IllegalArgumentException("Move already assigned)
            }
            command.move = book.getMove(string);
        } catch (KeyConflictException e) {
            //This checks isMove during matchesParameter, so it shouldnt ever be a problem
            e.printStackTrace();
        }
    }
}
