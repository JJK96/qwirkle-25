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
	public List<Stone> makeMove() {
		return null;
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
	 * Take stones out of the bag after the player has made his move
	 * 
	 * @param stones
	 */
	public void takeStones(List<Stone> stones) {
		stones.addAll(stones);
	}

	/**
	 * Swap specified stones with the bag
	 * 
	 * @param swapStones
	 */
	public void swapStones(List<Stone> swapStones) {
		stones.removeAll(swapStones);
		game.getClient().trade(swapStones);

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
