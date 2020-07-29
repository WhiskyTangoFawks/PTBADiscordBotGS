package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Command;

import java.io.IOException;

public class StatToken implements IToken {

    @Override
    public boolean matchesParameter(Playbook book, String string, int index) {
        if (string.startsWith("+") || string.startsWith("-")) {
            return book.isStat(string.substring(1));
        }
        return book.isStat(string);
    }

    @Override
    public void execute(Playbook book, Command command, String string) throws IOException, DiscordBotException {
        if (string.startsWith("+")) {
            command.stat = book.getStat(string.substring(1));
        } else if (string.startsWith("-")) {
            command.stat = book.getStat(string.substring(1));
            command.stat.modStat *= -1;
        } else {
            command.stat = book.getStat(string);
        }
        if (command.stat.isDebilitated) {
            command.rawToParse.add(command.stat.debilityTag);
        }
    }
}
