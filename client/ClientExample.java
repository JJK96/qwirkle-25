package client;

import server.Game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientExample extends Thread {
	private static final String USAGE = "usage: ClientExample <name> <address> <port>";

	/** Start een ClientExample-applicatie op. */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.out.println(USAGE);
			System.exit(0);
		}

		InetAddress host = null;
		int port = 0;

		try {
			host = InetAddress.getByName(args[1]);
		} catch (UnknownHostException e) {
			print("ERROR: no valid hostname!");
			System.exit(0);
		}

		try {
			port = Integer.parseInt(args[2]);
		} catch (NumberFormatException e) {
			print("ERROR: no valid portnummer!");
			System.exit(0);
		}

		try {
			ClientExample clientExample = new ClientExample(args[0], host, port);
			clientExample.sendMessage(args[0]);
			clientExample.start();

			do {
				String input = readString("");
				clientExample.sendMessage(input);
			} while (true);

		} catch (IOException e) {
			print("ERROR: couldn't construct a client object!");
			System.exit(0);
		}

	}

	private String clientName;
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	private Game game;

	/**
	 * Constructs a ClientExample-object and tries to make a socket connection
	 */
	public ClientExample(String name, InetAddress host, int port) throws IOException {
		this.clientName = name;
		this.sock = new Socket(host, port);
		this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		String[] names = null;
		this.game = new Game(names);
	}

	/**
	 * Reads the messages in the socket connection. Each message will be
	 * forwarded to the MessageUI
	 */
	public void run() {
		String line = "";
		try {
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** send a message to a ClientHandlerExample. */
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
		print("Closing socket connection...");
		try {
			in.close();
			out.close();
			sock.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/** returns the client name */
	public String getClientName() {
		return clientName;
	}

	private static void print(String message) {
		System.out.println(message);
	}

	public static String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}
}