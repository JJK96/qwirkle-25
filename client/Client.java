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
import java.util.List;

import shared.InvalidCommandException;
import shared.Protocol;
import shared.Stone;

public class Client extends Thread {

	private static final String USAGE = "usage: Client <address> <port>";

	private static void printStatic(String message) {
		System.out.println(message);
	}

	/** Start een Client-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}

		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			printStatic("ERROR: no valid hostname!");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			printStatic("ERROR: no valid portnummer!");
			System.exit(0);
		}

		try {
			Client client = new Client(host, port);
			client.start();			
		} catch (IOException e) {
			printStatic("ERROR: couldn't construct a client object!");
			System.exit(0);
		}

	}

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private String[] options;
	private ClientGame game;
	private View view;
	private boolean registerSuccesfull = false;

	public Client(InetAddress host, int port) throws IOException {
		this.sock = new Socket(host, port);
		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		this.view = new View(this);
		init();
	}

	public void init() {
		while (!registerSuccesfull) {
			clientName = view.getClientName();
			sendMessage(Protocol.REGISTER + Protocol.SPLIT + getClientName());
			String input = null;
			if ((input = readString()) != null) {
				String[] inputArray = input.split(Protocol.SPLIT);
				if (inputArray[0].equals(Protocol.ACKNOWLEDGE)) {
					registerSuccesfull = true;
				} else if (inputArray[0].equals(Protocol.ERROR)) {
					if (inputArray.length == 1) {
						System.out.println("wrong error code");
					}
					else if (!inputArray[1].equals(Protocol.errorcode.INVALIDNAME)) {
						System.out.println(input);
					}
				}
			}
		}
		//view.start();

	}

	public void run() {
		String input = null;
		while ((input = readString()) != null) {
			String[] inputArray = input.split(Protocol.SPLIT);
			if (inputArray[0].equals(Protocol.ACKNOWLEDGE)) {
				registerSuccesfull = true;
			}
			if (!registerSuccesfull) {
				break;
			}
			if (inputArray[0].equals(Protocol.ENDGAME)) {
				// implement
			} else if (inputArray[0].equals(Protocol.ERROR)) {
				if (inputArray.length == 1) {
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
				List<Stone> stones = new ArrayList<Stone>();
				try {
					stones = Protocol.StringToPlacedStonelist(inputArray);
				} catch (InvalidCommandException e) {
					e.printStackTrace();
				}
				int[] x = Protocol.convertPlacedX(inputArray);
				int[] y = Protocol.convertPlacedY(inputArray);
				for (int i = 0; i < stones.size(); i++) {
					game.getBoard().makeMove(x[i], y[i], stones.get(i));
				}
			} else if (inputArray[0].equals(Protocol.NEWSTONES)) {
				List<Stone> stones = Protocol.StringToStonelist(inputArray);
				game.getCurrentPlayer().takeStones(stones);
			} else if (inputArray[0].equals(Protocol.TRADED)) {
				view.print("Speler " + inputArray[1] + " " + inputArray[0] + " " + inputArray[2] + " stones.");
			} else if (inputArray[0].equals(Protocol.TURN)) {
				Player[] players = game.getPlayers();
				for (int i = 0; i < game.getPlayers().length; i++) {
					if (players[i].getName().equals(inputArray[1])) {
						game.setCurrentPlayer(players[i]);
						break;
					}
				}
			} else if (inputArray[0].equals(Protocol.PLAYERS)) {

			} else if (inputArray[0].equals(Protocol.JOINLOBBY)) {
				if (inputArray[1].equals(clientName)) {
					String prompt = "";
					view.readInt(prompt);
				}
			} else if (inputArray[0].equals(Protocol.START)) {
				String[] players = new String[inputArray.length - 1];
				for (int i = 1; i < inputArray.length; i++) {
					players[i - 1] = inputArray[i];
				}
				this.game = new ClientGame(players, this);
			} else if (inputArray[0].equals(Protocol.MSG)) {

			} else if (inputArray[0].equals(Protocol.MSGPM)) {

			} else if (inputArray[0].equals(Protocol.NEWCHALLENGE)) {

			} else if (inputArray[0].equals(Protocol.ACCEPT)) {

			} else if (inputArray[0].equals(Protocol.DECLINE)) {

			} else
				;
		}
		shutdown();
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

	/** close the socket connection. */
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

	/** returns the client name */
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
			msg = msg + Protocol.SPLIT + s.toUsableString() + Protocol.SPLIT + s.getPosition().toUsableString();
		}
		sendMessage(msg);
	}

	public void trade(List<Stone> stones) {
		String msg = Protocol.TRADE;
		for (Stone s : stones) {
			msg = msg + Protocol.SPLIT + s.toUsableString();
		}
		sendMessage(msg);
	}

	public void register() {
		sendMessage(Protocol.REGISTER + Protocol.SPLIT + options);
	}

	public void join(int amount) {
		sendMessage(Protocol.JOINAANTAL + Protocol.SPLIT + amount);
	}

	public void chat(String msg) {
		sendMessage(Protocol.CHAT + Protocol.SPLIT + msg);
	}

	public void chatPM(String msg, Player player) {
		sendMessage(Protocol.CHATPM + Protocol.SPLIT + player.getName() + Protocol.SPLIT + msg);
	}

}
