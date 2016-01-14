package server;

import java.util.List;
import java.util.Observable;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerGame extends Thread{
    private ServerPlayer[] players;
    private int size;
    private int playernum;
    private boolean started;
    private Board board;
    private List<Stone> bag;
    private Server server;
    /**
     * Creates a game with the given size
     *
     * @param size
     */
    public ServerGame(int size, Server server) {
        this.size = size;
        players = new ServerPlayer[size];
        playernum = 0;
        started = false;
        this.server = server;
    }

    @Override
    public void run() {
        System.out.println("game started with players: " + getPlayerNames());
    }

    public int addPlayer(ServerPlayer player) {
        if (playernum < size) {
            players[playernum] = player;
            playernum += 1;
        }
        return size-playernum;
    }

    public int getSize() {
        return size;
    }

    public int getPlayernum() {
        return playernum;
    }

    public boolean isStarted() {
        return started;
    }

    public String getPlayerNames() {
        String playernames = "";
        for (ServerPlayer p : players) {
            playernames += p.getThisName() + Protocol.SPLIT;
        }
        return playernames;
    }

    public void endgame() {
        for (ServerPlayer p : players) {
            p.setGame(null);
        }
        broadcast(Protocol.ENDGAME);
        server.removeGame(this);
    }

    public void broadcast(String msg) {
        for (ServerPlayer p : players) {
            p.sendMessage(msg);
        }
    }
}
