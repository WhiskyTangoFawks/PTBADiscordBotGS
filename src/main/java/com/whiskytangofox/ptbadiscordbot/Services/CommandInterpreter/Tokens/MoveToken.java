package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;

import java.io.IOException;

public class MoveToken implements IToken {

    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return book.isMove(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) throws IOException {
        try {
            command.move = book.getMove(string);
        } catch (KeyConflictException e) {
            //This checks isMove during matchesParameter, so it shouldnt ever be a problem
            e.printStackTrace();
        }
    }
}
