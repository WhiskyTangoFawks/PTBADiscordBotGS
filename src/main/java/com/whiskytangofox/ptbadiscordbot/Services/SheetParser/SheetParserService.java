package com.whiskytangofox.ptbadiscordbot.Services.SheetParser;

import com.whiskytangofox.ptbadiscordbot.AbstractGameSheetMethods;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.SheetParser.NoteParsers.*;
import com.whiskytangofox.ptbadiscordbot.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SheetParserService {

    public AbstractGameSheetMethods game;

    public SheetParserService(AbstractGameSheetMethods game) {
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
                        PARSER parser = PARSER.valueOf(note.split(";")[0].split("=")[0]);
                        String result = parser.parse(this, sheet, playbook, note, i, j);
                        game.sendDebugMsg("Parsed note " + parser.name() + ":  " + result);
                    }
                    } catch (IllegalArgumentException e) {
                        CellReference cell = new CellReference(i, j);
                        String emsg = "Unable to parse notation found in cell " + cell.getCellRef() + ", note= " + sheet.getNote(i, j);
                    logger.error(emsg);
                    game.sendGameMsg(emsg);
                    e.printStackTrace();
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
    }

    public void checkAndRegisterPlayBook(Playbook book) {
        if (book != null && book.isValid()) {
            game.sendGameMsg(book.player, "registered playbook *" + book.title + "*");
            book.basicMoves = game.basicMoves;
            book.skippedMoves = game.skippedMoves;
            game.playbooks.playbooks.put(book.player, book);
        }
    }

    public void registerBasicMove(Move move) {
        game.basicMoves.put(move.name, move);
    }

    public void registerSkippedMove(String moveName) {
        game.registerSkippedMove(moveName);
    }

}
