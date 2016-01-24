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

	/**
	 * creates a space.
	 */
	public Space() {
		this.column = new ArrayList<Stone>();
		this.row = new ArrayList<Stone>();
	}

	/**
	 * Get the column of a space.
	 * 
	 * @return List of stones in that column
	 */
	public List<Stone> getColumn() {
		return column;
	}

	/**
	 * Sets the column of a list of stones.
	 * 
	 * @param column
	 */
	public void setColumn(List<Stone> column) {
		this.column = column;
	}

	/**
	 * Adds a column to every stone of the specified list.
	 * 
	 * @param stoneList
	 */
	public void addColumn(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			addColumn(s);
		}
	}

	/**
	 * Adds a column to the stone.
	 * 
	 * @param s
	 */
	public void addColumn(Stone s) {
		this.column.add(s);
	}

	/**
	 * Adds a row to every stone in the specified list.
	 * 
	 * @param stoneList
	 */
	public void addRow(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			addRow(s);
		}
	}

	/**
	 * Adds a row to the stone.
	 * 
	 * @param s
	 */
	public void addRow(Stone s) {
		this.row.add(s);
	}

	/**
	 * Sets the row of the //////////////////////////////////////////////////////////////////////////
	 * @param row
	 */
	public void setRow(List<Stone> row) {
		this.row = row;
	}

	public List<Stone> getRow() {
		return row;
	}

	public Position getPosition() {
		return position;
	}

	// @ requires position.length == 2;
	public void setPosition(Position position) {
		this.position = position;
	}
}