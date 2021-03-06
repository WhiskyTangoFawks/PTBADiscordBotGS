package com.whiskytangofox.ptbadiscordbot.GoogleSheet;

import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.GridData;
import com.whiskytangofox.ptbadiscordbot.App;

import java.util.Collection;
import java.util.HashMap;

public class RangeWrapper {

    private final HashMap<CellReference, String> values;
    private final HashMap<CellReference, String> notes;
    public final CellReference firstCell;
    public final CellReference lastCell;
    public final String tab;

    public RangeWrapper(GridData data, String tab, String rangeDef) {
        this.tab = tab;
        firstCell = new CellReference(rangeDef.split(":")[0]);
        lastCell = new CellReference(rangeDef.split(":")[1]);
        this.values = new HashMap<CellReference, String>();
        this.notes = new HashMap<CellReference, String>();
        for (int i = 0; i < lastCell.getColumnInt() - firstCell.getColumnInt() + 1; i++) {
            for (int j = 0; j < lastCell.getRow() - firstCell.getRow() + 1; j++) {
                int c = i + firstCell.getColumnInt();
                int r = j + firstCell.getRow();
                try {
                    CellData cell = data.getRowData().get(j).getValues().get(i);
                    CellReference cellref = new CellReference(c, r);
                    this.values.put(cellref, cell.getFormattedValue());
                    this.notes.put(cellref, cell.getNote());
                } catch (IndexOutOfBoundsException e) {
                    //value = null;
                } catch (NullPointerException e) {
                    //value = null;
                }

                //App.logger.info(c + ", " + r + " : " + range.get(j).get(i).toString());
            }
        }
    }

    public RangeWrapper(HashMap<CellReference, String> dataValues, HashMap<CellReference, String> dataNotes, String tab, String rangeDef) {
        firstCell = new CellReference(rangeDef.split(":")[0]);
        lastCell = new CellReference(rangeDef.split(":")[1]);
        this.tab = tab;
        this.notes = dataNotes;
        this.values = dataValues;
    }

    /**
     *
     * @param parent range to take the subrange from
     * @param subrangeAnchor the base range, to be offset
     * @param offset the number of cells to shift subRangeAnchor to the right
     */
    public RangeWrapper(RangeWrapper parent, String subrangeAnchor, int offset) {
        CellReference subrangeFirst = new CellReference(subrangeAnchor.split(":")[0]);
        CellReference subrangeLast = new CellReference(subrangeAnchor.split(":")[1]);
        firstCell = new CellReference(subrangeFirst.getColumnInt() + offset, subrangeFirst.getRow());
        lastCell = new CellReference(subrangeLast.getColumnInt() + offset, subrangeLast.getRow());
        this.tab = parent.tab;
        this.notes = parent.notes;
        this.values = parent.values;
    }

    public String getValue(int sheetColumn, int sheetRow){
        try {
            if (sheetColumn < firstCell.getColumnInt() || sheetColumn > lastCell.getColumnInt() ||
                    sheetRow < firstCell.getRow() || sheetRow > lastCell.getRow()){
                //App.logger.warn("Tried to access value outside range");
            }
            return values.get(new CellReference(sheetColumn, sheetRow));
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public void setValue(int sheetColumn, int sheetRow, String value) {
        if (sheetColumn < firstCell.getColumnInt() || sheetColumn > lastCell.getColumnInt() ||
                sheetRow < firstCell.getRow() || sheetRow > lastCell.getRow()) {
            //App.logger.warn("Tried to access value outside range");
        }
        values.put(new CellReference(sheetColumn, sheetRow), value);
    }

    public String getNote(int sheetColumn, int sheetRow) {
        try {
            if (sheetColumn < firstCell.getColumnInt() || sheetColumn > lastCell.getColumnInt() ||
                    sheetRow < firstCell.getRow() || sheetRow > lastCell.getRow()) {
                App.logger.warn("Tried to access value outside range");
            }
            return notes.get(new CellReference(sheetColumn, sheetRow));
        } catch (IndexOutOfBoundsException e) {
            return null;
        } catch (NullPointerException e){
            return null;
        }
    }

    public String getValue(String cellRef){
        CellReference cell = new CellReference(cellRef);
        return getValue(cell.getColumnInt(), cell.getRow());
    }

    public String getNote(String cellRef){
        CellReference cell = new CellReference(cellRef);
        return getNote(cell.getColumnInt(), cell.getRow());
    }

    public Collection<String> getValueSet(){
        return values.values();
    }

}
