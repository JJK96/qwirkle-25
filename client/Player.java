package client;

import java.util.ArrayList;
import shared.*;
import java.util.List;

public class Player {

	private String name;
	private int points;
	private List<Stone> stones;
	private ClientGame game;

	/**
	 * Create a player with specified name and game
	 * 
	 * @param name
	 */
	public Player(String name, ClientGame game) {
		stones = new ArrayList<Stone>();
		points = 0;
		this.name = name;
		this.game = game;

	}

	/**
	 * The move the player makes
	 */
	public void makeMove() {

	}

	public boolean isValidPossiblemove(PossibleMove p) {
		for (int i = 0; i < stones.size(); i++) {
			if (getGame().getBoard().isValidMove(p, stones.get(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Get the game the player plays
	 * 
	 * @return game
	 */
	public ClientGame getGame() {
		return game;
	}

	/**
	 * Get the name of the player
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Get the stones the player has
	 * 
	 * @return stones
	 */
	public List<Stone> getStones() {
		return stones;
	}

	/**
	 * Makes a string representation of the stones the player has. Before every
	 * stone is a number added so choosing is easy for the view.
	 * 
	 * @return String representation of the stones with a number for every
	 *         stone.
	 */
	public String stonesToString() {
		String stoneString = "";
		if (stones.size() == 0) {
			return stoneString;
		} else {
			for (int i = 0; i < stones.size(); i++) {
				stoneString += i + " " + stones.get(i).toString() + "\n";
			}
		}
		return stoneString;
	}

	/**
	 * Take stones out of the bag after the player has made his move
	 * 
	 * @param stones
	 */
	public void takeStones(List<Stone> stones) {
		this.stones.addAll(stones);
	}

	/**
	 * Removes the specified stone from the players stones.
	 * 
	 * @param stone
	 */
	public void removeStone(Stone stone) {
		stones.remove(stone);
	}

	/**
	 * Add the points the player earned by his last move to his points
	 * 
	 * @param points
	 */
	public void addPoints(int points) {
		this.points = this.points + points;
	}

	/**
	 * Get the points of the player
	 * 
	 * @return points
	 */
	public int getPoints() {
		return points;
	}
}
