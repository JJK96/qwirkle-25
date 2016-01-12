package server;

import java.util.ArrayList;
import java.util.List;

public class Player {

	private String name;
	private int points;
	private List<Stone> stones;

	
	public Player(String name) {
		stones = new ArrayList<Stone>();
		points = 0;
		this.name = name;
	}

	public void makeMove(){
		
	}

	public void takeStones(){
		
	}

	public void swapStones(){
		
	}

	public void addPoints() {

	}

	public int getPoints() {
		return points;
	}
}
