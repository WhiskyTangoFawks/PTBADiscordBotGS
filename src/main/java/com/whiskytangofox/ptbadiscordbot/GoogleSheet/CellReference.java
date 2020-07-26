package com.whiskytangofox.ptbadiscordbot.GoogleSheet;

import com.whiskytangofox.ptbadiscordbot.App;

import java.util.HashMap;
import java.util.Objects;

public class CellReference {

    private final int column;
    private final int row;

    private static final HashMap<Integer, String> intToCol = new HashMap<Integer, String>();
    private static final HashMap<String, Integer> colToInt = new HashMap<String, Integer>();

    public CellReference(String cellRef) {
        if (intToCol.isEmpty() || colToInt.isEmpty()) {
            setupHashMaps();
        }
        cellRef = cellRef.toUpperCase();
        if (cellRef.contains("!")) {
            cellRef = cellRef.split("!")[1];
        }
        if (cellRef.contains(":")) {
            App.logger.warn("CellRef instantiated with a range instead of a single cell reference, dropping 2nd coordinate");
            cellRef = cellRef.split(":")[0];
        }
        int firstDigit = getFirstDigit(cellRef);
        column = colToInt.get(cellRef.substring(0,firstDigit));
        row = Integer.parseInt(cellRef.substring(firstDigit));
    }

    public CellReference(int column, int row) {
        if (intToCol.isEmpty() || colToInt.isEmpty()) {
            setupHashMaps();
        }
        this.column = column;
        this.row = row;
    }

    public String getColumnString(){
        return intToCol.get(column);
    }
    public int getColumnInt(){
        return column;
    }
    public int getRow(){return row;}
    public String getCellRef(){
        return getColumnString()+row;
    }
    public String getColumnOffsetCellRef(int offset){
        return intToCol.get(column+offset) + row;
    }

    private int getFirstDigit(String range){
        for (int i = 0; i < range.length(); i++){
            if (Character.isDigit(range.charAt(i))){
                return i;
            }
        }
        return -1;
    }

    private static void setupHashMaps(){
        Character[] columnName = {null, 'A'};
        for (int i = 1;i < 676; i++){
            String temp = columnName[0]==null?columnName[1].toString():columnName[0].toString()+columnName[1].toString();
            intToCol.put(i, temp);
            colToInt.put(temp, i);
            if (columnName[1] == 'Z'){
                columnName[1] = 'A';
                if (columnName[0] == null){
                    columnName[0]='A';
                } else {
                    columnName[0]++;
                }
            } else { //columnName[1] != 'Z'
                columnName[1]++;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellReference cellRef = (CellReference) o;
        return column == cellRef.column &&
                row == cellRef.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

}
