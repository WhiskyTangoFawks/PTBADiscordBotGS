package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.StatResponse;
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
        if (command.hasStat()) {
            throw new IllegalArgumentException("Attempted to assign a stat twice");
        }
        StatResponse stat;
        String sign = "";
        if (string.startsWith("+") || string.startsWith("-")) {
            if (string.startsWith("-")) {
                sign = "-";
            }
            string = string.substring(1);
        }
        stat = book.getStat(string);
        command.addModifier(string, Command.TYPE.STAT, sign + stat.modStat);
        if (stat.isDebilitated) {
            command.rawToParse.add(stat.debilityTag);
        }
    }
}
