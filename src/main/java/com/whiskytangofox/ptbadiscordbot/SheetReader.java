package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.Playbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                                move = builder.getMove();
                                game.basicMoves.put(move.name, move);
                                break;
                            case playbook_move:
                                builder = parseMove(sheet, i, j);
                                move = builder.getMove();
                                playbook.moves.put(move.name, move);
                                for (String moveName : builder.getModifiesMoves()) {
                                    MoveWrapper copy = game.getMove(playbook.player, moveName).getModifiedCopy(move);
                                    playbook.moves.put(copy.name, copy);
                                }
                                break;
                            case stat:
                                statName = note.replace(Metadata.stat.name() + ":", "").toLowerCase();
                                playbook.stats.put(statName, new CellRef(i, j));
                            case stat_penalty:
                                String stats = note.replace(Metadata.stat_penalty.name() + ":", "");
                                String statNames[] = stats.toLowerCase().split(",");
                                for (String stat : statNames) {
                                    playbook.stat_penalties.put(stat, new CellRef(i, j));
                                }
                                break;
                            case resource:
                                String resourceName = note.replace(Metadata.resource.name() + ":", "").toLowerCase();
                                playbook.resources.put(resourceName, new CellRef(i, j));
                            case default_dice:
                                String value = sheet.getValue(i, j);
                                for (String parameter : value.split(" ")) {
                                    if (ParsedCommand.isDieNotation(parameter)) {
                                        playbook.moveOverrideDice.put(note.split(":")[1], parameter);
                                    }
                                }
                                break;
                            case discord_name:
                                playbook.player = sheet.getValue(i, j).split("#")[0];
                                break;
                            case new_playbook:
                                checkAndRegisterPlayBook(playbook);
                                logger.info(start_load_msg + sheet.getValue(i, j));
                                playbook = new Playbook(sheet.tab);
                                break;
                            default:
                                game.sendGameMessage("Unable to parse note: " + note);
                                break;
                        }


                    }
                } catch (Throwable e) {
                    CellRef cell = new CellRef(i, j);
                    String emsg = "Unexpected exception trying to read cell " + cell.getCellRef() + ", note= "+ sheet.getNote(i, j);
                    logger.error(emsg);
                    game.sendGameMessage(emsg);
                    e.printStackTrace();
                }
            }
        }
        checkAndRegisterPlayBook(playbook);
    }

    public MoveBuilder parseMove(RangeWrapper sheet, int i, int j) {
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
            if (builder.get(k).isEmpty()) {
                breakColumn = true;
            }
        }
        //return null if no title has been set
        return builder.get(0).isBlank() ? null : builder;
    }

    public void checkAndRegisterPlayBook(Playbook book) {
        if (book != null && book.isValid()) {
            logger.info(registered_msg + book.player);
            game.playbooks.put(book.player, book);
        }
    }




}
