package com.whiskytangofox.ptbadiscordbot.DataObjects;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.SetResourceResponse;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.StatResponse;
import com.whiskytangofox.ptbadiscordbot.DataStructure.GameSettings;
import com.whiskytangofox.ptbadiscordbot.DataStructure.PatriciaTrieIgnoreCase;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.KeyConflictException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.MissingValueException;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.Services.SheetAPIService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlaybookTest {

    public static final Logger logger = LoggerFactory.getLogger(PlaybookTest.class);

    @Mock
    SheetAPIService mockSheetService;

    @Mock
    GameSettings mockSettings;

    Playbook book;

    String testPlayer = "test";

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        mockSheetService.settings = mockSettings;
        book = new Playbook(mockSheetService, null);
        book.player = testPlayer;
        book.basicMoves = new PatriciaTrieIgnoreCase<>();
        book.skippedMoves = new HashSet<>();
    }

    @Test
    public void testIsStat() {
        book.stats.put("str", new CellReference("A1"));
        book.stat_penalties.put("str", new CellReference("A2"));

        assertTrue(book.isStat("str"));
        assertTrue(book.isStat("STR"));
        assertFalse(book.isStat("blood"));
    }

    @Test
    public void testGetStat() throws IOException, DiscordBotException {
        Playbook book = new Playbook(mockSheetService, null);

        book.stats.put("str", new CellReference("A1"));
        book.stat_penalties.put("str", new CellReference("A2"));
        when(mockSettings.get(GameSettings.KEY.stat_debility_tag)).thenReturn("dis");

        //Mocks
        ArrayList<String> mockValues = new ArrayList<>();
        mockValues.add("+2");
        mockValues.add("FALSE");

        when(mockSheetService.getValues(any(), any())).thenReturn(mockValues);
        StatResponse result = book.getStat("str");
        assertEquals(2, result.modStat);
        assertEquals("dis", result.debilityTag);
    }

    @Test
    public void testGetStatException_NullPointer() throws IOException, DiscordBotException {
        Playbook book = new Playbook(mockSheetService, null);

        book.stats.put("str", new CellReference("A1"));
        book.stat_penalties.put("str", new CellReference("A2"));
        when(mockSettings.get(GameSettings.KEY.stat_debility_tag)).thenReturn("dis");

        boolean thrown = false;
        when(mockSheetService.getValues(any(), any())).thenThrow(new NullPointerException("test null pointer"));
        try {
            StatResponse result = book.getStat("str");
        } catch (MissingValueException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testGetStatException_NumberFormatException() throws IOException, DiscordBotException {
        Playbook book = new Playbook(mockSheetService, null);

        book.stats.put("str", new CellReference("A1"));
        book.stat_penalties.put("str", new CellReference("A2"));
        when(mockSettings.get(GameSettings.KEY.stat_debility_tag)).thenReturn("dis");

        //Mocks
        ArrayList<String> mockValues = new ArrayList<>();
        mockValues.add("NotANumber");
        mockValues.add("FALSE");
        when(mockSheetService.getValues(any(), any())).thenReturn(mockValues);

        boolean thrown = false;
        try {
            StatResponse result = book.getStat("str");
        } catch (MissingValueException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Test
    public void testHasMoveStat() throws KeyConflictException {
        book.moves.put("move", new Move("Move", "roll +stat"));
        book.stats.put("stat", new CellReference("A1"));
        assertTrue(book.hasMoveStat("Move"));
    }

    @Test
    public void testHasMoveStat_False() throws KeyConflictException {
        book.moves.put("move", new Move("Move", "roll +NotAStat"));
        book.stats.put("stat", new CellReference("A1"));
        assertFalse(book.hasMoveStat("Move"));
    }

    @Test
    public void testGetMoveStat() throws KeyConflictException, DiscordBotException, IOException {
        book.moves.put("move", new Move("Move", "roll +stat"));
        book.stats.put("stat", new CellReference("A1"));
        book.stat_penalties.put("stat", new CellReference("A2"));
        when(mockSettings.get(GameSettings.KEY.stat_debility_tag)).thenReturn("dis");

        //MockStatResponse
        ArrayList<String> mockValues = new ArrayList<>();
        mockValues.add("+2");
        mockValues.add("FALSE");
        ArrayList<String> cells = new ArrayList<>();
        cells.add("A1");
        cells.add("A2");
        when(mockSheetService.getValues(any(), any())).thenReturn(mockValues);

        StatResponse response = book.getMoveStat("move");
        assertEquals("stat", response.stat);
        assertEquals(2, response.modStat);
        assertFalse(response.isDebilitated);
        assertEquals("dis", response.debilityTag);
    }

    @Test
    public void testIsResource() {
        book.resources.put("hp", new Resource(new CellReference("A1")));
        assertTrue(book.isResource("hp"));
    }

    @Test
    public void testIsMove() {
        book.moves.put("move0", new Move("move", "move text"));
        assertTrue(book.isMove("move0"));
    }

    @Test
    public void testModifyResource_Integer_NoChange() throws IOException {
        book.resources.put("hp", new Resource(new CellReference("A1")));

        ArrayList<String> mockResult = new ArrayList<>();
        mockResult.add("10");
        when(mockSheetService.getValues(any(), any())).thenReturn(mockResult);
        SetResourceResponse result = book.modifyResource("hp", 0);
        assertEquals(0, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(10, result.newValue);
        verify(mockSheetService, times(0)).setValues(any(), any(), any());
    }


    @Test
    public void testModifyResource_Integer_Change() throws IOException {
        book.resources.put("hp", new Resource(new CellReference("A1")));

        ArrayList<String> sheetValues = new ArrayList<>();
        sheetValues.add("10");
        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", 1);
        assertEquals(1, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(11, result.newValue);
        verify(mockSheetService, times(1)).setValues(any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_ChangeMax() throws IOException {

        Resource resource = new Resource(new CellReference("A1"));
        resource.min = 0;
        resource.max = 20;
        book.resources.put("hp", resource);

        ArrayList<String> sheetValues = new ArrayList<>();
        sheetValues.add("10");
        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", 20);
        assertEquals(20, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(20, result.newValue);
        verify(mockSheetService, times(1)).setValues(any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_ChangeMin() throws IOException {

        Resource resource = new Resource(new CellReference("A1"));
        resource.min = 0;
        resource.max = 20;
        book.resources.put("hp", resource);

        ArrayList<String> sheetValues = new ArrayList<>();
        sheetValues.add("10");
        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", -20);
        assertEquals(-20, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(0, result.newValue);
        verify(mockSheetService, times(1)).setValues(any(), any(), any());
    }


    @Test
    public void testModifyResource_Checklist_NoChange() throws IOException {
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(CellReference::new).collect(Collectors.toList()));
        book.resources.put("hp", new Resource(listCellRefs));

        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", 0);
        assertEquals(0, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(3, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockSheetService, times(0))
                .setValues(null, Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_PosChange() throws IOException {
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));
        String[] arrValues = {"TRUE", "TRUE", "FALSE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", 2);
        assertEquals(2, result.mod);
        assertEquals(2, result.oldValue);
        assertEquals(4, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "TRUE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockSheetService, times(1))
                .setValues(null, Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_NegChange() throws IOException {
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));

        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", -2);
        assertEquals(-2, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(1, result.newValue);
        String[] expectedArr = {"TRUE", "FALSE", "FALSE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockSheetService, times(1))
                .setValues(null, Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_NegChangeLimited() throws IOException {
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList(Arrays.asList(cellRefs).stream()
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));

        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", -4);
        assertEquals(-4, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(0, result.newValue);
        String[] expectedArr = {"FALSE", "FALSE", "FALSE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockSheetService, times(1))
                .setValues(null, Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_PosChangeLimited() throws IOException {
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockSheetService.getValues(any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = book.modifyResource("hp", 10);
        assertEquals(10, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(5, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "TRUE", "TRUE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockSheetService, times(1))
                .setValues(null, Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testGetMovePenalty() throws IOException, KeyConflictException {
        book.movePenalties.put("move", new CellReference("A1"));
        when(mockSheetService.getCellValue(any(), any())).thenReturn("-1");
        assertEquals(-1, book.getMovePenalty("move"));
    }

    @Test
    @Ignore
    public void testGetMovePenalty_secondaryMove() throws IOException, KeyConflictException {
        //TODO - implement this or commit to NOT doing it
        book.movePenalties.put("move", new CellReference("A1"));
        when(mockSheetService.getCellValue(any(), any())).thenReturn("-1");
        assertEquals(-1, book.getMovePenalty("secondary (move)"));
    }

    @Test
    public void testValidatePlaybook() {
        book.stat_penalties.put("stat0", null);
        book.stat_penalties.put("stat", null);
        book.movePenalties.put("move0", null);
        book.movePenalties.put("move", null);
        book.stats.put("stat0", new CellReference("A1"));
        book.moves.put("move0", new Move("move0", "move text"));

        assertTrue(book.isMove("move0"));
        assertFalse(book.moves.containsKey("move"));

        assertTrue(book.isStat("stat0"));
        assertFalse(book.isStat("stat"));

        String validationString = book.getValidationMsg();
        logger.info(validationString);
        assertFalse(validationString.contains("move0"));
        assertFalse(validationString.contains("stat0"));
        assertTrue(validationString.contains("move"));
        assertTrue(validationString.contains("stat"));
    }

    @Test
    public void testValidatePlaybook_SkippedMove() {
        book.movePenalties.put("move", null);
        book.movePenalties.put("moveskipped", null);

        book.skippedMoves.add("moveskipped");

        String validationString = book.getValidationMsg();
        logger.info(validationString);
        assertFalse(validationString.contains("moveskipped"));
        assertTrue(validationString.contains("move"));
    }


}