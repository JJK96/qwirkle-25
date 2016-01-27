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
	 * correct row and column. also updates changes it's own possible shapes and
	 * moves.
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
	 * Updates all the possibilities for this possibleMove.
	 * 
	 * @return ??
	 */
	public int updatePossibilities() {
		if (getColumn().size() >= 6 || getRow().size() >= 6) {
			return 0;
		} else if (doubleRowColumn()) {
			return 0;
		}
		return 1;
	}

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
	public boolean commonShape(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getShape() != stone.getShape()) {
				return false;
			}
		}
		return true;
	}

	public boolean noCommonShape(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getShape() == stone.getShape()) {
				return false;
			}
		}
		return true;
	}

	public boolean commonColor(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getColor() != stone.getColor()) {
				return false;
			}
		}
		return true;
	}

	public boolean noCommonColor(List<Stone> list, Stone stone) {
		for (Stone s : list) {
			if (s.getColor() == stone.getColor()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ??
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
