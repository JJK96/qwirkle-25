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
import java.util.Iterator;
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
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String[] options;
	private ClientGame game;
	private View view;
	private int aantal;
	private boolean registerSuccesfull = false;
	private boolean computerPlayerBool;
	private Strategy strategy;
	private List<String> players;
	private Player you;

	public Client() {
		this.view = new View(this);
		players = new LinkedList<String>();
		init();
		logIn();
	}

	public void init() {
		sock = null;
		while (sock == null) {
			InetAddress hostname = null;
			while (hostname == null) {
				try {
					hostname = view.getHostName();

				} catch (UnknownHostException e) {
					view.print("Invalid hostname");
				}
			}
			int port = view.getPort();
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

	}

	public void logIn() {
		while (!registerSuccesfull) {
			clientName = view.getClientName();
			sendMessage(Protocol.REGISTER + Protocol.SPLIT + getClientName());
			String input = null;
			if ((input = readString()) != null) {
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
		}
	}

	public void startGame() {
		this.computerPlayerBool = view.askHumanOrComputerPlayer();
		if (computerPlayerBool == true) {
			this.strategy = view.getStrategyFromInput();
		}
		this.aantal = view.startGame();
		join(aantal);
	}

	public void setYou(Player player) {
		you = player;
	}

	public boolean getComputerPlayerBool() {
		return computerPlayerBool;
	}

	public Strategy getStrategy() {
		return strategy;
	}

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
					String[] newArray = new String[inputArray.length - 2];
					for (int i = 2; i < inputArray.length; i++) {
						newArray[i - 2] = inputArray[i];
					}
					placed(newArray);
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
						String message = "Player " + inputArray[1] + " joined the room";
						// view.print(message);
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
		}
		shutdown();
	}

	public void endgame() {
		String message = "Game ended, player " + game.getWinner() + " has won.\n" + "with "
						+ game.getWinner().getPoints() + " points.";
		view.print(message);
		playagain();
	}

	public void playagain() {
		String playagain = view.readString("Do you want to play another game? y/n: ");
		if (playagain.equals("y")) {
			init();
		} else {
			shutdown();
		}

	}

	public void placed(String[] inputArray) throws InvalidCommandException {
		List<Stone> stones = Protocol.stringToPlacedStoneList(inputArray);
		List<Position> positions = Protocol.stringToPlacePositionList(inputArray);
		try {
			game.makeMove(positions, stones);
		} catch (InvalidMoveException e) {
			throw new InvalidCommandException();
		}
	}

	//@ requires inputArray.length == 2;
	public void turn(String[] inputArray) throws InvalidCommandException {
		game.setCurrentPlayer(inputArray[1]);
		if (inputArray[1].equals(clientName)) {
			System.out.println("current player making move");
			game.getCurrentPlayer().makeMove();
		} else {
			view.print("waiting for player: " + inputArray[1] + " to make a move");
		}
	}

	public void initGame(String[] inputArray) {
		String[] newPlayers = new String[inputArray.length - 1];
		for (int i = 1; i < inputArray.length; i++) {
			newPlayers[i - 1] = inputArray[i];
		}
		this.game = new ClientGame(newPlayers, this);
	}

	public ClientGame getGame() {
		return game;
	}

	public View getView() {
		return view;
	}

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
	 *  close the socket connection. 
	 */
	public void shutdown() {
		view.print("Closing socket connection...");
		try {
			in.close();
			out.close();
			sock.close();
			// implement dingen enzo
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

	public String readString() {
		String line = null;
		try {
			line = in.readLine();
		} catch (IOException e) {
		}
		return line;
	}

	public void place(List<Stone> stones) {
		String msg = Protocol.PLACE;
		for (Stone s : stones) {
			msg = msg + Protocol.SPLIT + s.toUsableString() + Protocol.SPLIT 
					+ s.getPosition().toUsableString();
		}
		sendMessage(msg);
	}

	public void trade(List<Stone> stones) {
		String msg = Protocol.TRADE;
		for (int i = 0; i < stones.size(); i++) {
			Stone s = stones.get(i);
			msg = msg + Protocol.SPLIT + s.toUsableString();
		}
		sendMessage(msg);
	}

	public void register() {
		sendMessage(Protocol.REGISTER + Protocol.SPLIT + options);
	}

	public void join(int amount) {
		sendMessage(Protocol.JOINAANTAL + Protocol.SPLIT + amount);
		view.print("waiting for other players...");
	}

	public void chat(String msg) {
		sendMessage(Protocol.CHAT + Protocol.SPLIT + msg);
	}

	public void chatPM(String msg, Player player) {
		sendMessage(Protocol.CHATPM + Protocol.SPLIT + player.getName() + Protocol.SPLIT + msg);
	}

	public void askPlayers() {
		sendMessage(Protocol.WHICHPLAYERS);
	}

	public void serverBroken() {
		System.out.println("Server is broken, OK DOEI! (client might also be broken)");
		shutdown();
	}
}
