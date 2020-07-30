package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GameSheetTest {

    Game game;
    public static final Logger logger = LoggerFactory.getLogger(GameSheetTest.class);

    @Mock
    static GoogleSheetAPI mockApi;

    SheetAPIService mockSheetService;
    Move move;
    Move secondaryMove;
    Playbook book;

    @BeforeClass
    public static void beforeClass() {
        logger.info("Running @BeforeClass Setup");

    }

    @Before
    public void setupGame() throws Exception {
        game = new Game(null, null, null, false);
        MockitoAnnotations.initMocks(this);
        mockSheetService = new SheetAPIService(null, mockApi, game.settings);
        move = new Move("Basic Move", "basic move text roll +STR:");
        secondaryMove = new Move("Advanced Move (Basic Move)", "advanced move text roll +INT:");
        book = new Playbook(mockSheetService, null);
        book.player = "test";
    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves() {
        game.basicMoves.put(move.name, move);
        book.basicMoves = game.basicMoves;

        book.moves.put(secondaryMove.name, secondaryMove);
        game.playbooks.put(book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(move.name));
        assertTrue(book.moves.containsKey(move.name));
    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves_ConcurrentModException() {

        game.basicMoves.put(move.name, move);
        game.basicMoves.put("filler1", new Move("filler1", "text"));
        game.basicMoves.put("filler2", new Move("filler2", "text"));
        game.basicMoves.put("filler3", new Move("filler3", "text"));

        Playbook book = new Playbook(mockSheetService, null);
        book.basicMoves = game.basicMoves;
        book.player = "test";
        book.moves.put(secondaryMove.name, secondaryMove);
        book.moves.put("pbfiller1", new Move("pbfiller1", "text"));
        book.moves.put("pbfiller2", new Move("pbfiller2", "text"));
        book.moves.put("pbfiller3", new Move("pbfiller3", "text"));
        game.playbooks.put(book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(secondaryMove.name));
        assertTrue(book.moves.containsKey(move.name));
    }


    @Test
    public void testCopyAndStoreModifiedBasicMoves_TestForTwoMovesModifySameBasicMove() {
        Move basicMove = new Move("Move", "move text");
        game.basicMoves.put(basicMove.name, basicMove);

        Playbook book = new Playbook(mockSheetService, null);
        book.basicMoves = game.basicMoves;
        book.player = "test";

        Move adv1 = new Move("Secondary 1 (Move)", "Secondary move text Number 1");
        Move adv2 = new Move("Secondary 2 (Move)", "Secondary Move text Number 2");

        book.moves.put(adv1.name, adv1);
        book.moves.put(adv2.name, adv2);
        game.playbooks.put(book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(basicMove.name));
        assertTrue(book.moves.containsKey(adv1.name));
        assertTrue(book.moves.containsKey(adv2.name));

        Move modifiedBasic = book.moves.get(basicMove.name);
        logger.info(modifiedBasic.text);
        assertTrue(modifiedBasic.text.contains("Number 1"));
        assertTrue(modifiedBasic.text.contains("Number 2"));
    }

    @Test
    public void testGetMovePlaybookOverrideBasic() throws KeyConflictException {

        game.basicMoves.put(move.name, move);

        Move override = new Move(move.name, "override move text");

        book.moves.put(override.name, override);
        game.playbooks.put(book);
        assertEquals("override move text", book.getMove(move.getReferenceMoveName()).text);
    }

}