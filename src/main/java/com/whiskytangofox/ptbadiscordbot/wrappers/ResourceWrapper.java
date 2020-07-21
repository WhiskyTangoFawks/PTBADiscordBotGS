package com.whiskytangofox.ptbadiscordbot.wrappers;

import com.whiskytangofox.ptbadiscordbot.googlesheet.CellRef;

import java.util.ArrayList;
import java.util.List;

public class ResourceWrapper {

    public Integer min = null;
    public Integer max = null;
    final ArrayList<CellRef> cells = new ArrayList();

    public ResourceWrapper(CellRef ref) {
        cells.add(ref);
    }

    public ResourceWrapper(ArrayList<CellRef> listCellRefs) {
        cells.addAll(listCellRefs);
    }

    public void add(CellRef ref) {
        cells.add(ref);
    }

    public CellRef get(int index) {
        return cells.get(index);
    }

    public int size() {
        return cells.size();
    }

    public List<CellRef> getList() {
        return cells;
    }
}
