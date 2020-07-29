package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;

public class RollDefaultToken implements IToken {

    String defaultRollCommand = "roll";

    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return index == 0 && defaultRollCommand.equalsIgnoreCase(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        command.doRoll = true;
    }
}
