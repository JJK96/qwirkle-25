package server;

import java.util.List;

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
    /**
     * Creates a game with the given size
     *
     * @param size
     */
    public ServerGame(int size) {
        this.size = size;
        players = new ServerPlayer[size];
        playernum = 0;
        started = false;
    }

    @Override
    public void run() {

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

}
