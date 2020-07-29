package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Dice;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;

public class DiceNotationToken implements IToken {

    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        return string.matches(".*\\dd\\d.*");
    }

    @Override
    public void execute(Playbook book, Command command, String string) {
        command.doRoll = true;
        String[] rollParameters = string.split("d");
        int num = 0;
        int size = 0;
        if (rollParameters.length == 2) {
            num = Integer.parseInt(rollParameters[0]);
            size = Integer.parseInt(rollParameters[1]);
            command.dice.add(new Dice(num, size));
        }
    }
}
