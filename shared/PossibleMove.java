package shared;

import java.util.List;

/**
 * Created by jjk on 1/7/16.
 *
 * The possibleMove class is used to store a place where a move can be made. It
 * contains the possible Shapes and Colors that that place can contain. It can
 * hand the rows and columns over to the stone that is placed here.
 *
 */
public class PossibleMove extends Space {

	/**
	 * Creates a possiblemove.
	 * 
	 * @param
	 * @param
	 */
	public PossibleMove(Position p) {
		super();
		setPosition(p);
	}

	/**
	 * edits the stone that is placed at this location so that it has the
	 * correct row and column.
	 * And updates the stones that are in the same row or column as the new 
	 * stone with the correct rows and columns.
	 *
	 * @param stone
	 * @return ??
	 */
	public Stone fill(Stone stone) {
		stone.setColumn(getColumn());
		stone.setRow(getRow());
		stone.setPosition(getPosition());
		stone.place();
		stone.addColumn(stone);
		stone.addRow(stone);
		for (Stone s : stone.getColumn()) {
            s.setColumn(stone.getColumn());
		}
		for (Stone s : stone.getRow()) {
			s.setRow(stone.getRow());
		}
		return stone;
	}

	/**
	 * Checks if there are any possibilities left for this possible move.
	 *
	 * @return 0 if there are no possibilities left, or 1 if there are.
	 */
	public int updatePossibilities() {
		if (getColumn().size() >= 6 || getRow().size() >= 6) {
			return 0;
		} else if (doubleRowColumn()) {
			return 0;
		}
		return 1;
	}

	/**
	 * Checks for duplicate stones in the row/column of this possible move.
	 * @return 	true if duplicates are found.
	 * 			false if there are no duplicates.
     */
	private boolean doubleRowColumn() {
		for (Stone s1 : getRow()) {
			for (Stone s2 : getRow()) {
				if (s1.equals(s2) && s1 != s2) {
					return true;
				}
			}
		}
		for (Stone s1 : getColumn()) {
			for (Stone s2 : getColumn()) {
				if (s1.equals(s2) && s1 != s2) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns the common shape or null if there is no common shape (or if the
	 * list only contains 1 stone).
	 * 
	 * @param list
	 * @return
	 */
	private boolean commonShape(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getShape() != stone.getShape()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if there is no common shape in the given stonelist.
	 * @param list
	 * @param stone
     * @return 	true if there is no common shape.
	 * 			false if there is.
     */
	private boolean noCommonShape(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getShape() == stone.getShape()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if there is a common color in the given stonelist.
	 * @param list
	 * @param stone
     * @return 	true if there is a common color.
	 * 			false if there is not.
     */
	private boolean commonColor(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getColor() != stone.getColor()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if there is no common color in the given stonelist.
	 * @param list
	 * @param stone
     * @return 	true if there is no common color.
	 * 			false if there is.
     */
	private boolean noCommonColor(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getColor() == stone.getColor()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the given stone is acceptable at this position. According to the rules of the game.
	 *
	 * @param stone
	 * @return
	 */
	public boolean acceptable(Stone stone) {
		if ((commonColor(getRow(), stone) && noCommonShape(getRow(), stone))
						|| (commonShape(getRow(), stone) && noCommonColor(getRow(), stone))) {
			if ((commonColor(getColumn(), stone) && noCommonShape(getColumn(), stone))
								|| (commonShape(getColumn(), stone) 
										&& noCommonColor(getColumn(), stone))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Sring-representation of a possiblemove.
	 */
	@Override
	public String toString() {
		return "[ " + getPosition().getX() + " , " + getPosition().getY() + " ]";
	}
}
