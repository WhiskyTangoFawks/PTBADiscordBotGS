package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.GoogleSheetAPI;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.PatriciaTrieIgnoreCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class MoveLoaderTest {

    public static final Logger logger = LoggerFactory.getLogger(MoveLoaderTest.class);
    static GoogleSheetAPI api;
    String sheetID = "1zwZlDaLdNDF7vs5Gx_WVFf19-E_IAy6pCNp_guQjMko";
    String tab = "movetest";


    private static Boolean isMoveInList(ArrayList<MoveWrapper> moves,String moveName){
        for (MoveWrapper move : moves){
            if (moveName.equalsIgnoreCase(move.name)){
                return true;
            }
        }
        return false;
    }

    private static Boolean isMoveInList(ArrayList<MoveWrapper> moves, String name, String text){
        for (MoveWrapper move : moves){
            if (name.equalsIgnoreCase(move.name)){
                return move.text.toLowerCase().contains(text.toLowerCase());
            }
        }
        return false;
    }

    @BeforeClass
    public static void setup() throws IOException, GeneralSecurityException {
        logger.info("Running @BeforeClass Setup");
        api = new GoogleSheetAPI();
    }

    @Test
    public void testGetValueIntInt() throws Exception {
        String rangeRef = "A1:F14";
        RangeWrapper range = api.getRange(sheetID, tab, rangeRef);
        ArrayList<MoveWrapper> list = MoveLoader.loadMovesFromRange(range, new PatriciaTrieIgnoreCase<MoveWrapper>());

        assertFalse(isMoveInList(list, "b10"));
        assertFalse(isMoveInList(list, "b10"));
        assertTrue(isMoveInList(list, "b2", "b3"));
        assertTrue(isMoveInList(list, "b5", "B6"));
        assertTrue(isMoveInList(list, "d2", "E3"));
        assertTrue(isMoveInList(list, "d2", "F3"));
        assertFalse(isMoveInList(list, "d2", "E4"));
        assertTrue(isMoveInList(list, "d6", "E7"));
        assertTrue(isMoveInList(list, "d6", "E8"));
    }

    @Test
    public void testLoadMoveSubRangeColumns() throws Exception {
        String fullTab = "A1:F12";
        RangeWrapper range = api.getRange(sheetID, tab, fullTab);
        String subRange = "A1:C7";

        ArrayList<MoveWrapper> list = MoveLoader.loadMovesFromRange(range, new PatriciaTrieIgnoreCase<MoveWrapper>());

        assertFalse(isMoveInList(list, "b10"));
        assertFalse(isMoveInList(list, "C10"));
        assertTrue(isMoveInList(list, "b2", "B3"));
        assertTrue(isMoveInList(list, "b5", "B6"));

        assertTrue(isMoveInList(list, "d2"));
        assertTrue(isMoveInList(list, "d6"));

    }

    @Test
    public void testLoadMoveOffsetSubRange() throws Exception {
        String fullTab = "A1:F12";
        RangeWrapper range = api.getRange(sheetID, tab, fullTab);
        String subRange = "A1:C10";

        ArrayList<MoveWrapper> list = MoveLoader.loadMovesFromRange(range, new PatriciaTrieIgnoreCase<MoveWrapper>());

        assertFalse(isMoveInList(list, "b10"));
        assertFalse(isMoveInList(list, "C10"));
        assertTrue(isMoveInList(list, "b2"));
        assertTrue(isMoveInList(list, "b5"));

        assertTrue(isMoveInList(list, "d2", "E3"));
        assertTrue(isMoveInList(list, "d2", "F3"));
        assertFalse(isMoveInList(list, "d2", "E4"));

        assertTrue(isMoveInList(list, "d6", "E7"));
        assertTrue(isMoveInList(list, "d6", "E8"));

    }

    @Test
    public void testLoadMoveSubRangeRows() throws Exception {
        String fullTab = "A1:F14";
        RangeWrapper range = api.getRange(sheetID, tab, fullTab);
        String subRange = "A5:F9";

        ArrayList<MoveWrapper> list = MoveLoader.loadMovesFromRange(range, new PatriciaTrieIgnoreCase<MoveWrapper>());

        assertTrue(isMoveInList(list, "b2"));
        assertTrue(isMoveInList(list, "d2"));
        assertFalse(isMoveInList(list, "b10"));
        assertFalse(isMoveInList(list, "C10"));
        assertTrue(isMoveInList(list, "b12"));
        assertTrue(isMoveInList(list, "d12"));

        assertTrue(isMoveInList(list, "b5", "B6"));

        assertTrue(isMoveInList(list, "b5", "B6"));
        assertTrue(isMoveInList(list, "d6", "E7"));
        assertTrue(isMoveInList(list, "d6", "E8"));
    }

    @Test
    public void advancedMoveLoadTest() throws Exception {
        String fullTab = "B12:D20";
        RangeWrapper range = api.getRange(sheetID, tab, fullTab);
        ArrayList<MoveWrapper> list = MoveLoader.loadMovesFromRange(range, new PatriciaTrieIgnoreCase<MoveWrapper>());

        assertTrue(isMoveInList(list, "c15"));
        assertTrue(isMoveInList(list, "c15", "B16"));
        assertFalse(isMoveInList(list, "c18"));
    }



    @Test
    public void testSecondaryMoveLoad() throws Exception {
        String basicRef = "H2:H3";
        RangeWrapper basicRange = api.getRange(sheetID, tab, basicRef);
        ArrayList<MoveWrapper> basicList = MoveLoader.loadMovesFromRange(basicRange, new PatriciaTrieIgnoreCase<MoveWrapper>());

        PatriciaTrieIgnoreCase<MoveWrapper> basicTrie = new PatriciaTrieIgnoreCase<MoveWrapper>();
        for (MoveWrapper move:basicList){
            basicTrie.put(move.name, move);
        }

        String playerRef = "H5:H6";
        RangeWrapper range2 = api.getRange(sheetID, tab, playerRef);

        ArrayList<MoveWrapper> secondaryMoves = MoveLoader.loadMovesFromRange(range2, basicTrie);
        assertTrue(isMoveInList(secondaryMoves, "H5 (H2)"));
        assertTrue(isMoveInList(secondaryMoves, "h2"));
        //assertEquals("int", player.get("h2").stat);
    }







}