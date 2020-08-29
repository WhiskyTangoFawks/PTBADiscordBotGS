package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.GameSettings;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.Command;
import com.whiskytangofox.ptbadiscordbot.Services.CommandStringInterpreter.CommandStringInterpreterService;
import com.whiskytangofox.ptbadiscordbot.Services.DiceService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.util.List;

import static com.whiskytangofox.ptbadiscordbot.App.logger;

public class GameGoogle extends AbstractGameGoogleSheetMethods {

    public final Guild guild;
    public final MessageChannel channel;
    protected final boolean debugLog;

    protected CommandStringInterpreterService interpreter = new CommandStringInterpreterService();
    protected DiceService rollerService = new DiceService();

    public GameGoogle(Guild guild, MessageChannel channel, String sheetID, Boolean debug) throws IOException {
        super(sheetID);
        this.guild = guild;
        this.channel = channel;
        this.debugLog = debug;
        if (sheetID != null) {
            initGame();
            postInit();
        }
    }

    public String OnMessageReceived(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentDisplay();
        String player = event.getAuthor().getName();
        String c = settings.get(GameSettings.KEY.commandchar);
        if (c == null) {
            sendDebugMsg("Missing game property: commandchar");
            throw new NullPointerException("Missing game property: commandchar");
        }
        try {
            if (msg.startsWith(c)) {// /alias string
                msg = msg.toLowerCase().replace(c, "");

                return runCommand(event.getAuthor().getName(), msg);
            }

        } catch (Throwable e) {
            sendGameMsg("Exception: " + e.toString());
            e.printStackTrace();
            return (e.toString());
        }
        return "No command character detected";
    }

    public String runCommand(String player, String msg) throws DiscordBotException, KeyConflictException, IOException {
        Playbook book = playbooks.getPlaybook(player);
        Command command = interpreter.interpretCommandString(book, msg);
        String response = "";
        if (command.move != null) {
            response = response + command.move.text + System.lineSeparator();
        }
        if (command.doRoll) {
            response = response
                    + " "
                    + rollerService.roll(command);
        } else if (command.resource != null) {
            int sum = command.modifiers.stream().mapToInt(m -> m.mod).sum();
            response = response + book.modifyResource(command.resource, sum).getDescriptiveResult();
        }
        sendGameMsg(player, response);
        return (response);
    }

    public void reloadGame() throws IOException {
        initGame();
        postInit();
    }

    public void sendGameMsg(String msg) {
        if (channel != null) {
            channel.sendMessage(msg).queue();
        } else {
            logger.info("Test result for sendGameMessage: " + msg);
        }
    }

    @Override
    public void sendGameMsg(String player, String msg) {
        if (guild == null) {
            return; //if no channel set, we are running tests
        }

        List<Member> members = guild.getMembersByEffectiveName(player, true);
        if (members.size() == 1) {
            String mention = members.get(0).getAsMention();
            sendGameMsg(mention + " " + msg);
        } else if (members.size() == 0) {
            throw new IllegalArgumentException("multiple players found in channel for discord name " + player);
        } else {
            throw new IllegalArgumentException("No players found in channel for discord name " + player);
        }
    }

    public void sendDebugMsg(String msg) {
        if (debugLog) {
            sendGameMsg(msg);
        }
    }

}
