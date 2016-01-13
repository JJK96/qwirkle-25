package server;

import java.util.ArrayList;
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
	private List<Stone.Shape> possibleShape;
	private List<Stone.Color> possibleColor;
	private Stone.Color rowCommonColor = null;
	private Stone.Color columnCommonColor = null;
	private Stone.Shape rowCommonShape = null;
	private Stone.Shape columnCommonShape = null;


	/**
	 * Creates a possiblemove
	 * 
	 * @param
	 * @param
	 */
	public PossibleMove(Position p) {
		super();
		setPosition(p);
		this.possibleShape = new ArrayList<Stone.Shape>();
		this.possibleColor = new ArrayList<Stone.Color>();
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
		for (Space s : stone.getColumn()) {
			s.addColumn(stone);
		}
		for (Space s : stone.getRow()) {
			s.addRow(stone);
		}
		stone.addColumn(stone);
		stone.addRow(stone);
		return stone;
	}

	/**
	 * Updates all the possibilities for this possibleMove
	 * 
	 * @return ??
	 */
	public int updatePossibilities() {
		if (getColumn().size() ==6 || getRow().size() == 6) {
			return 0;
		}
		return 1;
	}

	/**
	 * ??
	 * 
	 * @param possibleShape
	 */
	public void setPossibleShape(List<Stone.Shape> possibleShape) {
		this.possibleShape = possibleShape;
	}

	/**
	 * returns the common shape or null if there is no common shape (or if the list only contains 1 stone);
	 * @param list
	 * @return
     */
	public boolean commonShape(List<Space> list, Stone stone) {
		for (Space s : list) {
			if (((Stone) s).getShape() != stone.getShape()) {
				return false;
			}
		}
		return true;
	}
	public boolean noCommonShape(List<Space> list, Stone stone) {
		for (Space s : list) {
			if (((Stone) s).getShape() == stone.getShape()) {
				return false;
			}
		}
		return true;
	}

	public boolean commonColor(List<Space> list, Stone stone) {
		for (Space s : list) {
			if (((Stone) s).getColor() != stone.getColor()) {
				return false;
			}
		}
		return true;
	}

	public boolean noCommonColor(List<Space> list, Stone stone) {
		for (Space s : list) {
			if (((Stone) s).getColor() == stone.getColor()) {
				return false;
			}
		}
		return true;
	}


	/**
	 * ??
	 * 
	 * @param shape
	 */
	public void removePossibleShape(Stone.Shape shape) {
		possibleShape.remove(shape);
	}

	/**
	 * ??
	 * 
	 * @param color
	 */
	public void removePossibleColor(Stone.Color color) {
		possibleColor.remove(color);
	}

	/**
	 * ??
	 * 
	 * @return
	 */
	public List<Stone.Shape> getPossibleShape() {
		return possibleShape;
	}

	/**
	 * removes all shapes that appear in the possibleShape list and not in the
	 * argument. this is useful for updating the possiblemove after a stone has
	 * been placed next to it.
	 * 
	 * @param possibleShape
	 */
	public void retainShapes(List<Stone.Shape> possibleShape) {
		possibleShape.retainAll(possibleShape);
	}

	/**
	 * ??
	 * 
	 * @param possibleColor
	 */
	public void setPossibleColor(List<Stone.Color> possibleColor) {
		this.possibleColor = possibleColor;
	}

	/**
	 * ??
	 * 
	 * @return
	 */
	public List<Stone.Color> getPossibleColor() {
		return possibleColor;
	}

	/**
	 * ??
	 * 
	 * @param possibleColor
	 */
	public void retainColors(List<Stone.Color> possibleColor) {
		this.possibleColor.retainAll(possibleColor);
	}

	/**
	 * ??
	 * 
	 * @param stone
	 * @return
	 */
	public boolean acceptable(Stone stone) {
		if ((commonColor(getRow(), stone) && noCommonShape(getRow(), stone)) || (commonShape(getRow(), stone) && noCommonColor(getRow(), stone))) {
			if ((commonColor(getColumn(), stone) && noCommonShape(getColumn(), stone)) || (commonShape(getColumn(), stone) && noCommonColor(getColumn(), stone))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the Sring-representation of a possiblemove
	 */
	@Override
	public String toString() {
		return "[ " + getPosition().getX() + " , " + getPosition().getY() + " ]";
	}
}
