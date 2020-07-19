package com.whiskytangofox.ptbadiscordbot.Exceptions;

import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;

public class MissingValueException extends DiscordBotException {
    public MissingValueException(String s) {
        super(s);
    }
}
