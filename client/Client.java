package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import shared.*;

public class Client extends Thread {

	/** Start een Client-applicatie op. */
	public static void main(String[] args) {
		Client client = new Client();
		client.start();
		client.startGame();
	}

	private String clientName;
	private Socket sock = null;
	private InetAddress hostname;
	private int port;
	private BufferedReader in;
	private BufferedWriter out;
	@SuppressWarnings("unused")
	private String[] options;
	private ClientGame game;
	private View view;
	private int aantal;
	private boolean registerSuccesfull = false;
	private boolean computerPlayerBool;
	private Strategy strategy;
	private List<String> players;
	private Player you;

	/**
	 * Creates a client through setup.
	 */
	public Client() {
		this.view = new View(this);
		players = new LinkedList<String>();
		init();
		while (!registerSuccesfull) {
			login();
		}
	}

	/**
	 * Asks the user to type the hostname of the server and then the portnumber of the server.
	 */
	public void init() {
		sock = null;
		hostname = null;
		while (hostname == null) {
			try {
				hostname = view.getHostName();
			} catch (UnknownHostException e) {
				view.print("Invalid hostname");
			}
		}
		port = view.getPort();
	}

	/**
	 * Asks the user to type his/her username and starts the streams of input 
	 * and output from and to the server. If the username is already taken the 
	 * user is asked to type a new one.
	 */
	public void login() {
		clientName = view.getClientName();
		while (sock == null) {
			try {
				sock = new Socket(hostname, port);
			} catch (IOException e) {
				view.print("Could not connect to server");
			}
		}
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		sendMessage(Protocol.REGISTER + Protocol.SPLIT + getClientName());
		String input = null;
		input = readString();
		if (input != null) {
			System.out.println(input);
			String[] inputArray = input.split(Protocol.SPLIT);
			if (inputArray[0].equals(Protocol.ACKNOWLEDGE)) {
				registerSuccesfull = true;
			} else if (inputArray[0].equals(Protocol.ERROR)) {
				if (inputArray.length == 1) {
					System.out.println("wrong error code");
				} else if (!inputArray[1].equals(Protocol.ErrorCode.INVALIDNAME)) {
					System.out.println(input);
				}
			}
		}
		if (!registerSuccesfull) {
			view.print("username taken");
		}
	}

	/**
	 * Asks the user if he wants to play as humanplayer or let a computerplayer
	 * play and if he chooses a computerplayer then he can choose a strategy
	 * for the computerplayer. After that he can choose with how many players
	 * he wants to start a game.
	 */
	public void startGame() {
		this.computerPlayerBool = view.askHumanOrComputerPlayer();
		if (computerPlayerBool == true) {
			this.strategy = view.getStrategyFromInput();
		}
		this.aantal = view.startGame();
		join(aantal);
	}

	/**
	 * Sets the field you in client with the player in the clientgame that has
	 * the name of the player that plays on this client.
	 * 
	 * @param player
	 */
	public void setYou(Player player) {
		you = player;
	}

	/**
	 * Get the boolean that determines of this client plays as computerplayer or
	 * as humanplayer.
	 * 
	 * @return true if this client plays as computerplayer, false if this client
	 * 			plays as humanplayer.
	 */
	public boolean getComputerPlayerBool() {
		return computerPlayerBool;
	}

	/**
	 * Get the strategy of the computerplayer of this client.
	 * 
	 * @return the strategy
	 */
	public Strategy getStrategy() {
		return strategy;
	}

	/**
	 * As long as there comes input from the server, after the client started this run method, 
	 * the client will analyze which command came through the input and responds accordingly.
	 * If no command came through the client will shutdown.
	 */
	public void run() {
		String input = null;
		try {
			while ((input = readString()) != null) {
				String[] inputArray = input.split(Protocol.SPLIT);
				if (inputArray[0].equals(Protocol.ENDGAME)) {
					endgame();
				} else if (inputArray[0].equals(Protocol.ERROR)) {
					if (inputArray.length == 1) {
						System.out.println("error");
						view.print("Geen foutcode meegegeven foei foei foei");
					} else if (inputArray[1].equals("0")) {
						view.print("Fout commando: 0");
					} else if (inputArray[1].equals("1")) {
						view.print("Foute beurt: 1");
					} else if (inputArray[1].equals("2")) {
						view.print("Niet unieke naam of onjuiste naam: 2");
					} else if (inputArray[1].equals("3")) {
						view.print("Speler disconnected: 3");
					} else if (inputArray[1].equals("4")) {
						view.print("Speler heeft functie niet: 4");
					}
				} else if (inputArray[0].equals(Protocol.PLACED)) {
					if (inputArray.length >= 5) {
						String[] newArray = new String[inputArray.length - 2];
						for (int i = 2; i < inputArray.length; i++) {
							newArray[i - 2] = inputArray[i];
						}
						placed(newArray);
						if (you instanceof HumanPlayer) {
							view.print(game.getBoard().toString());
							String pointsPlayers = "--------------------------------------------"
											+ "----------------\nPLAYERPOINTS:\n\n";
							for (Player p : game.getPlayers()) {
								pointsPlayers += p.getName() + ": " + p.getPoints() + "\n";
							}
							pointsPlayers += "----------------------------------------------------";
							game.getClient().getView().print(pointsPlayers);
						}
					} else {
						throw new InvalidCommandException();
					}
				} else if (inputArray[0].equals(Protocol.NEWSTONES)) {
					List<Stone> stones = null;
					try {
						stones = Protocol.stringToStoneList(inputArray);
						game.giveStones(stones, clientName);
					} catch (InvalidStoneException e) {
						throw new InvalidCommandException();
					} catch (InvalidMoveException e) {
						throw new InvalidCommandException();
					}
				} else if (inputArray[0].equals(Protocol.TRADED)) {
					view.print("Speler " + inputArray[1] + " traded " + inputArray[2] + " stones.");
				} else if (inputArray[0].equals(Protocol.TURN)) {
					if (inputArray.length == 2) {
						turn(inputArray);
					} else {
						throw new InvalidCommandException();
					}
				} else if (inputArray[0].equals(Protocol.PLAYERS)) {
					if (inputArray.length >= 2) {
						players = new ArrayList<String>();
						for (int i = 1; i < inputArray.length; i++) {
							players.add(inputArray[i]);
						}
					}
				} else if (inputArray[0].equals(Protocol.JOINLOBBY)) {
					if (inputArray.length >= 2) {
						String message = "\n\nPlayer " + inputArray[1] + " joined the room";
						view.print(message);
					}
				} else if (inputArray[0].equals(Protocol.START)) {
					if (inputArray.length >= 3) {
						if (input.contains(clientName)) {
							initGame(inputArray);
						}
					} else {
						throw new InvalidCommandException();
					}
				}
			}
		} catch (InvalidCommandException e) {
			e.printStackTrace();
			serverBroken();
		} catch (GameNotEndedException e) {
			serverBroken();
		}
		shutdown();
	}

	/**
	 * If the game ended on the server this method is called from the run method.
	 * This method shows on the view which player has won and all points of all 
	 * the players, then asks if the client wants to play another game.
	 * 
	 * @throws GameNotEndedException
	 */
	public void endgame() throws GameNotEndedException {
		Player winner = game.getWinner();
		String message = "Game ended, player " + winner.getName() 
						+ " has won.\n" + "with " + winner.getPoints()
						+ " points.";
		String pointsPlayers = "\n-------------------------------------------------------"
						+ "\nPLAYERPOINTS:\n\n";
		for (Player p : game.getPlayers()) {
			pointsPlayers += p.getName() + ": " + p.getPoints() + "\n";
		}
		pointsPlayers += "-------------------------------------------------------";
		view.print(message + pointsPlayers);
		playagain();
	}

	/**
	 * Asks the user of the client if he wants to play another game, and if yes the he can choose
	 * if he wants a human or computerplayer and so on, if not yes then the client shuts down.
	 */
	public void playagain() {
		String playagain = view.readString("Do you want to play another game? y/n: ");
		startGame();
		if (playagain.equals("y")) {
			startGame();
		} else {
			shutdown();
		}

	}

	/**
	 * Gets from the server the playername, points and stones of the player who has just made his move
	 * and updates the playerpoints on the client and the board on the client.
	 * 
	 * @param inputArray
	 * @throws InvalidCommandException
	 */
	public void placed(String[] inputArray) throws InvalidCommandException {
		List<Stone> stones = Protocol.stringToPlacedStoneList(inputArray);
		List<Position> positions = Protocol.stringToPlacePositionList(inputArray);
		try {
			game.makeMove(positions, stones);
			game.addPoints(inputArray[0]);
		} catch (InvalidMoveException e) {
			throw new InvalidCommandException();
		}
	}

	/**
	 * Gets from the server the player who's turn it is and prints on the view which player
	 * is making a move. If it is the clientplayers turn it starts the makemove method
	 * of the clientplayer.
	 * 
	 * @param inputArray
	 * @throws InvalidCommandException
	 */
	//@ requires inputArray.length == 2;
	public void turn(String[] inputArray) throws InvalidCommandException {
		game.setCurrentPlayer(inputArray[1]);
		if (inputArray[1].equals(clientName)) {
			System.out.println("current player making move");
			game.incMoveCount();
			game.getCurrentPlayer().makeMove();
		} else {
			view.print("waiting for " + inputArray[1] + " to make a move");
		}
	}

	/**
	 * Creates a clientgame with the players the server sends to this method,
	 * gotten from the run method via the right command from the protocol.
	 * 
	 * @param inputArray
	 */
	public void initGame(String[] inputArray) {
		String[] newPlayers = new String[inputArray.length - 1];
		for (int i = 1; i < inputArray.length; i++) {
			newPlayers[i - 1] = inputArray[i];
		}
		this.game = new ClientGame(newPlayers, this);
	}

	/**
	 * Get the game that is played by this client.
	 * 
	 * @return the game.
	 */
	public ClientGame getGame() {
		return game;
	}

	/**
	 * Get the view that is used.
	 * 
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * Sends the specified message to the server.
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			shutdown();
		}
	}

	/**
	 * close the socket connection.
	 */
	public void shutdown() {
		view.print("Closing socket connection...");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns the client name.
	 */
	public String getClientName() {
		return clientName;
	}

	/**
	 * Reads from the input the server gives.
	 * 
	 * @return input from server
	 */
	public String readString() {
		String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
		}
		return line;
	}

	/**
	 * Sends to the server the stones that the clientplayer placed and where
	 * those stones were placed.
	 * 
	 * @param stones
	 */
	public void place(List<Stone> stones) {
		String msg = Protocol.PLACE;
		for (Stone s : stones) {
			msg = msg + Protocol.SPLIT + s.toUsableString() 
					+ Protocol.SPLIT + s.getPosition().toUsableString();
		}
		sendMessage(msg);
	}

	/**
	 * Sends to the server the stones the clientplayer traded.
	 * 
	 * @param stones
	 */
	public void trade(List<Stone> stones) {
		String msg = Protocol.TRADE;
		for (int i = 0; i < stones.size(); i++) {
			Stone s = stones.get(i);
			msg = msg + Protocol.SPLIT + s.toUsableString();
		}
		sendMessage(msg);
	}

	/**
	 * Sends to the server that the clientplayer wants to join a game
	 * with the specified amount of players.
	 * 
	 * @param amount
	 */
	public void join(int amount) {
		sendMessage(Protocol.JOINAANTAL + Protocol.SPLIT + amount);
		view.print("waiting for other players...");
	}

	/**
	 * The chat send function of the protocol, not used because we hadnt time to
	 * make the chat function.
	 * 
	 * @param msg
	 */
	public void chat(String msg) {
		sendMessage(Protocol.CHAT + Protocol.SPLIT + msg);
	}

	/**
	 * The chat to specified player send function of the protocol, not used 
	 * because we hadnt time to make the chat function.
	 * 
	 * @param msg
	 * @param player
	 */
	public void chatPM(String msg, Player player) {
		sendMessage(Protocol.CHATPM + Protocol.SPLIT + player.getName() + Protocol.SPLIT + msg);
	}

	/**
	 * Asks the server which players are online.
	 */
	public void askPlayers() {
		sendMessage(Protocol.WHICHPLAYERS);
	}

	/**
	 * If the server gives input that is not according to the protocol the
	 * client shuts down and the view prints that the server is broken.
	 */
	public void serverBroken() {
		System.out.println("Server is broken. OKDOEI!");
		shutdown();
	}
}
