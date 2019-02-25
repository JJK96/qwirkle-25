package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import client.LittleBetterStrategy;
import shared.*;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerGame extends Thread {
	public static final int MAXSTONES = 6;
	// @ private invariant players.length >= 1 && players.length <= 4;
	private ServerPlayer[] players;
	// @ private invariant players.length == size;
	private int size;
	private int playernum;
	private boolean running;
	// @ private invariant board != null;
	private Board board;
	// @ private invariant bag != null;
	private List<Stone> bag;
	// @ private invariant server != null;
	private Server server;
	// @ private invariant currentPlayer != null;
	private ServerPlayer currentPlayer;
	private Lock lock;
	private Condition playerDone;
	private ServerPlayer winner;
	private int firstMoveLength;

	/**
	 * Creates a game with the given size.
	 *
	 * @param size
	 */
	/*
	 * @ requires size >= 1 && size <= 4; ensures players.length == size;
	 */
	public ServerGame(int size, Server server) {
		this.size = size;
		players = new ServerPlayer[size];
		playernum = 0;
		running = false;
		this.server = server;
		lock = new ReentrantLock();
		playerDone = lock.newCondition();
		init();
	}

	/**
	 * Initializes the game. Creates a new board. Creates a new bag with all
	 * stones.
	 */
	/*
	 * @ ensures board.getStones().isEmpty() && board.getPossibleMoves().size()
	 * == 1 && bag.size() == 3*36;
	 */
	private void init() {
		board = new Board();
		bag = new LinkedList<Stone>();
		for (Stone.Color c : Stone.Color.values()) {
			for (Stone.Shape s : Stone.Shape.values()) {
				for (int i = 0; i < 3; i++) {
					bag.add(new Stone(s, c));
				}
			}
		}
	}

	/**
	 * Gives turns to the correct players according to the rules of the game.
	 *
	 * The first turn goes to the player who can make the best move as
	 * determined by LittleBetterStrategy. Then the next player is the next in
	 * the list that can make a move. If no player can make a move or there is a
	 * winner the game is ended and the players are properly notified.
	 */
	/*
	 * @ ensures getCurrentPlayer() != null;
	 */
	@Override
	public void run() {
		lock.lock();
		boolean movesLeft = true;
		running = true;
		System.out.println("game started with players: " + getPlayerNames());
		giveInitialStones();
		int currentplayernum = determineFirstPlayer();
		while (!hasWinner() && isRunning() && !this.isInterrupted() && movesLeft) {
			System.out.println("Stones: " + currentPlayer.getStones());
			sendTurn();
			try {
				playerDone.await();
			} catch (InterruptedException e) {
				running = false;
			}
			int loop = 0;
			do {
				loop++;
				currentplayernum = (currentplayernum + 1) % playernum;
				currentPlayer = players[currentplayernum];
			} while (bag.isEmpty() && !currentPlayer.canPlay(board) && loop <= playernum);
			if (loop > playernum) {
				movesLeft = false;
			}
		}
		lock.unlock();
		if (hasWinner() || !movesLeft) {
			endgame();
		} else {
			broadcast(Protocol.ERROR + Protocol.SPLIT + 
								Protocol.ErrorCode.PLAYERDISCONNECTED.ordinal());
		}
		end();
	}

	/**
	 * Determines which player should move first.
	 *
	 * The player which can make the best move according to the
	 * LittleBetterStrategy cam move first.
	 *
	 * @return the player that can make the best move.
	 */
	/*
	 * @ ensures beginner >=0 && beginner < players.length;
	 */
	private int determineFirstPlayer() {
		List<Stone> firstMove = new ArrayList<Stone>();
		LittleBetterStrategy strat = new LittleBetterStrategy(0);
		int beginner = 0;
		for (int i = 0; i < players.length; i++) {
			List<Stone> move = strat.getMove(board, players[i].getStones());
			if (move.size() > firstMove.size()) {
				firstMove = move;
				beginner = i;
			}
		}
		firstMoveLength = firstMove.size();
		currentPlayer = players[beginner];
		System.out.println(firstMove);
		return beginner;
	}

	/**
	 * Give all players their initial six stones.
	 */
	// @ ensures (\forall int i; i<players.length; players[i].getStones().size()
	// == 6);
	private void giveInitialStones() {
		for (ServerPlayer p : players) {
			List<Stone> stones = new ArrayList<Stone>();
			for (int i = 0; i < MAXSTONES; i++) {
				stones.add(takeStone());
			}
			p.giveStones(stones);
		}
	}

	/**
	 * Adds a player to this game.
	 * 
	 * @param player
	 * @return the number of players that are still to join before the game can
	 *         be started.
	 */
	/*
	 * @ requires player != null && !player.inGame(); ensures player.getGame()
	 * == this && player.getStones().size() == 0 && player.getPoints() == 0;
	 */
	public int addPlayer(ServerPlayer player) {
		if (playernum < size) {
			players[playernum] = player;
			playernum += 1;
		}
		player.setGame(this);
		player.reset();
		return size - playernum;
	}

	/**
	 * Handles the leaving of a player. Just ends the game.
	 * 
	 * @param player
	 */
	// @ ensures isRunning() == false;
	public void removePlayer(ServerPlayer player) {
		this.interrupt();
		running = false;
	}

	/**
	 * Ends the game and removes it from the server.
	 */
	/*
	 * @ ensures !server.getGames().contains(this) && (\forall int i;
	 * i<players.length; players[i].getGame() == null);
	 */
	private void end() {
		for (ServerPlayer p : players) {
			p.setGame(null);
		}
		server.removeGame(this);
	}

	/**
	 * @return whether or not the game has a winner.
	 */
	public /* @ pure */ Boolean hasWinner() {
		return winner != null;
	}

	/**
	 * Gets the current player.
	 * 
	 * @return the player that currently has the turn.
	 */
	public /* @ pure */ ServerPlayer getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Gets the number of players the game has.
	 * 
	 * @return the size of the game.
	 */
	public /* @ pure */ int getSize() {
		return size;
	}

	/**
	 * Gets the number of players currently online.
	 * 
	 * @return the number of players.
	 */
	public /* @ pure */ int getPlayernum() {
		return playernum;
	}

	/**
	 * @return whether or not the game is running.
	 */
	public /* @ pure */ boolean isRunning() {
		return running;
	}

	/**
	 * Sends all players in the game the turn command to indicate the specified
	 * player can make his move.
	 */
	private void sendTurn() {
		broadcast(Protocol.TURN + Protocol.SPLIT + currentPlayer.getThisName());
	}

	/**
	 * Removes a stone from the bag.
	 * 
	 * @return the stone taken.
	 */
	// @ requires !bag.isEmpty();
	private Stone takeStone() {
		Stone s = bag.get((int) Math.floor(Math.random() * bag.size()));
		bag.remove(s);
		return s;
	}

	/**
	 * Gets the names of the players in the game.
	 * 
	 * @return a string with the names of all players in the game.
	 */
	// @ ensures (\forall int i; i<players.length;
	// \result.contains(players[i].getThisName()));
	public /* @ pure */ String getPlayerNames() {
		String playernames = "";
		for (ServerPlayer p : players) {
			playernames += p.getThisName() + Protocol.SPLIT;
		}
		return playernames;
	}

	/**
	 * Sends the endgame command to the players in this game.
	 */
	private void endgame() {
		broadcast(Protocol.ENDGAME);
	}

	/**
	 * Sends the specified message to all players in this game.
	 * 
	 * @param msg
	 */
	private void broadcast(String msg) {
		for (ServerPlayer p : players) {
			p.sendMessage(msg);
		}
	}

	/**
	 * Sends the placed command to the players in the game.
	 *
	 * @param stones
	 * @param positions
	 * @param points
	 */
	/*
	 * @ requires getBoard().getStones().containsAll(stones);
	 */
	private void placed(List<Stone> stones, List<Position> positions, int points) {
		String message = Protocol.PLACED + Protocol.SPLIT 
						+ currentPlayer.getThisName() + Protocol.SPLIT;
		message += points + Protocol.SPLIT;
		for (int i = 0; i < stones.size(); i++) {
			message += stones.get(i).toUsableString()
					+ Protocol.SPLIT + positions.get(i).toUsableString()
					+ Protocol.SPLIT;
		}
		broadcast(message);
	}

	/**
	 * Sends the traded command to the players in the game.
	 * 
	 * @param stones
	 */
	// @ requires gotAllStones(stones) && getBag().size() >= stones.size();
	private void traded(List<Stone> stones) {
		String message = Protocol.TRADED + Protocol.SPLIT 
						+ currentPlayer.getThisName() + Protocol.SPLIT
						+ stones.size();
		broadcast(message);
	}

	/**
	 * Places the specified stones on the given positions. Throws
	 * InvalidMoveException if the move was incorrect.
	 *
	 * If the player who made the move has no stones and the bag is empty the
	 * game has a winner.
	 *
	 * @param stones
	 * @param positions
	 * @throws InvalidMoveException
	 */
	// @ requires stones.size() == positions.size();
	public void placeStones(List<Stone> stones, List<Position> positions) 
			throws InvalidMoveException {
		if (gotAllStones(stones)) {
			lock.lock();
			try {
				if (board.getStones().isEmpty() && stones.size() < firstMoveLength) {
					System.out.println(stones.size());
					throw new InvalidMoveException();
				}
				board.makeMoves(positions, stones);
				currentPlayer.removeStones(stones);
				currentPlayer.giveStones(takeSomeStones(stones.size()));
				int points = board.calculatePoints(stones, positions);
				currentPlayer.addpoints(points);
				placed(stones, positions, points);
				if (currentPlayer.getStones().isEmpty() && bag.isEmpty()) {
					currentPlayer.addpoints(6);
					winner = getWinner();
				}
				playerDone.signal();
			} catch (InvalidMoveException e) {
				System.out.println("resetting board");
				board = board.deepCopy();
				throw new InvalidMoveException();
			} finally {
				lock.unlock();
			}
		} else {
			throw new InvalidMoveException();
		}
	}

	/**
	 * Determines which player has the most points and has thus won this game.
	 * 
	 * @return
	 */
	// @ ensures (\forall int i; i<players.length; players[i].getPoints() <=
	// \result.getPoints());
	public /* @ pure */ ServerPlayer getWinner() {
		ServerPlayer newWinner = players[0];
		for (ServerPlayer p : players) {
			if (p.getPoints() > newWinner.getPoints()) {
				newWinner = p;
			}
		}
		return newWinner;
	}

	/**
	 * Checks if the current player has all stones that are to be placed or
	 * swapped.
	 * 
	 * @param stonelist
	 * @return true if the player has all specified stones. false if the player
	 *         does not have all specified stones.
	 */
	public /* @ pure */ boolean gotAllStones(List<Stone> stonelist) {
		return currentPlayer.getStones().containsAll(stonelist);
	}

	/**
	 * Trades the specified stones. If the player tries to trade more stones
	 * than there are available in the bag an InvalidMoveException is thrown.
	 * 
	 * @param stones
	 * @throws InvalidMoveException
	 */
	/*
	 * @ ensures (gotAllStones(stones) && bag.size() >= stones.size() &&
	 * !getBoard().getStones().isEmpty()) ==> getBag().containsAll(stones) &&
	 * (\forall int i; i<stones.size();
	 * !getCurrentPlayer().getStones().contains(stones.get(i)));
	 */
	public void trade(List<Stone> stones) throws InvalidMoveException {
		if (gotAllStones(stones) && bag.size() >= stones.size() && !board.getStones().isEmpty()) {
			lock.lock();
			currentPlayer.removeStones(stones);
			currentPlayer.giveStones(takeSomeStones(stones.size()));
			bag.addAll(stones);
			traded(stones);
			playerDone.signal();
			lock.unlock();
		} else {
			throw new InvalidMoveException();
		}
	}

	/**
	 * Takes the given number of stones from the bag.
	 *
	 * @param stoneNum
	 * @return the stones taken from the bag.
	 */
	/*
	 * @ ensures bag.size() + stoneNum == \old (bag.size()) && (\forall int i;
	 * i<\result.size(); !getBag().contains(\result.get(i)));
	 */
	private List<Stone> takeSomeStones(int stoneNum) {
		int stoneNumber = stoneNum;
		if (stoneNum > bag.size()) {
			stoneNumber = bag.size();
		}
		List<Stone> stonelist = new ArrayList<Stone>();
		for (int i = 0; i < stoneNumber; i++) {
			stonelist.add(takeStone());
		}
		return stonelist;
	}

	public /* @ pure */ Board getBoard() {
		return board;
	}

	public /* @ pure */ List<Stone> getBag() {
		return bag;
	}
}
