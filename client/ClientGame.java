package client;

import shared.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Observable;

public class ClientGame extends Observable {

	private Player[] players;
	private Player currentPlayer;
	private Board board;
	private Client client;
	private int moveCount = 0;
	private int bag;

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
				Player p;
				if (client.getComputerPlayerBool()) {
					p = new ComputerPlayer(names[i], this, client.getStrategy());
					client.setYou(p);
				} else {
					p = new HumanPlayer(names[i], this);
					client.setYou(p);
					p.addObserver(client.getView());
				}
				players[i] = p;
				client.setYou(p);
			} else {
				players[i] = new Player(names[i], this);
			}
		}
		bag = 3 * (Stone.Shape.values().length
				* Stone.Color.values().length) - 6 * (players.length);
	}

	/**
	 * Get all the PossibleMoves.
	 * 
	 * @return map of PossibleMoves
	 */
	public Map<Position, PossibleMove> getPossibleMoves() {
		return board.getPossibleMoves();
	}

	/**
	 * This is called when a move is made, so the movecount is always
	 * as big as the amount of moves that are made.
	 */
	public void incMoveCount() {
		moveCount++;
	}
	
	/**
	 * Get the size of the bag on the server.
	 * 
	 * @return size of bag.
	 */
	public int getBag() {
		return bag;
	}

	/**
	 * Get the amount of moves that have been made.
	 * 
	 * @return amount of moves
	 */
	public int getMoveCount() {
		return moveCount;
	}

	/**
	 * Get the client that runs this game.
	 * 
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * Get the board which is currently used.
	 * 
	 * @return board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * Get the players that play this game.
	 * 
	 * @return the players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Checks if the choice of the humanplayer is a valid stone of the player.
	 * -1 if the player ends his turn.
	 * 
	 * @param choice
	 * @return true if choice is valid, false otherwise
	 */
	public boolean isValidIntStonesRange(int choice) {
		return choice < getCurrentPlayer().getStones().size() && choice >= -1;
	}

	/**
	 * Get the player whose turn it is.
	 * 
	 * @return currentplayer
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Finds the specified player.
	 * 
	 * @param name
	 * @return the player object that has the specified name
	 * 			or null if there is no player with that name
	 */
	public Player findPlayer(String name) {
		for (Player p : players) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	/**
	 * Makes the moves on the board that come from the server when another client has
	 * made his moves. The board is always updated because of this.
	 * 
	 * @param positions
	 * @param stones
	 * @throws InvalidMoveException
	 */
	public void makeMove(List<Position> positions, List<Stone> stones) throws InvalidMoveException {
		try {
			board.makeMoves(positions, stones);
		} catch (InvalidMoveException e) {
			board = board.deepCopy();
			throw e;
		}
		bag -= positions.size();
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Sets the player with the specified name as currentplayer.
	 * 
	 * @param name
	 * @throws InvalidCommandException
	 */
	public void setCurrentPlayer(String name) throws InvalidCommandException {
		Player p = findPlayer(name);
		if (p != null) {
			currentPlayer = p;
		} else {
			throw new InvalidCommandException();
		}
	}
	
	/**
	 * Gives the stones that came from the server to the player specified.
	 * 
	 * @param stonelist
	 * @param playername
	 * @throws InvalidMoveException
	 */
	public void giveStones(List<Stone> stonelist, String playername) throws InvalidMoveException {
		Player p = findPlayer(playername);
        p.takeStones(stonelist);
	}

	/**
	 * Gets the winner of the game by determining which player has the most points.
	 * 
	 * @return the winner of the game
	 */
	public Player getWinner() {
		Player winner = players[0];
		for (Player p : players) {
			if (p.getPoints() > winner.getPoints()) {
				winner = p;
			}
		}
		return winner;
	}

	/**
	 * Adds the points to the currentplayer.
	 * 
	 * @param s
	 */
	public void addPoints(String s) {
		currentPlayer.addPoints(Integer.parseInt(s));
	}

	@Override
	public String toString() {
		String pointsPlayers = "-------------------------------------------------------"
						+ "\nPLAYERPOINTS:\n\n";
		for (Player p : getPlayers()) {
			pointsPlayers += p.getName() + ": " + p.getPoints() + "\n";
		}
		pointsPlayers += "-------------------------------------------------------\n";
		return pointsPlayers + board.toString() + "\n-------------------------------------------------------";
	}
}
