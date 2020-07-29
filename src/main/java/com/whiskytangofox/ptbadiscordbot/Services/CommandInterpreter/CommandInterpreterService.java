package com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Services.CommandInterpreter.Tokens.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

public class CommandInterpreterService {

    ArrayList<IToken> database;

    public Command interpretCommandString(Playbook book, String raw) throws IOException, DiscordBotException, KeyConflictException {
        Command command = new Command(book, raw);
        while (!command.rawToParse.isEmpty()) {
            List<RawToken> tokenized = tokenizeStringCommand(book, command.rawToParse.poll());
            for (RawToken t : tokenized) {
                t.token.execute(book, command, t.string);
            }
        }
        finalizeCommand(book, command);
        return command;
    }

    protected void finalizeCommand(Playbook book, Command command) throws KeyConflictException, DiscordBotException, IOException {
        if (command.doRoll && command.dice.size() == 0) {
            command.setDefaultDice();
        }
        if (command.move != null && command.stat == null) {
            command.stat = book.getMoveStat(command.move.name);
        }

    }


    protected List<RawToken> tokenizeStringCommand(Playbook book, String string) {
        string = string.replaceAll("\\+", " +");
        string = string.replaceAll("-", " -");
        String[] split = string.split(" ");

        List<RawToken> list = IntStream.range(0, split.length)
                .filter(i -> split[i] != null)
                .filter(i -> !split[i].isBlank())
                .mapToObj(i -> mapToken(book, split[i], i))
                .collect(toList());

        //TODO - clean up the list - deal with potential for move names with spaces

        return list;
    }

    protected RawToken mapToken(Playbook book, String p, int index) {

        List<IToken> tokens = getDatabase().stream()
                .filter(i -> i.matchesParameter(book, p, index))
                .collect(toList());
        if (tokens.size() == 0) {
            throw new IllegalArgumentException("Unrecognized command parameter: " + p);
        }
        return new RawToken(p, tokens.get(0));
    }

    class RawToken {
        protected final String string;
        protected final IToken token;

        RawToken(String string, IToken token) {
            this.string = string;
            this.token = token;
        }
    }

    private ArrayList<IToken> getDatabase() {
        if (database == null) {
            database = new ArrayList<>();
            database.add(new RollDefaultToken());
            database.add(new AdvantageToken());
            database.add(new DisadvantageToken());
            database.add(new IntegerModifierToken());
            database.add(new ResourceToken());
            database.add(new StatToken());
            database.add(new MoveToken());
        }
        return database;
    }

}
