package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
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

    static MoveBuilder basicBuilder;
    static MoveBuilder advancedBuilder;

    @Mock
    static GoogleSheetAPI mockApi;

    SheetAPIService mockSheetService;

    @BeforeClass
    public static void beforeClass() {
        logger.info("Running @BeforeClass Setup");

        basicBuilder = new MoveBuilder();
        basicBuilder.addLine();
        basicBuilder.set(0, "Basic Move");
        basicBuilder.addLine();
        basicBuilder.set(1, "basic move text roll +STR:");

        advancedBuilder = new MoveBuilder();
        advancedBuilder.addLine();
        advancedBuilder.set(0, "Advanced Move (Basic Move)");
        advancedBuilder.addLine();
        advancedBuilder.set(1, "advanced move text roll +INT:");
    }

    @Before
    public void setupGame() throws Exception {
        game = new Game(null, null, null, false);
        MockitoAnnotations.initMocks(this);
        mockSheetService = new SheetAPIService(null, mockApi, game.settings);

    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves() {
        Move basicMove = basicBuilder.getMove();
        game.basicMoves.put(basicMove.name, basicMove);

        Move advMove = advancedBuilder.getMove();
        Playbook book = new Playbook(mockSheetService, null);
        book.basicMoves = game.basicMoves;
        book.player = "test";
        book.moves.put(advMove.name, advMove);
        game.playbooks.put(book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(advMove.name));
        assertTrue(book.moves.containsKey(basicMove.name));
    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves_ConcurrentModException() {
        Move basicMove = basicBuilder.getMove();
        game.basicMoves.put(basicMove.name, basicMove);
        game.basicMoves.put("filler1", new Move("filler1", "text"));
        game.basicMoves.put("filler2", new Move("filler2", "text"));
        game.basicMoves.put("filler3", new Move("filler3", "text"));

        Move advMove = advancedBuilder.getMove();
        Playbook book = new Playbook(mockSheetService, null);
        book.basicMoves = game.basicMoves;
        book.player = "test";
        book.moves.put(advMove.name, advMove);
        book.moves.put("pbfiller1", new Move("pbfiller1", "text"));
        book.moves.put("pbfiller2", new Move("pbfiller2", "text"));
        book.moves.put("pbfiller3", new Move("pbfiller3", "text"));
        game.playbooks.put(book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(advMove.name));
        assertTrue(book.moves.containsKey(basicMove.name));
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
        Move basic = basicBuilder.getMove();
        basic.text = "Basic Move Text";
        game.basicMoves.put(basic.name, basic);

        Move override = basicBuilder.getMove();
        override.text = "override move text";
        Playbook book = new Playbook(mockSheetService, null);
        book.player = "test";
        book.moves.put(basic.name, override);
        game.playbooks.put(book);
        assertEquals("override move text", book.getMove(basic.getReferenceMoveName()).text);
    }

}