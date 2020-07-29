package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;

public class ResourceToken implements IToken {

    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return index == 0 && book.isResource(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        command.resource = string;
    }
}
