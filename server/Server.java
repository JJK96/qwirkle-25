package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import shared.InvalidCommandException;
import shared.InvalidNameException;
import shared.Protocol;

/**
 * Created by jjk on 1/14/16.
 */
public class Server {
	private List<ServerPlayer> players;
	private int port;
	private List<ServerGame> games;

	/**
	 * Constructs a Server object with the given port.
	 *
	 * @param port should be a number between 0 and 65535
	 */
	public Server(int port) {
		this.port = port;
		players = new ArrayList<ServerPlayer>();
		games = new ArrayList<ServerGame>();
	}

	/**
	 * Asks which port the server should run on and then starts the server on the given port.
	 * @param args
     */
	public static void main(String[] args) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		boolean valid = false;
		int port = 0;
		while (!valid) {
			System.out.println("enter a port number");
			try {
				String input = null;
				while (input == null) {
					input = in.readLine();
				}
				port = Integer.parseInt(input);
				if (port > 0 && port <= 65535) {
					valid = true;
				} else {
					System.out.println("Invalid port number");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				
			}
		}
		Server server = new Server(port);
		server.run();
	}

	/**
	 * Handles new clients that want to connect to the server.
	 * It first checks if the client has entered a correct name.
	 * If this is the case the server allows the client to join.
	 * If not, the server sends an error and disconnects the client.
	 */
	public void run() {
		System.out.println("Server is running :)");
		try {
			@SuppressWarnings("resource")
			ServerSocket sock = new ServerSocket(port);
			while (true) {
				Socket client = sock.accept();
				ServerPlayer newplayer = new ServerPlayer(client, this);
				try {
					newplayer.register();
					if (!isUniqueName(newplayer.getThisName())) {
						throw new InvalidNameException(newplayer.getThisName());
					}
					addPlayer(newplayer);
					newplayer.acknowledge();
					newplayer.sendPlayers();
					newplayer.start();
					joinLobby(newplayer);
				} catch (InvalidCommandException e) {
					newplayer.error(Protocol.ErrorCode.WRONGCOMMAND);
				} catch (InvalidNameException e) {
					newplayer.error(Protocol.ErrorCode.INVALIDNAME);
					newplayer.shutdown();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks if there is no user with the given name.
	 * @param name
	 * @return	true if there is no such user.
	 * 			false if there is a user with this name.
     */
	private boolean isUniqueName(String name) {
		boolean nameunique = true;
		for (ServerPlayer p : players) {
			if (p.getThisName().equals(name)) {
				nameunique = false;
			}
		}
		return nameunique;
	}

	/**
	 * Adds the given player to the player list.
	 * @param player
     */
	private void addPlayer(ServerPlayer player) {
		synchronized (players) {
			players.add(player);
		}
	}

	/**
	 * Removes the given player from the server and the game it is in.
	 * And notifies all other players of this event.
	 * @param player
     */
	public void removePlayer(ServerPlayer player) {
		synchronized (players) {
			players.remove(player);
			if (player.inGame()) {
				player.getGame().removePlayer(player);
			}
			broadcast(Protocol.DISCONNECT + Protocol.SPLIT + player.getThisName());
		}
	}

	/**
	 * Removes the specified game.
	 * @param game
     */
	public synchronized void removeGame(ServerGame game) {
		synchronized (games) {
			games.remove(game);
		}
	}

	/**
	 * Sends the specified message to all players currently online.
	 * @param msg
     */
	public void broadcast(String msg) {
		synchronized (players) {
			for (ServerPlayer p : players) {
				p.sendMessage(msg);
			}
		}
	}

	/**
	 * send the joinlobby command when the player joins.
	 * 
	 * @param player
	 */
	private void joinLobby(ServerPlayer player) {
		broadcast(Protocol.JOINLOBBY + Protocol.SPLIT + player.getThisName() 
						+ Protocol.SPLIT + player.getOptions());
	}

	/**
	 * returns the list of player names that do not equal the name argument this
	 * is used for getting an updated player list where the player asking for
	 * the list is not included.
	 * 
	 * @param name
	 */
	public String getPlayers(String name) {
		synchronized (players) {
			String out = "";
			for (ServerPlayer p : players) {
				if (!p.getThisName().equals(name)) {
					out += p.getThisName() + Protocol.SPLIT;
				}
			}
			return out;
		}
	}

	/**
	 * Handles the request from a client to join a game with the given number of players.
	 * Checks if there is a game with the specified size that is still waiting for players to join.
	 * If that is the case the player joins this game.
	 * If it is not, a new game is made with the given size.
	 * If the game is full it is started.
	 * @param player
	 * @param size
     */
	public void joinGame(ServerPlayer player, int size) {
		synchronized (games) {
			ServerGame game = null;
			for (ServerGame g : games) {
				if (g.getSize() == size && !g.isRunning()) {
					game = g;
					break;
				}
			}
			if (game == null) {
				game = new ServerGame(size, this);
				addGame(game);
			}
			if (game.addPlayer(player) == 0) {
				startGame(game);
			}
		}
	}

	/**
	 * Adds the specified game to the list of games.
	 * @param game
     */
	private void addGame(ServerGame game) {
		synchronized (games) {
			games.add(game);
		}
	}

	/**
	 * Starts the specified game and notifies all players.
	 * @param game
     */
	private synchronized void startGame(ServerGame game) {
		broadcast(Protocol.START + Protocol.SPLIT + game.getPlayerNames());
		game.start();
	}

	/**
	 * Gets a list of all players that are currently online.
	 * @return a list contianing all players online.
     */
	public List<ServerPlayer> getPlayers() {
		return players;
	}

	/**
	 * Gets a list of all games.
	 * @return a list containing all games on this server.
     */
	public List<ServerGame> getGames() {
		return games;
	}
}
