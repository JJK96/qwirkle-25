package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import shared.*;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerGame extends Thread {
	public static final int MAXSTONES = 6;
	private ServerPlayer[] players;
	private int size;
	private int playernum;
	private boolean running;
	private Board board;
	public List<Stone> bag;
	private Server server;
	private ServerPlayer currentplayer;
	private Lock lock;
	private Condition playerDone;
	private ServerPlayer winner;

	/**
	 * Creates a game with the given size.
	 *
	 * @param size
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

	public void init() {
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

	@Override
	public void run() {
		lock.lock();
		boolean movesLeft = true;
		running = true;
		System.out.println("game started with players: " + getPlayerNames());
		giveInitialStones();
		int currentplayernum = (int) Math.floor(Math.random() * playernum);
		currentplayer = players[currentplayernum];
		while (!hasWinner() && isRunning() && !this.isInterrupted() && movesLeft) {
			System.out.println("Stones: " +currentplayer.getStones());
			sendTurn();
			try {
				playerDone.await();
			} catch (InterruptedException e) {
				running = false;
			}
			System.out.println("next player");
			int loop = 0;
			do {
				loop++;
				currentplayernum = (currentplayernum + 1) % playernum;
				currentplayer = players[currentplayernum];
			} while (bag.isEmpty() && !currentplayer.canPlay(board) && loop <= playernum);
			if (loop > playernum) {
				movesLeft = false;
			}
		}
		lock.unlock();
		if (hasWinner() || !movesLeft) {
			endgame();
		} else {
			broadcast(Protocol.ERROR + Protocol.SPLIT 
							+ Protocol.ErrorCode.PLAYERDISCONNECTED.ordinal());
		}
		end();
	}

	public void giveInitialStones() {
		for (ServerPlayer p : players) {
			List<Stone> stones = new ArrayList<Stone>();
			for (int i = 0; i < MAXSTONES; i++) {
				stones.add(takeStone());
			}
			p.giveStones(stones);
		}
	}

	public int addPlayer(ServerPlayer player) {
		if (playernum < size) {
			players[playernum] = player;
			playernum += 1;
		}
		player.setGame(this);
		player.reset();
		return size - playernum;
	}

	public void removePlayer(ServerPlayer player) {
		this.interrupt();
		running = false;
	}

	public void end() {
		for (ServerPlayer p : players) {
			p.setGame(null);
		}
		server.removeGame(this);
	}

	public Boolean hasWinner() {
		return winner != null;
	}

	public ServerPlayer getCurrentPlayer() {
		return currentplayer;
	}

	public int getSize() {
		return size;
	}

	public int getPlayernum() {
		return playernum;
	}

	public boolean isRunning() {
		return running;
	}

	private void sendTurn() {
		broadcast(Protocol.TURN + Protocol.SPLIT + currentplayer.getThisName());
	}

	//@ requires !bag.isEmpty();
	private Stone takeStone() {
		Stone s = bag.get((int) Math.floor(Math.random() * bag.size()));
		bag.remove(s);
		return s;
	}

	public String getPlayerNames() {
		String playernames = "";
		for (ServerPlayer p : players) {
			playernames += p.getThisName() + Protocol.SPLIT;
		}
		return playernames;
	}

	public void endgame() {
		broadcast(Protocol.ENDGAME);
	}

	public void broadcast(String msg) {
		for (ServerPlayer p : players) {
			p.sendMessage(msg);
		}
	}

	public void placed(List<Stone> stones, List<Position> positions, int points) {
		String message = Protocol.PLACED + Protocol.SPLIT + currentplayer.getThisName()
						+ Protocol.SPLIT;
		message += points + Protocol.SPLIT;
		for (int i = 0; i < stones.size(); i++) {
			message += stones.get(i).toUsableString() + Protocol.SPLIT 
						+ positions.get(i).toUsableString() + Protocol.SPLIT;
		}
		broadcast(message);
	}

	public void traded(List<Stone> stones) {
		String message = Protocol.TRADED + Protocol.SPLIT + currentplayer.getThisName() 
						+ Protocol.SPLIT + stones.size();
		broadcast(message);
	}

	//@ requires stones.size() == positions.size();
	public void placeStones(List<Stone> stones, List<Position> positions) 
			throws InvalidMoveException {
		if (gotAllStones(stones)) {
			lock.lock();
			try {
				board.makeMoves(positions, stones);
				//System.out.println("currentplayer placed stones");
				System.out.println(board);
				currentplayer.removeStones(stones);
				currentplayer.giveStones(takeSomeStones(stones.size()));
				int points = board.calculatePoints(stones, positions);
				currentplayer.addpoints(points);
				placed(stones, positions, points);
				if (currentplayer.getStones().isEmpty()) {
					currentplayer.addpoints(6);
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

	public ServerPlayer getWinner() {
		ServerPlayer newWinner = players[0];
		for (ServerPlayer p : players) {
			if (p.getPoints() > newWinner.getPoints()) {
				newWinner = p;
			}
		}
		return newWinner;
	}

	public boolean gotAllStones(List<Stone> stonelist) {
		return currentplayer.getStones().containsAll(stonelist);
	}

	public void trade(List<Stone> stones) throws InvalidMoveException {
		if (gotAllStones(stones) && bag.size() >= stones.size() && !board.getStones().isEmpty()) {
			lock.lock();
			currentplayer.removeStones(stones);
			currentplayer.giveStones(takeSomeStones(stones.size()));
			bag.addAll(stones);
			traded(stones);
			playerDone.signal();
			lock.unlock();
		} else {
			throw new InvalidMoveException();
		}
	}

	public List<Stone> takeSomeStones(int stoneNum) {
		int stoneNumber = stoneNum;
		if (stoneNum > bag.size()) {
			stoneNumber = bag.size();
		}
		List<Stone> stonelist = new ArrayList<>();
		for (int i = 0; i < stoneNumber; i++) {
			stonelist.add(takeStone());
		}
		return stonelist;
	}

}
