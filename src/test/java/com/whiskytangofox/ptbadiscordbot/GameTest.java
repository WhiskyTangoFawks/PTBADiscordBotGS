package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.wrappers.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class GameTest {

    static Game game;
    public static final Logger logger = LoggerFactory.getLogger(GameTest.class);

    static MoveBuilder basicBuilder;
    static MoveBuilder advancedBuilder;

    @Mock
    static GoogleSheetAPI mockApi;

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
        App.googleSheetAPI = mockApi;
    }

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIsMove() throws KeyConflictException {
        MoveWrapper move = basicBuilder.getMove();
        game.basicMoves.put(move.name, move);
        assertTrue(game.isMove("test", move.name));
    }

    @Test
    public void testIsMoveFalse() throws KeyConflictException {
        MoveWrapper move = basicBuilder.getMove();
        game.basicMoves.put(move.name, move);
        assertFalse(game.isMove("test", "not a move"));
    }

    @Test
    public void testIsMovePlaybookMove() throws KeyConflictException {
        MoveWrapper move = basicBuilder.getMove();
        Playbook book = new Playbook(null);
        book.player = "test";
        book.moves.put(move.name, move);
        game.playbooks.put("test", book);
        assertTrue(game.isMove("test", move.name));
    }

    @Test
    public void testGetMoveBasic() throws KeyConflictException {
        MoveWrapper move = basicBuilder.getMove();
        game.basicMoves.put(move.name, move);
        assertNotNull(game.getMove("test", move.name));
    }

    @Test
    public void testGetMovePlaybook() throws KeyConflictException {
        MoveWrapper move = basicBuilder.getMove();
        Playbook book = new Playbook(null);
        book.player = "test";
        book.moves.put(move.name, move);
        game.playbooks.put("test", book);
        assertNotNull(game.getMove("test", move.name));
    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves() {
        MoveWrapper basicMove = basicBuilder.getMove();
        game.basicMoves.put(basicMove.name, basicMove);

        MoveWrapper advMove = advancedBuilder.getMove();
        Playbook book = new Playbook(null);
        book.player = "test";
        book.moves.put(advMove.name, advMove);
        game.playbooks.put("test", book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(advMove.name));
        assertTrue(book.moves.containsKey(basicMove.name));
    }

    @Test
    public void testCopyAndStoreModifiedBasicMoves_ConcurrentModException() {
        MoveWrapper basicMove = basicBuilder.getMove();
        game.basicMoves.put(basicMove.name, basicMove);
        game.basicMoves.put("filler1", new MoveWrapper("filler1", "text"));
        game.basicMoves.put("filler2", new MoveWrapper("filler2", "text"));
        game.basicMoves.put("filler3", new MoveWrapper("filler3", "text"));

        MoveWrapper advMove = advancedBuilder.getMove();
        Playbook book = new Playbook(null);
        book.player = "test";
        book.moves.put(advMove.name, advMove);
        book.moves.put("pbfiller1", new MoveWrapper("pbfiller1", "text"));
        book.moves.put("pbfiller2", new MoveWrapper("pbfiller2", "text"));
        book.moves.put("pbfiller3", new MoveWrapper("pbfiller3", "text"));
        game.playbooks.put("test", book);

        game.copyAndStoreModifiedBasicMoves();

        assertTrue(book.moves.containsKey(advMove.name));
        assertTrue(book.moves.containsKey(basicMove.name));
    }

    @Test
    public void testGetMovePlaybookOverrideBasic() throws KeyConflictException {
        MoveWrapper basic = basicBuilder.getMove();
        basic.text = "Basic Move Text";
        game.basicMoves.put(basic.name, basic);

        MoveWrapper override = basicBuilder.getMove();
        override.text = "override move text";
        Playbook book = new Playbook(null);
        book.player = "test";
        book.moves.put(basic.name, override);
        game.playbooks.put("test", book);
        assertEquals("override move text", game.getMove("test", basic.getReferenceMoveName()).text);
    }

    @Test
    public void testIsStat() {
        Playbook book = new Playbook("test");
        book.player = "test";
        book.stats.put("str", new CellRef("A1"));
        book.stat_penalties.put("str", new CellRef("A2"));
        game.playbooks.put("test", book);
        assertTrue(game.isStat("test", "str"));
        assertTrue(game.isStat("test", "STR"));
        assertFalse(game.isStat("test", "blood"));
    }

    @Test
    public void testGetStat() throws IOException, DiscordBotException {
        Playbook book = new Playbook("test");
        book.player = "test";
        book.stats.put("str", new CellRef("A1"));
        book.stat_penalties.put("str", new CellRef("A2"));
        game.playbooks.put("test", book);

        //Mocks
        ArrayList<String> mockValues = new ArrayList<String>();
        mockValues.add("+2");
        mockValues.add("FALSE");
        ArrayList<String> cells = new ArrayList<String>();
        cells.add("A1");
        cells.add("A2");
        when(App.googleSheetAPI.getValues(null, "test", cells)).thenReturn(mockValues);
        StatWrapper result = game.getStat("test", "str");
        assertEquals(2, result.modStat);
    }

    @Test
    public void testGetPlaybook() throws PlayerNotFoundException {
        game.playbooks.put("test1", new Playbook(null));
        assertTrue(game.playbooks.containsKey("test1"));
        assertNotNull(game.getPlaybook("test1"));
    }

    @Test
    public void testGetPlaybookCaseDifference() throws PlayerNotFoundException {
        game.playbooks.put("test1", new Playbook(null));
        assertTrue(game.playbooks.containsKey("Test1"));
        assertTrue(game.getPlaybook("test1") != null);
    }

    @Test
    public void testGetPlaybookException() {
        game.playbooks.put("test1", new Playbook(null));
        assertTrue(game.playbooks.containsKey("test1"));
        boolean thrown = false;
        try {
            game.getPlaybook("test2");
        } catch (PlayerNotFoundException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testIsResource() {
        Playbook book = new Playbook("test");
        book.resources.put("hp", new ResourceWrapper(new CellRef("A1")));
        game.playbooks.put("test1", book);
        assertTrue(game.playbooks.containsKey("test1"));
        assertTrue(game.playbooks.get("test1").resources.containsKey("hp"));
        assertTrue(game.isResource("test1", "hp"));
    }

    @Test
    public void testModifyResource_Integer_NoChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        book.resources.put("hp", new ResourceWrapper(new CellRef("A1")));
        game.playbooks.put("test1", book);
        ArrayList<String> mockResult = new ArrayList();
        mockResult.add("10");
        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(mockResult);
        SetResourceResult result = game.modifyResource("test1", "hp", 0);
        assertEquals(0, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(10, result.newValue);
        verify(App.googleSheetAPI, times(0)).setValues(any(), any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_Change() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        book.resources.put("hp", new ResourceWrapper(new CellRef("A1")));
        game.playbooks.put("test1", book);
        ArrayList<String> sheetValues = new ArrayList();
        sheetValues.add("10");
        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", 1);
        assertEquals(1, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(11, result.newValue);
        verify(App.googleSheetAPI, times(1)).setValues(any(), any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_ChangeMax() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        ResourceWrapper resource = new ResourceWrapper(new CellRef("A1"));
        resource.min = 0;
        resource.max = 20;
        book.resources.put("hp", resource);
        game.playbooks.put("test1", book);
        ArrayList<String> sheetValues = new ArrayList();
        sheetValues.add("10");
        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", 20);
        assertEquals(20, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(20, result.newValue);
        verify(App.googleSheetAPI, times(1)).setValues(any(), any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_ChangeMin() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        ResourceWrapper resource = new ResourceWrapper(new CellRef("A1"));
        resource.min = 0;
        resource.max = 20;
        book.resources.put("hp", resource);
        game.playbooks.put("test1", book);
        ArrayList<String> sheetValues = new ArrayList();
        sheetValues.add("10");
        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", -20);
        assertEquals(-20, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(0, result.newValue);
        verify(App.googleSheetAPI, times(1)).setValues(any(), any(), any(), any());
    }


    @Test
    public void testModifyResource_Checklist_NoChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellRef> listCellRefs = new ArrayList(Arrays.asList(cellRefs).stream()
                .map(c -> new CellRef(c)).collect(Collectors.toList()));
        book.resources.put("hp", new ResourceWrapper(listCellRefs));
        game.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", 0);
        assertEquals(0, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(3, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(App.googleSheetAPI, times(0))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_PosChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellRef> listCellRefs = Arrays.asList(cellRefs).stream()
                .map(CellRef::new).collect(Collectors.toCollection((Supplier<ArrayList>) ArrayList::new));

        book.resources.put("hp", new ResourceWrapper(listCellRefs));
        game.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "FALSE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", 2);
        assertEquals(2, result.mod);
        assertEquals(2, result.oldValue);
        assertEquals(4, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "TRUE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(App.googleSheetAPI, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_NegChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellRef> listCellRefs = Arrays.stream(cellRefs)
                .map(CellRef::new)
                .collect(Collectors.toCollection((Supplier<ArrayList>) ArrayList::new));

        book.resources.put("hp", new ResourceWrapper(listCellRefs));
        game.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", -2);
        assertEquals(-2, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(1, result.newValue);
        String[] expectedArr = {"TRUE", "FALSE", "FALSE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(App.googleSheetAPI, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_NegChangeLimited() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellRef> listCellRefs = new ArrayList(Arrays.asList(cellRefs).stream()
                .map(c -> new CellRef(c)).collect(Collectors.toList()));

        book.resources.put("hp", new ResourceWrapper(listCellRefs));
        game.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", -4);
        assertEquals(-4, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(0, result.newValue);
        String[] expectedArr = {"FALSE", "FALSE", "FALSE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(App.googleSheetAPI, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_PosChangeLimited() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook("test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellRef> listCellRefs = Arrays.stream(cellRefs)
                .map(CellRef::new)
                .collect(Collectors.toCollection((Supplier<ArrayList>) ArrayList::new));

        book.resources.put("hp", new ResourceWrapper(listCellRefs));
        game.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(App.googleSheetAPI.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResult result = game.modifyResource("test1", "hp", 10);
        assertEquals(10, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(5, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "TRUE", "TRUE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(App.googleSheetAPI, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }
}