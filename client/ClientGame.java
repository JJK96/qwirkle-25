package client;

import java.util.ArrayList;
import shared.*;
import java.util.List;
import java.util.Map;

public class ClientGame {

	private List<Stone> bag;
	private Player[] players;
	private Player currentPlayer;
	private Board board;
	private View view;
	private int size;
	private Client client;

	/**
	 * Creates a game with the names of the players.
	 * 
	 * @param names
	 */
	public ClientGame(String[] names, int size, Client client) {
		players = new Player[names.length];
		this.board = new Board();
		this.view = new View(this);
		this.size = size;
		this.client = client;
		for (int i = 0; i < names.length; i++) {
			players[i] = new Player(names[i], this);
		}
	}

	/**
	 * Get all the PossibleMoves
	 * 
	 * @return map of PossibleMoves
	 */
	public Map<Position, PossibleMove> getPossibleMoves() {
		return board.getPossibleMoves();
	}

	/**
	 * Checks if the game has a winner
	 * 
	 * @return true if the game has a winner, otherwise false
	 */
	public Boolean hasWinner() {
		return false;
	}

	/**
	 * Resets the game
	 */
	public void reset() {

	}

	/**
	 * Get all the stones in the bag
	 * 
	 * @return List off stones in the bag
	 */
	public List<Stone> stones() {
		return bag;
	}

	/**
	 * Get the view which is currently used
	 * 
	 * @return view
	 */
	public View getView() {
		return view;
	}

	/**
	 * Get the board which is currently used
	 * 
	 * @return board
	 */
	public Board getBoard() {
		return board;
	}

	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Checks if the choice the humanplayer made is a valid possiblemove
	 * 
	 * @return true if choice is valid, false otherwise
	 */
	public boolean isValidInt(int choice) {
		return (choice < getPossibleMoves().size() && choice >= 0);
	}

	/**
	 * Returns the amount of stones to the player out of the bag and removes the
	 * stones the player gets from the bag
	 * 
	 * @param player
	 * @param amount
	 */
	public void giveStones(Player player, int amount) {
		List<Stone> randomStones = new ArrayList<Stone>();
		for (int i = 0; i < amount; i++) {
			int place = ((int) Math.random() * (bag.size() + 1));
			randomStones.add(bag.get(place));
			bag.remove(place);
		}
		player.takeStones(randomStones);
	}

	/**
	 * Get the player whose turn it is
	 * 
	 * @return currentplayer
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}
	
	public void setCurrentPlayer(Player player) {
		currentPlayer = player;
	}

	/**
	 * Shows the GUI/TUI for the game
	 */
	public void showGUI() {

	}

}
