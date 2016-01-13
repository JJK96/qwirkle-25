package server;

import client.ClientHandlerExample;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerExample {
	private static final String USAGE = "usage: " + ServerExample.class.getName() + " <port>";

	/** Start een ServerExample-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}

		ServerExample serverExample = new ServerExample(Integer.parseInt(args[0]));
		serverExample.run();

	}

	private int port;
	private List<ClientHandlerExample> threads;
	private List<Game> games;

	/** Constructs a new ServerExample object */
	public ServerExample(int portArg) {
		this.port = portArg;
		threads = new ArrayList<ClientHandlerExample>();
		games = new ArrayList<Game>();
	}

	/**
	 * Listens to a port of this ServerExample if there are any Clients that
	 * would like to connect. For every new socket connection a new
	 * ClientHandlerExample thread is started that takes care of the further
	 * communication with the ClientExample.
	 */
	public void run() {
		try {
			ServerSocket sock = new ServerSocket(port);
			while (true) {
				Socket acceptedsocket = sock.accept();
				ClientHandlerExample ch = new ClientHandlerExample(this, acceptedsocket);
				addHandler(ch);
				ch.announce();
				ch.start();
				String[] names = new String[2];
				Game game = new Game(names);
				addGame(game);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void print(String message) {
		System.out.println(message);
	}

	/**
	 * Sends a message using the collection of connected ClientHandlers to all
	 * connected Clients.
	 * 
	 * @param msg
	 *            message that is send
	 */
	public void broadcast(String msg) {
		for (ClientHandlerExample c : threads) {
			c.sendMessage(msg);
		}
	}

	/**
	 * Add a ClientHandlerExample to the collection of ClientHandlers.
	 * 
	 * @param handler
	 *            ClientHandlerExample that will be added
	 */
	public void addHandler(ClientHandlerExample handler) {
		threads.add(handler);
	}

	/**
	 * Remove a ClientHandlerExample from the collection of ClientHanlders.
	 * 
	 * @param handler
	 *            ClientHandlerExample that will be removed
	 */
	public void removeHandler(ClientHandlerExample handler) {
		threads.remove(handler);
	}

	public void addGame(Game game) {
		games.add(game);
	}

	public void removeGame(Game game) {
		games.remove(game);
	}

	public void sendErrorCode(int errorCode, Player player) {
		for (ClientHandlerExample ch : threads) {
			if (ch.getClientName().equals(player.getName())) {
				ch.sendMessage("error" + errorCode);
				break;
			}
		}
	}

	public void notifyAllPlayerPointsPlacedMoves(Player player, int points, List<Stone> stones) {
		String msg = "placed " + player.getName() + " " + points;
		for (Stone s : stones) {
			msg = msg + " " + s.getShape().ordinal() + "," + s.getColor().ordinal() + " " + s.getPosition();
		}
		broadcast(msg);
	}

	public void newStones(List<Stone> stones, Player player) {
		String msg = "newstones";
		for (Stone s : stones) {
			msg = msg + " " + s.getShape().ordinal() + "," + s.getColor().ordinal();
		}
		for (ClientHandlerExample ch : threads) {
			if (ch.getClientName().equals(player.getName())) {
				ch.sendMessage(msg);
				break;
			}
		}
	}

	public void playerTraded(Player player, int amount) {
		String msg = "traded " + player.getName() + " " + amount;
		broadcast(msg);
	}

	public void notifyAllCurrentPlayer(Player player) {
		String msg = "turn " + player.getName();
		broadcast(msg);
	}

	public void endGame() {
		broadcast("endgame");
	}

}
