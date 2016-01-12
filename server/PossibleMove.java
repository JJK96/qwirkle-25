package server;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjk on 1/7/16.
 *
 * The possibleMove class is used to store a place where a move can be made
 * It contains the possible Shapes and Colors that that place can contain
 * It can hand the rows and columns over to the stone that is placed here.
 *
 */
public class PossibleMove extends Space {
    private List<Stone.Shape> possibleShape;
    private List<Stone.Color> possibleColor;

    public PossibleMove(Stone.Shape[] possibleShape, Stone.Color[] possibleColor) {
        this.possibleShape = new ArrayList<Stone.Shape>();
        this.possibleColor = new ArrayList<Stone.Color>();
        for (Stone.Shape s : possibleShape) {
            this.possibleShape.add(s);
        }
        for (Stone.Color c : possibleColor) {
            this.possibleColor.add(c);
        }
    }

    /**
     * edits the stone that is placed at this location so that it has the correct row and column.
     * also updates changes it's own possible shapes and moves.
     * @param stone
     * @return
     */
    public Stone fill (Stone stone) {
        stone.setColumn(getColumn());
        stone.setRow(getRow());
        stone.setPosition(getPosition());
        stone.place();
        for (Space s : stone.getColumn()) {
            s.addColumn(s);
        }
        for (Space s : stone.getRow()) {
            s.addRow(s);
        }
        return stone;
    }

    public int updatePossibilities() {
        for (Space s : getColumn()) {
            if (s instanceof Stone) {
                removePossibleColor(((Stone) s).getColor());
                removePossibleShape(((Stone) s).getShape());
            }
        }
        for (Space s : getRow()) {
            if (s instanceof Stone) {
                removePossibleColor(((Stone) s).getColor());
                removePossibleShape(((Stone) s).getShape());
            }
        }
        return possibleColor.size() * possibleShape.size();
    }

    public void setPossibleShape(List<Stone.Shape> possibleShape) {
        this.possibleShape = possibleShape;
    }
    public void removePossibleShape(Stone.Shape shape) {
        possibleShape.remove(shape);
    }
    public void removePossibleColor(Stone.Color color) {
        possibleColor.remove(color);
    }
    public List<Stone.Shape> getPossibleShape() {
        return possibleShape;
    }

    /**
     * removes all shapes that appear in the possibleShape list and not in the argument.
     * this is useful for updating the possiblemove after a stone has been placed next to it.
     * @param possibleShape
     */
    public void retainShapes(List<Stone.Shape> possibleShape) {
        possibleShape.retainAll(possibleShape);
    }
    public void setPossibleColor(List<Stone.Color> possibleColor) {
        this.possibleColor = possibleColor;
    }
    public List<Stone.Color> getPossibleColor() {
        return possibleColor;
    }
    public void retainColors(List<Stone.Color> possibleColor) {
        this.possibleColor.retainAll(possibleColor);
    }

    public boolean acceptable(Stone stone) {
        return false;
    }

    @Override
    public String toString() {
        return "[ " + getPosition().getX() + " , " + getPosition().getY() + " ]";
    }
}
