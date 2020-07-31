package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;

public class IntegerModifierToken implements IToken {
    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        command.addModifier(string, Command.TYPE.INTEGER, string);
    }
}
