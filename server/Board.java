package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

    private Map<Position, Stone> stones;
    private Map<Position, PossibleMove> possibleMoves;

    public Board() {
        reset();
    }

    public void reset() {
        stones = new HashMap<Position, Stone>();
        possibleMoves = new HashMap<Position, PossibleMove>();
        PossibleMove init = new PossibleMove(Stone.Shape.values(), Stone.Color.values(), new Position(0,0));
        possibleMoves.put(init.getPosition(), init);
    }

    public Map<Position, PossibleMove> getPossibleMoves() {
        return possibleMoves;
    }

    public void update() {

    }

    public Map<Position, Stone> getStones() {
        return this.stones;
    }

    public Board deepCopy() {
        return null;
    }

    public boolean isValidMove(int x, int y, Stone stone) {
        PossibleMove p = possibleMoves.get(new Position(x,y));
        return p != null && p.acceptable(stone);
    }

    public void makeMove(int x, int y, Stone stone) {
        if (isValidMove(x, y, stone)) {
            makeMove(stone, possibleMoves.get(new Position(x,y)));
        }
    }

    //@ requires possibleMoves.contains(place) && place.acceptable(stone));
    public void makeMove(Stone stone, PossibleMove place) {
        stone = place.fill(stone);
        stones.put(stone.getPosition(), stone);
        possibleMoves.remove(place.getPosition());
        for (Position p : possibleMoves.keySet()) {
            if (p.getX() == stone.getPosition().getX() || p.getY() == stone.getPosition().getY()) {
                addPossibleMove(p);
            }
        }
        Position pos = stone.getPosition();
        addPossibleMove(pos.above());
        addPossibleMove(pos.below());
        addPossibleMove(pos.right());
        addPossibleMove(pos.left());
    }

    public void addPossibleMove(Position pos) {
        if (!stones.keySet().contains(pos)) {
            PossibleMove newPM = new PossibleMove(Stone.Shape.values(), Stone.Color.values(), pos);
            Stone above;
            Stone below;
            Stone right;
            Stone left;
            if ((above = stones.get(new Position(pos.getX(), pos.getY() - 1))) != null) {
                newPM.addColumn(above.getColumn());
            }
            if ((below = stones.get(new Position(pos.getX(), pos.getY() + 1))) != null) {
                newPM.addColumn(below.getColumn());
            }
            if ((right = stones.get(new Position(pos.getX() + 1, pos.getY()))) != null) {
                newPM.addRow(right.getRow());
            }
            if ((left = stones.get(new Position(pos.getX() - 1, pos.getY()))) != null) {
                newPM.addRow(left.getRow());
            }
            if (newPM.updatePossibilities() != 0) {
                possibleMoves.put(newPM.getPosition(), newPM);
            }
        }
    }

    public int[] getBoundaries() {
        int SmallestX = 0;
        int BiggestX = 0;
        int SmallestY = 0;
        int BiggestY = 0;
        for (Position p : stones.keySet()) {
            if (p.getX() > BiggestX) {
                BiggestX = p.getX();
            }
            if (p.getX() < SmallestX) {
                SmallestX = p.getX();
            }
            if (p.getY() > BiggestY) {
                BiggestY = p.getY();
            }
            if (p.getY() < SmallestY) {
                SmallestY = p.getY();
            }
        }
        return new int[]{SmallestX, SmallestY, BiggestX, BiggestY};
    }

    public String toString() {
        int[] boundaries = getBoundaries();
        String res = "";
        for (int i = boundaries[1]; i <= boundaries[3]; i++) {
            for (int j = boundaries[0]; j <= boundaries[2]; j++) {
                Stone s = null;
                if ((s = stones.get(new Position(j,i)))!= null) {
                    res += s;
                }
                else {
                    res += "     ";
                }
            }
            res += "\n";
        }
        res += "\nPossible moves: ";
        Object[] possibleMovesArray = possibleMoves.values().toArray();
        for (int i =0; i < possibleMovesArray.length; i++) {
            res += "\n" + i + " : " + possibleMovesArray[i];
        }
        return res;
    }
}
