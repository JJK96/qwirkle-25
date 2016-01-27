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
	 * Sets the column of this space to the given stonelist.
	 *
	 * @param column
	 */
	public void setColumn(List<Stone> column) {
		this.column = column;
	}

	/**
	 * Adds all stones in the stoneList to the column of this space.
	 *
	 * @param stoneList
	 */
	public void addColumn(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			addColumn(s);
		}
	}

	/**
	 * Adds a stone to the column.
	 *
	 * @param s
	 */
	public void addColumn(Stone s) {
		this.column.add(s);
	}

	/**
	 * Adds every stone in the given stoneList to the row.
	 *
	 * @param stoneList
	 */
	public void addRow(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			addRow(s);
		}
	}

	/**
	 * Adds the stone to the row.
	 * @param s
	 */
	public void addRow(Stone s) {
		this.row.add(s);
	}

	/**
	 * Sets the row of the space.
	 * 
	 * @param row
	 */
	public void setRow(List<Stone> row) {
		this.row = row;
	}

	public List<Stone> getRow() {
		return row;
	}

	/**
	 * Gets the position of this space.
	 * @return the position.
     */
	public Position getPosition() {
		return position;
	}

	/**
	 * Sets the position to the given position.
	 * @param position
     */
	public void setPosition(Position position) {
		this.position = position;
	}
}