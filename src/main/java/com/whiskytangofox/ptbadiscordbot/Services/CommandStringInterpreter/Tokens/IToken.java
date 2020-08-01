package com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Tokens;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;

import java.io.IOException;

public interface IToken {

    boolean matchesParameter(Playbook book, String string, int index);

    void execute(Playbook book, Command command, String string) throws IOException, DiscordBotException;

}
