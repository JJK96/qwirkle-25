package client;

import shared.*;

import java.util.List;
import java.util.Map;
import java.util.Observable;

public class ClientGame extends Observable {

	private Player[] players;
	private Player currentPlayer;
	private Board board;
	private Client client;
	private Player winner;
	private int bag;

	/**
	 * Creates a game with the names of the players.
	 * 
	 * @param names
	 */
	public ClientGame(String[] names, Client client) {
		bag = 3*(Stone.Shape.values().length * Stone.Color.values().length) - 6 * (players.length -1);
		players = new Player[names.length];
		this.board = new Board();
		this.client = client;
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(client.getClientName())) {
				Player p;
				if (client.getComputerPlayerBool()) {
					p = new ComputerPlayer(names[i], this, client.getStrategy());
				} else {
					p = new HumanPlayer(names[i], this);
					p.addObserver(client.getView());
				}
				players[i] = p;
				client.setYou(p);
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
	public boolean isValidInt(int choice, Board b) {
		return (choice < b.getPossibleMoves().size() && choice >= -1);
	}

	public boolean isValidIntNotMinusOne(int choice) {
		return (choice < getPossibleMoves().size() && choice >= 0);
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

	public Player findPlayer(String name) {
		for (Player p : players) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	public void makeMove(List<Position> positions, List<Stone> stones) throws InvalidMoveException {
		try {
			board.makeMoves(positions, stones);
		} catch (InvalidMoveException e) {
			board = board.deepCopy();
			throw e;
		}
		bag -= positions.size();
		notifyObservers();
	}

	public void removeFromBag(int num) throws InvalidMoveException {
		if (bag >= num) {
			bag -= num;
		} else throw new InvalidMoveException();
	}
	public void setCurrentPlayer(String name) throws InvalidCommandException {
		Player p = findPlayer(name);
		if (currentPlayer != null) {
			currentPlayer = p;
		} else {
			throw new InvalidCommandException();
		}
	}
	public void giveStones(List<Stone> stonelist, String playername) throws InvalidMoveException {
		for (Player p : players) {
			if (p.getName().equals(playername)) {
				p.takeStones(stonelist);
				removeFromBag(stonelist.size());
			}
		}
	}

	public Player getWinner() {
		return winner;
	}
}
