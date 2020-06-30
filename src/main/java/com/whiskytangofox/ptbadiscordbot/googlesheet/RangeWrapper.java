package com.whiskytangofox.ptbadiscordbot.googlesheet;

import com.whiskytangofox.ptbadiscordbot.App;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class RangeWrapper {

    private final HashMap<CellRef, String> range;
    public final CellRef firstCell;
    public final CellRef lastCell;

    public RangeWrapper(List<List<Object>> range, String rangeDef) {
        firstCell = new CellRef(rangeDef.split(":")[0]);
        lastCell = new CellRef (rangeDef.split(":")[1]);
        this.range = new HashMap<CellRef, String>();
        for (int i = 0; i < lastCell.getColumnInt()-firstCell.getColumnInt()+1; i++){
            for (int j = 0; j < lastCell.getRow()-firstCell.getRow()+1; j++){
                int c = i+firstCell.getColumnInt();
                int r = j+firstCell.getRow();
                String value = "";
                try {
                    value = range.get(j).get(i).toString();
                } catch (IndexOutOfBoundsException e){
                     //value = null;
                } catch (NullPointerException e){
                    //value = null;
                }
                this.range.put(new CellRef(c,r), value);
                //App.logger.info(c + ", " + r + " : " + range.get(j).get(i).toString());
            }
        }
    }

    public RangeWrapper(HashMap<CellRef, String> range, String rangeDef) {
        firstCell = new CellRef(rangeDef.split(":")[0]);
        lastCell = new CellRef (rangeDef.split(":")[1]);
        this.range = range;
    }

    /**
     *
     * @param parent range to take the subrange from
     * @param subrangeAnchor the base range, to be offset
     * @param offset the number of cells to shift subRangeAnchor to the right
     */
    public RangeWrapper(RangeWrapper parent, String subrangeAnchor, int offset){
        CellRef subrangeFirst = new CellRef(subrangeAnchor.split(":")[0]);
        CellRef subrangeLast = new CellRef (subrangeAnchor.split(":")[1]);
        firstCell = new CellRef(subrangeFirst.getColumnInt()+offset, subrangeFirst.getRow());
        lastCell = new CellRef(subrangeLast.getColumnInt()+offset, subrangeLast.getRow());

        range = parent.range;
    }

    public RangeWrapper getSubRange(String subrange){
        return new RangeWrapper(range, subrange);
    }


    public String getValue(int sheetColumn, int sheetRow){
        try {
            if (sheetColumn < firstCell.getColumnInt() || sheetColumn > lastCell.getColumnInt() ||
                    sheetRow < firstCell.getRow() || sheetRow > lastCell.getRow()){
                App.logger.warn("Tried to access value outside range");
            }
            return range.get(new CellRef(sheetColumn, sheetRow));
        } catch (IndexOutOfBoundsException e){
            return null;
        } catch (NullPointerException e){
            return null;
        }
    }

    public String getValue(String cellRef){
        CellRef cell = new CellRef(cellRef);
        return getValue(cell.getColumnInt(), cell.getRow());
    }

    public String getColumnOffsetValue(String cell, int columnOffset){
        CellRef cellref = new CellRef(cell);
        return getValue(cellref.getColumnInt()+columnOffset, cellref.getRow());
    }

    public Collection<String> getValueSet(){
        return range.values();
    }

}
