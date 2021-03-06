package com.whiskytangofox.ptbadiscordbot.Services.SheetParser;

import com.whiskytangofox.ptbadiscordbot.AbstractGameGoogleSheetMethods;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers.*;
import com.whiskytangofox.ptbadiscordbot.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SheetParserService {

    public AbstractGameGoogleSheetMethods game;

    public SheetParserService(AbstractGameGoogleSheetMethods game) {
        this.game = game;
    }

    public static Logger logger = LoggerFactory.getLogger(SheetParserService.class);

    public enum PARSER {
        new_playbook(new NewPlaybookParser()),
        discord_name(new DiscordNameParser()),
        basic_move(new BasicMoveParser()),
        playbook_move(new PlaybookMoveParser()),
        stat(new StatParser()),
        resource(new ResourceParser()),
        default_dice(new DefaultDiceParser());

        private final INoteParser parser;

        PARSER(INoteParser parser) {
            this.parser = parser;
        }

        public String parse(SheetParserService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
            return parser.parse(service, sheet, playbook, note, i, j);
        }
    }

    public Playbook playbook = null;

    public void parseSheet(RangeWrapper sheet) {
        for (int i = sheet.firstCell.getColumnInt(); i < sheet.lastCell.getColumnInt() + 1; i++) {
            for (int j = sheet.firstCell.getRow(); j < sheet.lastCell.getRow() + 1; j++) {

                try {
                    if (sheet.getNote(i, j) != null && !sheet.getNote(i, j).isBlank()) {
                        String note = Utils.cleanString(sheet.getNote(i, j));
                        if (note.contains("[") && note.contains("]")) {
                            note = replaceCellReferences(sheet, new CellReference(i, j), note);
                        }
                        PARSER parser = PARSER.valueOf(note.split(";")[0].split("=")[0]);
                        String result = parser.parse(this, sheet, playbook, note, i, j);
                        game.sendDebugMsg("Parsed note " + parser.name() + ":  " + result);
                    }
                    } catch (IllegalArgumentException e) {
                        CellReference cell = new CellReference(i, j);
                        String emsg = "Unable to parse notation found in cell " + cell.getCellRef() + ", note= " + sheet.getNote(i, j);
                    logger.error(emsg);
                    game.sendGameMsg(emsg);
                    } catch (Throwable e){
                        String ref = new CellReference(i, j).getCellRef();
                        String emsg = e.toString() + " reading: " + sheet.tab + "!" + ref;
                    game.sendGameMsg(emsg);
                    logger.error(emsg);
                        e.printStackTrace();
                    }
                }
            }
            checkAndRegisterPlayBook(playbook);
            playbook = null;
    }

    public void checkAndRegisterPlayBook(Playbook book) {
        if (book != null && book.isValid()) {
            if (game.playbooks.playbooks.containsKey(book.player)){
                String error = book.player + " has already registered playbook " + game.playbooks.playbooks.get(book.player).title + ", players cannot register more than one playbook";
                game.sendGameMsg(error);
                return;
            }
            book.basicMoves = game.basicMoves;
            book.skippedMoves = game.skippedMoves;
            game.sendGameMsg(book.player, "registered playbook *" + book.title + "*");
            game.playbooks.playbooks.put(book.player, book);
        }
    }

    public String replaceCellReferences(RangeWrapper range, CellReference cell, String note) {
        String cellRef = null;
        try {
            cellRef = note.substring(note.indexOf("[") + 1, note.indexOf("]"));
            String replacement = cellRef;
            if(cellRef.contains(",")){
                int columnOffset = Integer.parseInt(cellRef.split(",")[0]);
                int rowOffset = Integer.parseInt(cellRef.split(",")[1]);
                replacement = cell.getOffsetCell(columnOffset, rowOffset).getCellRef();
            }
            String value = range.getValue(replacement);
            return note.replace("[" + cellRef+ "]", value);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to parse cell reference for " + cellRef);
        }
    }

    public void registerBasicMove(Move move) {
        game.basicMoves.put(move.name, move);
    }

    public void registerSkippedMove(String moveName) {
        game.registerSkippedMove(moveName);
    }

}
