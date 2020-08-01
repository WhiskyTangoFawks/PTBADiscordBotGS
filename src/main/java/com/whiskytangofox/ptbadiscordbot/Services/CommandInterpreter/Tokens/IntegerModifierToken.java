package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;
import com.whiskytangofox.ptbadiscordbot.Utils;

public class IntegerModifierToken implements IToken {
    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return Utils.isInteger(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        command.addModifier(string, Command.TYPE.INTEGER, string);
    }
}
