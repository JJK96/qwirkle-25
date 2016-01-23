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

	public void incMoveCount() {
		moveCount++;
	}
	
	public int getBag() {
		return bag;
	}

	public int getMoveCount() {
		return moveCount;
	}

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

	public Player[] getPlayers() {
		return players;
	}

	/**
	 * Checks if the choice the humanplayer made is a valid possiblemove.
	 * 
	 * @return true if choice is valid, false otherwise
	 */
	public boolean isValidInt(int choice, Board b) {
		return choice < b.getPossibleMoves().size() && choice >= -1;
	}

	public boolean isValidIntNotMinusOne(int choice) {
		return choice < getPossibleMoves().size() && choice >= 0;
	}

	public boolean isValidIntStonesRange(int choice) {
		return choice < getCurrentPlayer().getStones().size() && choice >= -1;
	}

	public boolean isValidIntStonesRangeFrom0(int choice) {
		return choice < getCurrentPlayer().getStones().size() && choice >= 0;
	}

	/**
	 * Get the player whose turn it is.
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
		setChanged();
		notifyObservers();
	}
	public void setCurrentPlayer(String name) throws InvalidCommandException {
		Player p = findPlayer(name);
		if (p != null) {
			currentPlayer = p;
		} else {
			throw new InvalidCommandException();
		}
	}
	public void giveStones(List<Stone> stonelist, String playername) throws InvalidMoveException {
		Player p = findPlayer(playername);
        p.takeStones(stonelist);
	}

	public Player getWinner() {
		Player winner = players[0];
		for (Player p : players) {
			if (p.getPoints() > winner.getPoints()) {
				winner = p;
			}
		}
		return winner;
	}

	public void addPoints(String s) {
		currentPlayer.addPoints(Integer.parseInt(s));
	}
}
