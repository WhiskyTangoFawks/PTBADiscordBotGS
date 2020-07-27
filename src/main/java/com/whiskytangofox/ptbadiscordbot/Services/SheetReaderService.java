package com.whiskytangofox.ptbadiscordbot.Services;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Game;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.Services.NoteParsers.*;
import net.dv8tion.jda.api.entities.Member;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SheetReaderService {

    public final Game game;

    public SheetReaderService(Game game) {
        this.game = game;
    }

    public static Logger logger = LoggerFactory.getLogger(SheetReaderService.class);

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

        public String parse(SheetReaderService service, RangeWrapper sheet, Playbook playbook, String note, int i, int j) {
            return parser.parse(service, sheet, playbook, note, i, j);
        }
    }

    public Playbook playbook = null;

    public void parseSheet(RangeWrapper sheet) {

        for (int i = sheet.firstCell.getColumnInt(); i < sheet.lastCell.getColumnInt() + 1; i++) {
            for (int j = sheet.firstCell.getRow(); j < sheet.lastCell.getRow() + 1; j++) {

                try {
                    if (sheet.getNote(i, j) != null && !sheet.getNote(i, j).isBlank()) {
                        String note = sheet.getNote(i, j).toLowerCase().replace(" ", "");
                        PARSER parser = PARSER.valueOf(note.split(";")[0].split("=")[0]);
                        String result = parser.parse(this, sheet, playbook, note, i, j);
                        game.sendDebugMsg("Parsed note " + parser.name() + ":  " + result);
                    }
                    } catch (IllegalArgumentException e) {
                        CellReference cell = new CellReference(i, j);
                        String emsg = "Unable to parse notation found in cell " + cell.getCellRef() + ", note= " + sheet.getNote(i, j);
                        logger.error(emsg);
                        game.sendGameMessage(emsg);
                        e.printStackTrace();
                    } catch (Throwable e){
                        String ref = new CellReference(i, j).getCellRef();
                        String emsg = e.toString() + " reading: " + sheet.tab + "!" + ref;
                        game.sendGameMessage(emsg);
                        logger.error(emsg);
                        e.printStackTrace();
                    }
                }
            }
            checkAndRegisterPlayBook(playbook);
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
            game.playbooks.playbooks.put(book.player, book);
        }
    }

    public void registerBasicMove(Move move) {
        game.basicMoves.put(move.name, move);
    }


}
