package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.Playbook;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SheetReader {

    private final Game game;

    public SheetReader(Game game) {
        this.game = game;
    }

    public static Logger logger = LoggerFactory.getLogger(SheetReader.class);

    public static String registered_msg = "Registered playbook for ";
    public static String start_load_msg = "Starting load for playbook ";

    enum Metadata {new_playbook, discord_name, basic_move, playbook_move, stat, stat_penalty, resource, default_dice}

    public void parseSheet(RangeWrapper sheet) {
        Playbook playbook = null;

            for (int i = sheet.firstCell.getColumnInt(); i < sheet.lastCell.getColumnInt() + 1; i++) {
                for (int j = sheet.firstCell.getRow(); j < sheet.lastCell.getRow() + 1; j++) {

                    try {
                        if (sheet.getNote(i, j) != null && !sheet.getNote(i, j).isBlank()) {
                            String note = sheet.getNote(i, j).toLowerCase().replace(" ", "");
                            MoveBuilder builder;
                            MoveWrapper move;
                            String statName;

                            switch (Metadata.valueOf(note.split(":")[0])) {
                                case basic_move:
                                    builder = parseMove(sheet, i, j);
                                    if (builder != null) {
                                        move = builder.getMove();
                                        game.basicMoves.put(move.name, move);
                                        game.sendDebugMsg("Loaded basic move: "+ move.name);
                                    }
                                    break;
                                case playbook_move:
                                    builder = parseMove(sheet, i, j);
                                    if (builder != null) {
                                        move = builder.getMove();
                                        playbook.moves.put(move.name, move);
                                        game.sendDebugMsg("Loaded playbook move: "+ move.name);
                                    }
                                    break;
                                case stat:
                                    statName = note.replace(Metadata.stat.name() + ":", "").toLowerCase();
                                    playbook.stats.put(statName, new CellRef(i, j));
                                    game.sendDebugMsg("Loaded stat: "+ statName);
                                case stat_penalty:
                                    String stats = note.replace(Metadata.stat_penalty.name() + ":", "");
                                    String statNames[] = stats.split(",");
                                    for (String stat : statNames) {
                                        playbook.stat_penalties.put(stat, new CellRef(i, j));
                                        game.sendDebugMsg("Loaded stat penalty: "+ stat);
                                    }
                                    break;
                                case resource:
                                    String resourceName = note.replace(Metadata.resource.name() + ":", "").toLowerCase();
                                    if (playbook.resources.containsKey(resourceName)){
                                        playbook.resources.get(resourceName).add(new CellRef(i, j));
                                    } else {
                                        ArrayList<CellRef> list = new ArrayList<CellRef>();
                                        list.add(new CellRef(i, j));
                                        playbook.resources.put(resourceName, list);
                                    }
                                    game.sendDebugMsg("Loaded resource: "+ resourceName);
                                    break;
                                case default_dice:
                                    String value = sheet.getValue(i, j);
                                    for (String parameter : value.split(" ")) {
                                        if (ParsedCommand.isDieNotation(parameter)) {
                                            playbook.moveOverrideDice.put(note.split(":")[1], parameter);
                                            game.sendDebugMsg("Loaded default dice for "+ note.split(":")[1] + " : " + parameter);
                                        }
                                    }
                                    break;
                                case discord_name:
                                    playbook.player = sheet.getValue(i, j).split("#")[0];
                                    game.sendDebugMsg("@" +sheet.getValue(i, j) + " assigned to playbook ");
                                    break;
                                case new_playbook:
                                    checkAndRegisterPlayBook(playbook);
                                    playbook = new Playbook(sheet.tab);
                                    playbook.title = sheet.getValue(i, j);
                                    game.sendDebugMsg(start_load_msg + playbook.title);
                                    break;
                                default:
                                    game.sendGameMessage("Unable to parse note: " + note);
                                    break;
                            }


                        }
                    } catch (IllegalArgumentException e) {
                        CellRef cell = new CellRef(i, j);
                        String emsg = "Unable to parse notation found in cell " + cell.getCellRef() + ", note= " + sheet.getNote(i, j);
                        logger.error(emsg);
                        game.sendGameMessage(emsg);
                        e.printStackTrace();
                    } catch (Throwable e){
                        String ref = new CellRef(i, j).getCellRef();
                        String emsg = e.toString() + " reading: "+ sheet.tab + "!"+ref;
                        game.sendGameMessage(emsg);
                        logger.error(emsg);
                        e.printStackTrace();
                    }
                }
            }
            checkAndRegisterPlayBook(playbook);

    }

    public MoveBuilder parseMove(RangeWrapper sheet, int i, int j) {
        String note = sheet.getNote(i, j);
        Boolean isList = false;
        if (sheet.getNote(i, j) != null && !sheet.getNote(i, j).isBlank()){
            String noteName = null;
            String noteText = null;
            for (String parameter : note.split(":")) {
                String tag = parameter.split("=")[0];
                if (tag.equalsIgnoreCase("name")) {
                    noteName = parameter.split("=")[1];
                } else if (tag.equalsIgnoreCase("text")) {
                    noteText =  parameter.split("=")[1];
                } else if (tag.equalsIgnoreCase("list")){
                    isList = true;
                }
            }
          if (noteName != null || noteText != null){
              if (isList){ throw new IllegalArgumentException("list moves cannot be combined with override text");}
              return new MoveBuilder().addLine(noteName != null ? noteName : sheet.getValue(i, j))
                      .addLine(noteText != null ? noteText : sheet.getValue(i, j));
          }
        }

        MoveBuilder builder = new MoveBuilder();
        boolean breakColumn = false;
        for (int k = 0; !breakColumn; k++) {
            builder.addLine();

            boolean enabled = true;
            boolean breakRow = false;

            for (int l = 0; !breakRow; l++) {

                String value = sheet.getValue(i + l, j + k);
                if (null == value || value.isEmpty()) {
                    breakRow = true;
                } else if ("true".equalsIgnoreCase(value)) {
                    enabled = true;
                } else if ("false".equalsIgnoreCase(value)) {
                    if (builder.get(k).isEmpty()) {
                        builder.set(k, " ");
                    }
                    enabled = false;
                } else if (enabled) {
                    builder.extend(k, value);
                }
            }
            if (builder.get(k).isEmpty() || (!isList && k > 0)) {
                breakColumn = true;
            }
        }
        //return null if no title has been set
        return builder.get(0).isBlank() ? null : builder;
    }

    public void checkAndRegisterPlayBook(Playbook book) {
        if (book != null && book.isValid()) {
            if (game.guild != null) { //if guild is null we are testing - TODO maybe should mock guild?
                List<Member> members = game.guild.getMembersByEffectiveName(book.player, true);
                //TODO - handle name conflicts
                if (members.size() == 1) {
                    String mention = members.get(0).getAsMention();
                    game.sendGameMessage(mention + " registered playbook *" + book.title + "*");
                } else if (members.size() == 0) {
                    game.sendGameMessage("Playbook loaded, but unable find Discord Channel Member " + book.player);
                } else {
                    game.sendGameMessage("Playbook loaded, but multiple matching Discord Channel Members found for " + book.player);
                }
            }
            game.playbooks.put(book.player, book);
        }
    }

    public MoveBuilder parseMoveFromNote(String note) {
        MoveBuilder builder = new MoveBuilder();
        String name = null;
        String text = null;
        for (String parameter : note.split(":")) {
            String [] split = parameter.split("=");
            if (parameter.split("=")[0].equalsIgnoreCase("name")) {
                name = parameter.split("=")[1];
            } else if (parameter.split("=")[0].equalsIgnoreCase("text")) {
                text =  parameter.split("=")[1];
            }
        }
        builder.addLine();
        builder.set(0, name);
        builder.addLine();
        builder.set(1, text);
        return builder;
    }


}