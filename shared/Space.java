package shared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjk on 1/7/16. this class is used to contain information about a
 * space on the board. It shows which other spaces belong to the column and row.
 * and contains the position.
 */
public class Space {
	private Position position;
	private List<Stone> column;
	private List<Stone> row;

	public Space() {
		this.column = new ArrayList<Stone>();
		this.row = new ArrayList<Stone>();
	}

	public List<Stone> getColumn() {
		return column;
	}

	public void setColumn(List<Stone> column) {
		this.column = column;
	}

	public void addColumn(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			addColumn(s);
		}
	}

	public void addColumn(Stone s) {
		this.column.add(s);
	}

	public void addRow(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			addRow(s);
		}
	}

	public void addRow(Stone s) {
		this.row.add(s);
	}

	public void setRow(List<Stone> row) {
		this.row = row;
	}

	public List<Stone> getRow() {
		return row;
	}

	public Position getPosition() {
		return position;
	}

	//@ requires position.length == 2;
	public void setPosition(Position position) {
		this.position = position;
	}
}