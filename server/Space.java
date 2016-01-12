package server;

import java.util.List;

/**
 * Created by jjk on 1/7/16.
 * this class is used to contain information about a space on the board.
 * it shows which other spaces belong to the column and row.
 * and contains the position.
 */
public class Space {
    private int[] position = new int[2];
    private List<Space> column;
    private List<Space> row;

    public List<Space> getColumn() {
        return column;
    }

    public void setColumn(List<Space> column) {
        this.column = column;
    }
    public void addColumn(List<Space> column) {
        this.column.addAll(column);
    }
    public void addColumn(Space s) {
        this.column.add(s);
    }
    public void addRow(List<Space> row) {
        this.row.addAll(row);
    }
    public void addRow(Space s) {
        this.row.add(s);
    }

    public void setRow(List<Space> row) {
        this.row = row;
    }

    public List<Space> getRow() {
        return row;
    }

    public int[] getPosition() {
        return position;
    }
    //@ requires position.length == 2;
    public void setPosition(int[] position) {
        this.position = position;
    }
}