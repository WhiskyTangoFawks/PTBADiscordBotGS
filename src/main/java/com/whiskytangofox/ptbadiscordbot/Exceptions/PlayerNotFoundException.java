package com.whiskytangofox.ptbadiscordbot.Exceptions;

import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;

public class PlayerNotFoundException extends DiscordBotException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
