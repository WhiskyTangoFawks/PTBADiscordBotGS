package com.whiskytangofox.ptbadiscordbot;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;
import com.whiskytangofox.ptbadiscordbot.googlesheet.RangeWrapper;
import com.whiskytangofox.ptbadiscordbot.wrappers.MoveBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MoveLoaderTest {

    public static final Logger logger = LoggerFactory.getLogger(MoveLoaderTest.class);

    private static Boolean isMoveInList(ArrayList<MoveBuilder> moves,String moveName){
        for (MoveBuilder move : moves){
            if (moveName.equalsIgnoreCase(move.get(0))){
                return true;
            }
        }
        return false;
    }

    private static Boolean isMoveInList(ArrayList<MoveBuilder> moves, String name, String text){
        for (MoveBuilder move : moves){
            if (name.equalsIgnoreCase(move.get(0))){
                for (int i = 0; i < move.lastIndex()+1; i++) {
                    if (move.get(i).toLowerCase().contains(text.toLowerCase())){
                        return true;
                    }

                }
            }
        }
        return false;
    }

    public RangeWrapper getTestData(String rangeDef){
        HashMap<CellRef, String> set = new HashMap<CellRef, String>();
        set.put(new CellRef("B2"), "B2");
        set.put(new CellRef("B3"), "B3 Move Text roll +str");
        set.put(new CellRef("B5"), "B5");
        set.put(new CellRef("B6"), "B6 Move Text");
        set.put(new CellRef("B10"), "B10");
        set.put(new CellRef("B12"), "B12");
        set.put(new CellRef("B13"), "B13 move text");
        set.put(new CellRef("B15"), "TRUE");
        set.put(new CellRef("B16"), "B16 move text");
        set.put(new CellRef("B18"), "FALSE");
        set.put(new CellRef("B19"), "B19 Move Text");
        set.put(new CellRef("C10"), "C10");
        set.put(new CellRef("C15"), "C15");
        set.put(new CellRef("C18"), "C18");
        set.put(new CellRef("D2"), "D2");
        set.put(new CellRef("D3"), "TRUE");
        set.put(new CellRef("D6"), "D6");
        set.put(new CellRef("D7"), " ");
        set.put(new CellRef("D8"), " ");
        set.put(new CellRef("D12"), "D12");
        set.put(new CellRef("D13"), "D13 move text");
        set.put(new CellRef("E3"), "E3 Move Text");
        set.put(new CellRef("E4"), "E4 Should Not Display");
        set.put(new CellRef("E7"), "E7 Move Text");
        set.put(new CellRef("E8"), "E8 Move Text");
        set.put(new CellRef("F3"), "F3 more move text roll +int");
        set.put(new CellRef("H2"), "H2");
        set.put(new CellRef("H3"), "H3 Move Text roll +str");
        set.put(new CellRef("H5"), "H5 (H2)");
        set.put(new CellRef("H6"), "H6 move text roll +int");
        set.put(new CellRef("H10"), "H10");
        set.put(new CellRef("H11"), "H11");
        set.put(new CellRef("H12"), "H12");
        set.put(new CellRef("I11"), "I11");
        set.put(new CellRef("I12"), "I12");
        return new RangeWrapper(set, new HashMap<CellRef, String>(), rangeDef);
    }

    @Test
    public void testGetValueIntInt() {
        String rangeRef = "A1:F14";
        RangeWrapper range = getTestData(rangeRef);
        ArrayList<MoveBuilder> list = MoveLoader.loadMovesFromRange(range);

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
    public void testLoadMoveSubRangeColumns() {
        String fullTab = "A1:F12";
        RangeWrapper range = getTestData(fullTab);

        ArrayList<MoveBuilder> list = MoveLoader.loadMovesFromRange(range);

        assertFalse(isMoveInList(list, "b10"));
        assertFalse(isMoveInList(list, "C10"));
        assertTrue(isMoveInList(list, "b2", "B3"));
        assertTrue(isMoveInList(list, "b5", "B6"));

        assertTrue(isMoveInList(list, "d2"));
        assertTrue(isMoveInList(list, "d6"));

    }

    @Test
    public void testLoadMoveOffsetSubRange() {
        String fullTab = "A1:F12";
        RangeWrapper range = getTestData(fullTab);

        ArrayList<MoveBuilder> list = MoveLoader.loadMovesFromRange(range);

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
    public void testLoadMoveSubRangeRows() {
        String fullTab = "A1:F14";
        RangeWrapper range = getTestData(fullTab);
        ArrayList<MoveBuilder> list = MoveLoader.loadMovesFromRange(range);

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
    public void advancedMoveLoadTest() {
        String fullTab = "B12:D20";
        RangeWrapper range = getTestData(fullTab);
        ArrayList<MoveBuilder> list = MoveLoader.loadMovesFromRange(range);

        assertTrue(isMoveInList(list, "c15"));
        assertTrue(isMoveInList(list, "c15", "B16"));
        assertFalse(isMoveInList(list, "c18"));
    }

    @Test
    public void testListMoveLoad() {
        String playerRef = "G9:J14";
        RangeWrapper range2 = getTestData(playerRef);

        ArrayList<MoveBuilder> secondaryMoves = MoveLoader.loadMovesFromRange(range2);
        assertTrue(isMoveInList(secondaryMoves, "H10", "H12"));
        assertFalse(isMoveInList(secondaryMoves, "H11"));
        assertFalse(isMoveInList(secondaryMoves, "I11"));
        assertFalse(isMoveInList(secondaryMoves, "I12"));
    }

}