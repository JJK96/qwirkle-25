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
    }

	public void addPossibleMove(int[] pos) {
		newPM = new PossibleMove()
		if (stones.get(new int[] {pos[0], pos[-1]}) != null) {

		}
	}

	public String toString() {
        return null;
	}
}
