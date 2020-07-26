package com.whiskytangofox.ptbadiscordbot;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.whiskytangofox.ptbadiscordbot.App.logger;

public abstract class ChannelInstance {

    public final Guild guild;
    public final MessageChannel channel;
    protected final boolean debugLog;

    public ChannelInstance(Guild guild, MessageChannel channel, boolean debugMode) {
        this.guild = guild;
        this.channel = channel;
        this.debugLog = debugMode;
    }

    public abstract void OnMessageReceived(MessageReceivedEvent event);

    public void sendGameMessage(String msg) {
        if (channel != null) {
            channel.sendMessage(msg).queue();
        } else {
            logger.info("Test result for sendGameMessage: " + msg);
        }
    }

    public void sendDebugMsg(String msg) {
        if (debugLog) {
            sendGameMessage(msg);
        }
    }

}
