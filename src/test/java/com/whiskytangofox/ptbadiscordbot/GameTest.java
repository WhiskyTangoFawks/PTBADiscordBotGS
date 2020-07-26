package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Move;
import com.whiskytangofox.ptbadiscordbot.DataObjects.MoveBuilder;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Playbook;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.Services.GameSheetService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class GameTest {

    static Game game;
    public static final Logger logger = LoggerFactory.getLogger(GameTest.class);

    static MoveBuilder basicBuilder;
    static MoveBuilder advancedBuilder;
    Playbook book;

    @Mock
    static GoogleSheetAPI mockApi;

    GameSheetService mockSheetService;

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
        mockSheetService = new GameSheetService(null, mockApi, game.settings);

    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsMove() throws KeyConflictException {
        Move move = basicBuilder.getMove();
        game.basicMoves.put(move.name, move);
        assertTrue(game.isMove("test", move.name));
    }

    @Test
    public void testIsMoveFalse() throws KeyConflictException {
        Move move = basicBuilder.getMove();
        game.basicMoves.put(move.name, move);
        assertFalse(game.isMove("test", "not a move"));
    }

    @Test
    public void testIsMovePlaybookMove() throws KeyConflictException {
        Move move = basicBuilder.getMove();
        Playbook book = new Playbook(mockSheetService, null);
        book.player = "test";
        book.moves.put(move.name, move);
        game.playbooks.put(book);
        assertTrue(game.isMove("test", move.name));
    }

    @Test
    public void testGetMoveBasic() throws KeyConflictException {
        Move move = basicBuilder.getMove();
        game.basicMoves.put(move.name, move);
        assertNotNull(game.getMove("test", move.name));
    }

    @Test
    public void testGetMovePlaybook() throws KeyConflictException {
        Move move = basicBuilder.getMove();
        Playbook book = new Playbook(mockSheetService, null);
        book.player = "test";
        book.moves.put(move.name, move);
        game.playbooks.put(book);
        assertNotNull(game.getMove("test", move.name));
    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves() {
        Move basicMove = basicBuilder.getMove();
        game.basicMoves.put(basicMove.name, basicMove);

        Move advMove = advancedBuilder.getMove();
        Playbook book = new Playbook(mockSheetService, null);
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
        assertEquals("override move text", game.getMove("test", basic.getReferenceMoveName()).text);
    }

}