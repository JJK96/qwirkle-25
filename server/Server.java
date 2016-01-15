package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import shared.Protocol;

/**
 * Created by jjk on 1/14/16.
 */
public class Server {
    private static final String USAGE = "usage: " + Server.class.getName() + " <port>";
    private List<ServerPlayer> players;
    private int port;
    private List<ServerGame> games;

    /**
     *
     * @param port should be a number between 0 and 65535
     */
    public Server(int port) {
        this.port = port;
        players = new ArrayList<ServerPlayer>();
        games = new ArrayList<ServerGame>();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(0);
        }

        Server server = new Server(Integer.parseInt(args[0]));
        server.run();
    }

    public void run() {
        try {
            ServerSocket sock = new ServerSocket(port);
            while (true) {
                Socket client = sock.accept();
                ServerPlayer newplayer = new ServerPlayer(client, this);
                if (newplayer.register() > 0) {
                    addPlayer(newplayer);
                    newplayer.start();
                    joinLobby(newplayer);
                }
                else {
                    newplayer.error(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPlayer(ServerPlayer player) {
        synchronized (players) {
            players.add(player);
        }
    }

    public void removePlayer(ServerPlayer player) {
        synchronized (players) {
            players.remove(player);
            if (player.inGame()) {
                player.getGame().removePlayer(player);
            }
        }
    }

    public synchronized void removeGame(ServerGame game) {
        synchronized (games) {
            try {
                game.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            games.remove(game);
            for (ServerPlayer p : players) {
                System.out.println(p.getThisName() + p.getGame());
            }
        }
    }

    public void broadcast(String msg) {
        synchronized (players) {
            for (ServerPlayer p : players) {
                p.sendMessage(msg);
            }
        }
    }

    /**
     * send the joinlobby command when the player joins.
     * @param player
     */
    public void joinLobby(ServerPlayer player) {
        broadcast(Protocol.JOINLOBBY + Protocol.SPLIT + player.getThisName() + Protocol.SPLIT + player.getOptions());
    }

    /**
     * returns the list of player names
     */
    public String getPlayers() {
        synchronized(players) {
            String out = "";
            for (ServerPlayer p : players) {
                out += p.getThisName() + Protocol.SPLIT;
            }
            return out;
        }
    }
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
                game = newGame(size);
                addGame(game);
            }
            if (game.addPlayer(player) == 0) {
                startGame(game);
            }
        }
    }

    public ServerGame newGame(int size) {
        synchronized (games) {
            ServerGame newgame = new ServerGame(size, this);
            games.add(newgame);
            return newgame;
        }
    }

    public void addGame(ServerGame game) {
        synchronized (games) {
            games.add(game);
        }
    }

    public synchronized void startGame(ServerGame game) {
        broadcast(Protocol.START + Protocol.SPLIT + game.getPlayerNames());
        game.start();
    }

}
