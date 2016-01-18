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
	
	public Client getClient() {
		return client;
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
