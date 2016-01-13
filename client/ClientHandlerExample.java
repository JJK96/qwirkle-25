package client;

import server.ServerExample;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandlerExample extends Thread {
	private ServerExample serverExample;
	private BufferedReader in;
	private BufferedWriter out;
	private String clientName;

	/**
	 * Constructs a ClientHandlerExample object Initialises both Data streams.
	 */
	//@ requires serverExampleArg != null && sockArg != null;
	public ClientHandlerExample(ServerExample serverExampleArg, Socket sockArg) throws IOException {
		this.serverExample = serverExampleArg;
		this.in = new BufferedReader(new InputStreamReader(sockArg.getInputStream()));
		this.out = new BufferedWriter(new OutputStreamWriter(sockArg.getOutputStream()));

	}

	/**
	 * Reads the name of a ClientExample from the input stream and sends a broadcast
	 * message to the ServerExample to signal that the ClientExample is participating in the
	 * chat. Notice that this method should be called immediately after the
	 * ClientHandlerExample has been constructed.
	 */
	public void announce() throws IOException {
		clientName = in.readLine();
		serverExample.broadcast("[" + clientName + " has entered]");
	}

	/**
	 * This method takes care of sending messages from the ClientExample. Every message
	 * that is received, is preprended with the name of the ClientExample, and the new
	 * message is offered to the ServerExample for broadcasting. If an IOException is
	 * thrown while reading the message, the method concludes that the socket
	 * connection is broken and shutdown() will be called.
	 */
	public void run() {
		String line = null;
		try {
			while ((line = in.readLine()) != null) {
				line = clientName + ": " + line;
				serverExample.broadcast(line);
			}
		} catch (Exception e) {
			shutdown();
		}
	}

	/**
	 * This method can be used to send a message over the socket connection to
	 * the ClientExample. If the writing of a message fails, the method concludes that
	 * the socket connection has been lost and shutdown() is called.
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
	 * This ClientHandlerExample signs off from the ServerExample and subsequently sends a
	 * last broadcast to the ServerExample to inform that the ClientExample is no longer
	 * participating in the chat.
	 */
	private void shutdown() {
		serverExample.removeHandler(this);
		serverExample.broadcast("[" + clientName + " has left]");
	}
	
	public String getClientName() {
		return clientName;
	}
}
