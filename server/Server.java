package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
	private static final String USAGE = "usage: " + Server.class.getName() + " <port>";

	/** Start een Server-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(USAGE);
			System.exit(0);
		}

		Server server = new Server(Integer.parseInt(args[0]));
		server.run();

	}

	private int port;
	private List<ClientHandler> threads;
	private List<Game> games;

	/** Constructs a new Server object */
	public Server(int portArg) {
		this.port = portArg;
		threads = new ArrayList<ClientHandler>();
		games = new ArrayList<Game>();
	}

	/**
	 * Listens to a port of this Server if there are any Clients that would like
	 * to connect. For every new socket connection a new ClientHandler thread is
	 * started that takes care of the further communication with the Client.
	 */
	public void run() {
		try {
			ServerSocket sock = new ServerSocket(port);
			while (true) {
				Socket acceptedsocket = sock.accept();
				ClientHandler ch = new ClientHandler(this, acceptedsocket);
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
		for (ClientHandler c : threads) {
			c.sendMessage(msg);
		}
	}

	/**
	 * Add a ClientHandler to the collection of ClientHandlers.
	 * 
	 * @param handler
	 *            ClientHandler that will be added
	 */
	public void addHandler(ClientHandler handler) {
		threads.add(handler);
	}

	/**
	 * Remove a ClientHandler from the collection of ClientHanlders.
	 * 
	 * @param handler
	 *            ClientHandler that will be removed
	 */
	public void removeHandler(ClientHandler handler) {
		threads.remove(handler);
	}

	public void addGame(Game game) {
		games.add(game);
	}

	public void removeGame(Game game) {
		games.remove(game);
	}

	public void sendErrorCode(int errorCode, Player player) {
		for (ClientHandler ch : threads) {
			if (ch.getClientName().equals(player.getName())) {
				ch.sendMessage("error" + errorCode);
				break;
			}
		}
	}

	public void notifyAllPlayerPointsPlacedMoves(Player player, int points, List<Stone> stones) {
		String msg = "placed " + player.getName() + " " + points;
		for (Stone s : stones) {
			msg = msg + " " + s.getColor().ordinal() + "," + s.getShape().ordinal() + " " + s.getPosition();
		}
		broadcast(msg);
	}

	public void newStones(List<Stone> stones, Player player) {
		String msg = "newstones";
		for (Stone s : stones) {
			msg = msg + " " + s.getColor().ordinal() + "," + s.getShape().ordinal();
		}
		for (ClientHandler ch : threads) {
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
