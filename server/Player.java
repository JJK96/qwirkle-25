package server;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private String name;
	private int points;
	private List<Stone> stones;
	private Game game;

	
	public Player(String name, Game game) {
		stones = new ArrayList<Stone>();
		points = 0;
		this.name = name;
		this.game = game;
		
	}

	public void makeMove(){
		
		
	}

	public void takeStones(List<Stone> stones){
		stones.addAll(stones);
	}

	public void swapStones(List<Stone> swapStones){
		stones.removeAll(swapStones);
		game.giveStones(this, swapStones.size());
		
	}

	public void addPoints(int points) {
		this.points = this.points + points;
	}

	public int getPoints() {
		return points;
	}
}
