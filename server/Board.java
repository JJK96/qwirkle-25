package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board {

	private Map<int[], Stone> stones;
    private Map<int[], PossibleMove> possibleMoves;

	public Board() {
        reset();
	}

	public void reset(){
        stones = new HashMap<int[], Stone>();
        possibleMoves = new HashMap<int[], PossibleMove>();
        PossibleMove init = new PossibleMove(Stone.Shape.values(), Stone.Color.values());
        init.setPosition(new int[] {0,0});
        possibleMoves.put(init.getPosition(), init);
	}

	public void update(){
		
	}

	public Map<int[], Stone> getStones(){
	    return this.stones;
	}

	public Board deepCopy(){
	    return null;
	}

	public boolean isValidMove(int row, int column){
	    return false;
	}

	public void makeMove(int row, int column) {
        if (isValidMove(row,column)) {

        }
    }
    //@ requires possibleMoves.contains(place) && place.acceptable(stone));
    public void makeMove(Stone stone, PossibleMove place) {
        stone=place.fill(stone);
        stones.put(stone.getPosition(), stone);
        int[] pos = place.getPosition();
        //add the new possible moves to the array. update old ones if they exist.
        int[] above = new int[] {pos[0], pos[1]-1};
        int[] below = new int[] {pos[0], pos[1]+1};
        int[] right = new int[] {pos[0]+1, pos[1]};
        int[] left = new int[] {pos[0] -1, pos[1]};
        PossibleMove newPossibleMove = null;
        if (( newPossibleMove = possibleMoves.get(above)) != null ) {
            newPossibleMove.retainColors(place.getPossibleColor());
            newPossibleMove.retainShapes(place.getPossibleShape());
            if (newPossibleMove.getPossibleColor().isEmpty()) {
                possibleMoves.remove(newPossibleMove);
            }
            else {
                newPossibleMove.addColumn(place.getColumn());
                newPossibleMove.addRow(place.getRow());
            }
        }

        possibleMoves.remove(place);
    }

	public String toString() {
        return null;
	}
}
