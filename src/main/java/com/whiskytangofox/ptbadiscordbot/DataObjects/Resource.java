package com.whiskytangofox.ptbadiscordbot.DataObjects;

import com.whiskytangofox.ptbadiscordbot.GoogleSheet.CellReference;

import java.util.ArrayList;
import java.util.List;

public class Resource {

    public Integer min = null;
    public Integer max = null;

    final ArrayList<CellReference> cells = new ArrayList();

    public Resource(CellReference ref) {
        cells.add(ref);
    }

    public Resource(ArrayList<CellReference> listCellRefs) {
        cells.addAll(listCellRefs);
    }

    public void add(CellReference ref) {
        cells.add(ref);
    }

    public CellReference get(int index) {
        return cells.get(index);
    }

    public int size() {
        return cells.size();
    }

    public List<CellReference> getList() {
        return cells;
    }
}
