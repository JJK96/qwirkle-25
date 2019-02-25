package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import shared.*;
import shared.Protocol;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerPlayer extends Thread {

	// @ private invariant in != null;
	private BufferedReader in;
	// @ private invariant out != null;
	private BufferedWriter out;
	// @ private invariant server.getPlayers().contains(this) ==> name != null;
	private String name;
	// @ private invariant points >= 0;
	private int points;
	private List<Stone> stones;
	private ServerGame game;
	// @ private invariant options.equals("");
	private String options;
	private Server server;

	public ServerPlayer(Socket sock, Server server) throws IOException {
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		points = 0;
		stones = new ArrayList<Stone>();
		options = "";
		this.server = server;

	}

	/*
	 * @ requires pointsToAdd >= 0; ensures getPoints() == \old (getPoints()) +
	 * pointsToAdd;
	 */
	public void addpoints(int pointsToAdd) {
		this.points += pointsToAdd;
	}

	@Override
	public void run() {
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				System.out.println("From " + name + ": " + line);
				String[] words = line.split(Protocol.SPLIT);
				if (words[0].equals(Protocol.JOINAANTAL)) {
					if (words.length >= 2 && !inGame()) {
						int number = Integer.parseInt(words[1]);
						if (number >= 1 && number <= 4) {
							server.joinGame(this, number);
						}
					}
				} else if (words[0].equals(Protocol.WHICHPLAYERS)) {
					sendPlayers();
				} else if (inGame()) {
					if (words[0].equals(Protocol.PLACE)) {
						if (game.getCurrentPlayer().equals(this)) {
							place(words);
						}
					} else if (words[0].equals(Protocol.TRADE)) {
						if (game.getCurrentPlayer().equals(this)) {
							trade(words);
						}
					}
				}
			}
			shutdown();
		} catch (IOException e) {
			shutdown();
		}
	}

	// @ requires inGame() && board != null;
	public boolean canPlay(Board board) {
		for (Stone s : stones) {
			for (PossibleMove p : board.getPossibleMoves().values()) {
				if (p.acceptable(s)) {
					return true;
				}
			}
		}
		return false;
	}

	public void register() throws IOException, InvalidCommandException {
		String line = in.readLine();
		if (line != null) {
			String[] words = line.split(Protocol.SPLIT);
			if (words.length >= 2 && words[0].equals(Protocol.REGISTER)) {
				name = words[1];
				if (words.length > 2) {
					for (int i = 2; i < words.length; i++) {
						options += words[i] + Protocol.SPLIT;
					}
				}
			} else {
				throw new InvalidCommandException(line);
			}
		} else {
			throw new InvalidCommandException();
		}
	}

	// @ requires inGame();
	private void trade(String[] inputArray) {
		if (inputArray.length >= 2) {
			try {
				List<Stone> stonesToTrade = Protocol.stringToStoneList(inputArray);
				game.trade(stonesToTrade);
			} catch (InvalidStoneException e) {
				error(Protocol.ErrorCode.WRONGTURN);
			} catch (InvalidMoveException e) {
				error(Protocol.ErrorCode.WRONGTURN);
			}
		} else {
			error(Protocol.ErrorCode.WRONGCOMMAND);
		}
	}

	// @ requires inGame();
	private void place(String[] inputArray) {
		if (inputArray.length >= 3 && inputArray.length % 2 == 1) {
			List<Stone> stonesToPlace = new ArrayList<Stone>();
			List<Position> positions = new ArrayList<Position>();
			try {
				stonesToPlace = Protocol.stringToPlacedStoneList(inputArray);
				positions = Protocol.stringToPlacePositionList(inputArray);
				game.placeStones(stonesToPlace, positions);
			} catch (InvalidCommandException e) {
				error(Protocol.ErrorCode.WRONGCOMMAND);
			} catch (InvalidMoveException e) {
				error(Protocol.ErrorCode.WRONGTURN);
			}
		} else {
			error(Protocol.ErrorCode.WRONGCOMMAND);
		}
	}

	/*
	 * @ requires getStones().containsAll(stoneList); ensures (\forall int i;
	 * i<stoneList.size(); !getStones().contains(stoneList.get(i)));
	 */
	public void removeStones(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			stones.remove(s);
		}
	}

	// @ ensures getStones().containsAll(stonesToGive);
	public void giveStones(List<Stone> stonesToGive) {
		this.stones.addAll(stonesToGive);
		String stoneString = "";
		for (Stone s : stonesToGive) {
			stoneString += s.toUsableString() + Protocol.SPLIT;
		}
		if (!stonesToGive.isEmpty()) {
			sendMessage(Protocol.NEWSTONES + Protocol.SPLIT + stoneString);
		}
	}

	public void acknowledge() {
		sendMessage(Protocol.ACKNOWLEDGE + Protocol.SPLIT);
	}

	public void sendMessage(String msg) {
		System.out.println("To " + name + ": " + msg);
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public void error(Protocol.ErrorCode code) {
		sendMessage(Protocol.ERROR + Protocol.SPLIT + code.ordinal());
	}

	public /* @ pure */ String getThisName() {
		return name;
	}

	public /* @ pure */ int getPoints() {
		return points;
	}

	public /* @ pure */ List<Stone> getStones() {
		return stones;
	}

	public/* @ pure */ ServerGame getGame() {
		return game;
	}

	public void setGame(ServerGame game) {
		this.game = game;
	}

	public /* @ pure */ String getOptions() {
		return options;
	}

	public void sendPlayers() {
		sendMessage(Protocol.PLAYERS + Protocol.SPLIT + server.getPlayers(getThisName()));
	}

	public /* @ pure */ boolean inGame() {
		return getGame() != null;
	}

	public void shutdown() {
		try {
			out.close();
			in.close();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		server.removePlayer(this);
	}

	public void reset() {
		this.points = 0;
		this.stones = new ArrayList<Stone>();
	}
}
