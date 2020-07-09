package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.wrappers.DieWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;

import java.io.IOException;
import java.util.ArrayList;

public class ParsedCommand {

    private final String author;
    private final Game game;
    MoveWrapper move = null;
    int mod = 0;
    ArrayList<DieWrapper> dice = new ArrayList<DieWrapper>();
    String rollResult;
    String stat = null;
    int statMod = 0;
    boolean doRoll = false;
    boolean failMsg = false;



    public ParsedCommand(Game game, String author, String command) throws KeyConflictException, IOException, DiscordBotException {
        this.author = author;
        this.game = game;

        if (command != null) { //if command is null, then we are running tests
            splitAndParseCommand(command);
            if (doRoll) {
                rollResult = getRollResults(failMsg);
            }
        }
    }



    public void splitAndParseCommand(String command) throws KeyConflictException {
        command = command.replaceAll("\\+", " ");
        String[] parameters = command.split("( )|/|(?=-)");
        for (int i = 0; i < parameters.length; i++) {
            //Zeroth - check if the command is a roll
            if (i == 0) {
                if ("info".equalsIgnoreCase(parameters[i])){
                    move = new MoveWrapper("Help Info", getInfoMessage());
                }else if ("roll".equalsIgnoreCase(parameters[i])) {
                    doRoll = true;
                } else if (game.isMove(author, parameters[i])) {
                   //skips the rest of the move name when it is written with spaces
                    i = i + getMoveArrayPositions(author, i, parameters);
                    move = game.getMove(author, parameters[i]);
                }
                else {
                    throw new IllegalArgumentException("Unrecognised command " + parameters[i]);
                }
            } else if (parameters[i].equalsIgnoreCase("dis") || parameters[i].equalsIgnoreCase("adv")) {
                parseAdvDis(parameters[i]);
            } else if (isDieNotation(parameters[i])){
                parseDieNotation(parameters[i]);
            } else if (game.isStat(parameters[i])) {
                stat = parameters[i];
            } else if (isInteger(parameters[i])) {
                mod = Integer.parseInt(parameters[i]);
            } else if (move == null && game.isMove(author, parameters[i])) {
                i = i + getMoveArrayPositions(author, i, parameters);
                move = game.getMove(author, parameters[i]);
            } else if (!parameters[i].isBlank()) {
                throw new IllegalArgumentException("Unrecognised argument " + parameters[i]);
            }
        }
    }

    public String getRollResults(Boolean failMsg) throws IOException, DiscordBotException {
            if ((dice.size() == 0)) {
                setDefaultDice();
            }
            if (move != null && stat == null){
                stat = move.stat;
            } //NOT IF
            if (stat != null){
                statMod = game.getStat(author, stat);
            }
            return Dice.roll(dice, mod, stat, statMod, failMsg);
        }


    boolean isDieNotation(String stringToCheck){
        return stringToCheck.matches(".*\\dd\\d.*");
    }

    public void setDefaultDice() throws IOException, PlayerNotFoundException {
        if (move != null && game.sheet_definitions.getProperty("default_"+move.getReferenceMoveName()+"_dice")!= null){
            String cell = game.sheet_definitions.getProperty("default_"+move.getReferenceMoveName()+"_dice");
            String diceNotation = game.getLivePlayerValue(author, cell);
            parseDieNotation(diceNotation);
        } else {
            parseDieNotation(game.sheet_definitions.getProperty("default_system_dice"));
            failMsg = true;
        }
    }

    public void parseDieNotation(String token) {
        String[] rollParameters = token.split("d");

        int num = 0;
        int size = 0;
        if (rollParameters.length == 2) {
            num = Integer.parseInt(rollParameters[0]);
            size = Integer.parseInt(rollParameters[1]);
            dice.add(new DieWrapper(num, size));
        }
    }

    public void parseAdvDis(String token) {
        if (dice.size() == 0) { //if dis/adv specified before dice size, then setup default
            dice.add(new DieWrapper(2, 6));
        }
        if (token.contains("dis")) {
            dice.get(dice.size() - 1).dis = true;
        } else if (token.contains("adv")) {
            dice.get(dice.size() - 1).adv = true;
        }
    }

    private boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getInfoMessage(){
        String r = System.lineSeparator();
        String c = game.sheet_definitions.getProperty("commandchar");
        String infoText = "**"+c+"roll  xdx  +/-Modifier  +Stat  MoveName  adv/dis**" + r +
                "**xdx** - Dice Notation: the number and size of dice to roll, this tag may be included multiple times to roll different dice. When used multiple times, the adv/dis tags should immediately follow the dice to receive the effect, e.g. roll 2d6 adv 1d4. If no dice notation tags are included in a roll command, the default dice (set in the properties tab of the game spreadsheet) will be rolled."+r+r+
                "**+/-Modifier** : any integer to be added to the sum of the rolls"+r+r+
                "**+Stat** : the stat modifier to be used for the roll, the system will get the live value from the spreadsheet (minus any debility penalty)"+r+r+
                "**MoveName**: if a move name or partial move name is included, the text of that move will be printed along with the roll result. If the move includes a single \"roll +STAT\" in it's text, (where STAT is a stat registered in the properties file), a +Stat tag for that stat is added to the roll command if one is not specified. If used as a command without \"roll\", it will print the move text (without rolling)"+r+r+
                "**adv/dis**: roll and additional die, and drop the highest/lowest result";
        return infoText;
    }

    public int getMoveArrayPositions(String player, int startPos, String[] parameters) throws KeyConflictException {
        StringBuffer buffer = new StringBuffer();
        for(int i = startPos;i < parameters.length; i++){
            buffer.append(parameters[i]);
            if (!game.isMove(player, buffer.toString())){
                return i-1-startPos;
            }
        }
        return parameters.length-1-startPos;

    }


}
