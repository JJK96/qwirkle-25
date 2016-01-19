package client;

import shared.*;
import java.util.List;
import java.util.Map;

public class ClientGame {

	private List<Stone> bag;
	private Player[] players;
	private Player currentPlayer;
	private Board board;
	private Client client;

	/**
	 * Creates a game with the names of the players.
	 * 
	 * @param names
	 */
	public ClientGame(String[] names, Client client) {
		players = new Player[names.length];
		this.board = new Board();
		this.client = client;
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(client.getClientName())) {
				if (client.getComputerPlayerBool()) {
					Player p = new ComputerPlayer(names[i], this, client.getStrategy());
					players[i] = p;
					client.setYou(p);
				} else {
					Player p1 = new HumanPlayer(names[i], this);
					players[i] = p1;
					client.setYou(p1);
				}
			} else {
				players[i] = new Player(names[i], this);
			}
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
		return (choice < getPossibleMoves().size() && choice >= -1);
	}

	public boolean isValidIntStonesRange(int choice) {
		return (choice < getCurrentPlayer().getStones().size() && choice >= -1);
	}

	public boolean isValidIntStonesRangeFrom0(int choice) {
		return (choice < getCurrentPlayer().getStones().size() && choice >= 0);
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

}
