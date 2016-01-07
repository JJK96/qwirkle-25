package server;

import java.util.ArrayList;
import java.util.List;

public class Board {

	private List<Stone> stones;
    private List<PossibleMove> possibleMoves;

	public Board() {
        reset();
	}

	public void reset(){
        stones = new ArrayList<Stone>();
        possibleMoves = new ArrayList<PossibleMove>();
        PossibleMove init = new PossibleMove(Stone.Shape.values(), Stone.Color.values());
        init.setPosition(new int[] {0,0});
        possibleMoves.add(init);
	}

	public void update(){
		
	}

	public List<Stone> getStones(){
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
        stones.add(stone);
        int[] pos = place.getPosition();
        //add the new possible moves to the array. update old ones if they exist.
        possibleMoves.remove(place);
    }

	public String toString() {
        return null;
	}
}
