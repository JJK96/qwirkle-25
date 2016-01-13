package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Server {
    private static final String USAGE
            = "usage: " + Server.class.getName() + " <port>";

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
    private List<Player> players;
    private List<Game> games;
    
    /** Constructs a new Server object */
    public Server(int portArg) {
        this.port = portArg;
        players = new ArrayList<Player>();
        games = new ArrayList<Game>();
    }
    
    /**
     * Listens to a port of this Server if there are any Clients that 
     * would like to connect. For every new socket connection a new
     * ClientHandler thread is started that takes care of the further
     * communication with the Client.
     */
    public void run() {
    	try {
			ServerSocket sock = new ServerSocket(port);
			while (true) {
				Socket acceptedsocket = sock.accept();
				String[] names = null;
				Game game = new Game(names);
				addGame(game);
				
				game.start();	
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void print(String message){
        System.out.println(message);
    }
    
    /**
     * Sends a message using the collection of connected ClientHandlers
     * to all connected Clients.
     * @param msg message that is send
     */
    public void broadcast(String msg) {
    	for (Game g : games) {
    		
    	}
    }
    
    /**
     * Add a Game to the collection of Games.
     * @param game that will be added
     */
    public void addGame(Game game) {
        games.add(game);
    }
    
    /**
     * Remove a game from the collection of games. 
     * @param game that will be removed
     */
    public void removeGame(Game game) {
       	games.remove(game);
    }
}
