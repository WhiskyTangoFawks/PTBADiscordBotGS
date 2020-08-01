package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;

public class DisadvantageToken implements IToken {
    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return "dis".equalsIgnoreCase(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        if (command.dice.size() == 0) {
            command.setDefaultDice();
        }
        command.dice.get(command.dice.size() - 1).dis = true;
    }
}
