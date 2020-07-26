package com.whiskytangofox.ptbadiscordbot.DataObjects;

import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.GetStatResponse;
import com.whiskytangofox.ptbadiscordbot.DataObjects.Responses.SetResourceResponse;
import com.whiskytangofox.ptbadiscordbot.Exceptions.DiscordBotException;
import com.whiskytangofox.ptbadiscordbot.Exceptions.PlayerNotFoundException;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;
import com.whiskytangofox.ptbadiscordbot.GoogleSheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.Services.GameSheetService;
import com.whiskytangofox.ptbadiscordbot.Services.PlaybookService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PlaybookTest {

    public static final Logger logger = LoggerFactory.getLogger(PlaybookTest.class);

    @Mock
    static GoogleSheetAPI mockApi;

    GameSheetService mockSheetService;

    PlaybookService playbookService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        mockSheetService = new GameSheetService(null, mockApi, new Properties());
        playbookService = new PlaybookService(mockSheetService);
    }

    @Test
    public void testGetStat() throws IOException, DiscordBotException {
        Playbook book = new Playbook(mockSheetService, null);
        book.player = "test";
        book.stats.put("str", new CellReference("A1"));
        book.stat_penalties.put("str", new CellReference("A2"));
        playbookService.playbooks.put("test", book);

        //Mocks
        ArrayList<String> mockValues = new ArrayList<String>();
        mockValues.add("+2");
        mockValues.add("FALSE");
        ArrayList<String> cells = new ArrayList<String>();
        cells.add("A1");
        cells.add("A2");
        when(mockApi.getValues(any(), any(), any())).thenReturn(mockValues);
        GetStatResponse result = playbookService.getStat("test", "str");
        assertEquals(2, result.modStat);
    }

    @Test
    public void testIsStat() {
        Playbook book = new Playbook(mockSheetService, "test");
        book.player = "test";
        book.stats.put("str", new CellReference("A1"));
        book.stat_penalties.put("str", new CellReference("A2"));
        playbookService.playbooks.put("test", book);
        assertTrue(playbookService.isStat("test", "str"));
        assertTrue(playbookService.isStat("test", "STR"));
        assertFalse(playbookService.isStat("test", "blood"));
    }

    @Test
    public void testIsResource() throws PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        book.resources.put("hp", new Resource(new CellReference("A1")));
        playbookService.playbooks.put("test1", book);
        assertTrue(playbookService.playbooks.containsKey("test1"));
        assertTrue(playbookService.playbooks.get("test1").resources.containsKey("hp"));
        assertTrue(playbookService.isResource("test1", "hp"));
    }


    @Test
    public void testModifyResource_Integer_NoChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        book.resources.put("hp", new Resource(new CellReference("A1")));
        playbookService.playbooks.put("test1", book);
        ArrayList<String> mockResult = new ArrayList<>();
        mockResult.add("10");
        when(mockApi.getValues(any(), any(), any())).thenReturn(mockResult);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", 0);
        assertEquals(0, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(10, result.newValue);
        verify(mockApi, times(0)).setValues(any(), any(), any(), any());
    }


    @Test
    public void testModifyResource_Integer_Change() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        book.resources.put("hp", new Resource(new CellReference("A1")));
        playbookService.playbooks.put("test1", book);
        ArrayList<String> sheetValues = new ArrayList<>();
        sheetValues.add("10");
        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", 1);
        assertEquals(1, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(11, result.newValue);
        verify(mockApi, times(1)).setValues(any(), any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_ChangeMax() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        Resource resource = new Resource(new CellReference("A1"));
        resource.min = 0;
        resource.max = 20;
        book.resources.put("hp", resource);
        playbookService.playbooks.put("test1", book);
        ArrayList<String> sheetValues = new ArrayList<>();
        sheetValues.add("10");
        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", 20);
        assertEquals(20, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(20, result.newValue);
        verify(mockApi, times(1)).setValues(any(), any(), any(), any());
    }

    @Test
    public void testModifyResource_Integer_ChangeMin() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        Resource resource = new Resource(new CellReference("A1"));
        resource.min = 0;
        resource.max = 20;
        book.resources.put("hp", resource);
        playbookService.playbooks.put("test1", book);
        ArrayList<String> sheetValues = new ArrayList<>();
        sheetValues.add("10");
        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", -20);
        assertEquals(-20, result.mod);
        assertEquals(10, result.oldValue);
        assertEquals(0, result.newValue);
        verify(mockApi, times(1)).setValues(any(), any(), any(), any());
    }


    @Test
    public void testModifyResource_Checklist_NoChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));
        book.resources.put("hp", new Resource(listCellRefs));
        playbookService.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", 0);
        assertEquals(0, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(3, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockApi, times(0))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_PosChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));
        playbookService.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "FALSE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", 2);
        assertEquals(2, result.mod);
        assertEquals(2, result.oldValue);
        assertEquals(4, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "TRUE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockApi, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_NegChange() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));
        playbookService.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", -2);
        assertEquals(-2, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(1, result.newValue);
        String[] expectedArr = {"TRUE", "FALSE", "FALSE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockApi, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_NegChangeLimited() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList(Arrays.asList(cellRefs).stream()
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));
        playbookService.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", -4);
        assertEquals(-4, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(0, result.newValue);
        String[] expectedArr = {"FALSE", "FALSE", "FALSE", "FALSE", "FALSE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockApi, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void testModifyResource_Checklist_PosChangeLimited() throws IOException, PlayerNotFoundException {
        Playbook book = new Playbook(mockSheetService, "test");
        String[] cellRefs = {"A1", "A2", "A3", "A4", "A5"};
        ArrayList<CellReference> listCellRefs = new ArrayList<>(Arrays.stream(cellRefs)
                .map(c -> new CellReference(c)).collect(Collectors.toList()));

        book.resources.put("hp", new Resource(listCellRefs));
        playbookService.playbooks.put("test1", book);
        String[] arrValues = {"TRUE", "TRUE", "TRUE", "FALSE", "FALSE"};
        List<String> sheetValues = Arrays.asList(arrValues);

        when(mockApi.getValues(any(), any(), any())).thenReturn(sheetValues);
        SetResourceResponse result = playbookService.modifyResource("test1", "hp", 10);
        assertEquals(10, result.mod);
        assertEquals(3, result.oldValue);
        assertEquals(5, result.newValue);
        String[] expectedArr = {"TRUE", "TRUE", "TRUE", "TRUE", "TRUE"};
        List<String> expectedList = Arrays.asList(expectedArr);
        verify(mockApi, times(1))
                .setValues(null, "test", Arrays.asList(cellRefs), expectedList);
    }

    @Test
    public void isPlaybookMove() {
    }

    @Test
    public void getMove() {
    }

    @Test
    public void getMoveDice() {
    }
}