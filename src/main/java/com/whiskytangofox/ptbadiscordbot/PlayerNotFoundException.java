package com.whiskytangofox.ptbadiscordbot;

public class PlayerNotFoundException extends DiscordBotException {
    public PlayerNotFoundException(String message) {
        super(message);
    }
}
