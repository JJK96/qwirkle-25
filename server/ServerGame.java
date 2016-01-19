package server;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import shared.*;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerGame extends Thread{
    public static final int MAXSTONES = 6;
    private ServerPlayer[] players;
    private int size;
    private int playernum;
    private boolean running;
    private Board board;
    private List<Stone> bag;
    private Server server;
    private ServerPlayer currentplayer;
    private Lock lock;
    private Condition playerDone;
    /**
     * Creates a game with the given size
     *
     * @param size
     */
    public ServerGame(int size, Server server) {
        this.size = size;
        players = new ServerPlayer[size];
        playernum = 0;
        running = false;
        this.server = server;
        lock = new ReentrantLock();
        playerDone = lock.newCondition();
        init();
    }

    public void init() {
        board = new Board();
        bag = new LinkedList<Stone>();
        for(Stone.Color c : Stone.Color.values()) {
            for (Stone.Shape s : Stone.Shape.values()) {
                for (int i=0; i < 3; i++) {
                    bag.add(new Stone(s,c));
                }
            }
        }
    }

    @Override
    public void run() {
        lock.lock();
        running = true;
        System.out.println("game started with players: " + getPlayerNames());
        giveInitialStones();
        int currentplayernum = (int) Math.floor(Math.random() * playernum);
        while (!hasWinner()) {
            currentplayer = players[currentplayernum];
            sendTurn();
            try {
                playerDone.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("next player");
            currentplayernum = (currentplayernum + 1) % playernum;
        }
        lock.unlock();
    }

    public void giveInitialStones() {
        for (ServerPlayer p : players) {
            List stones = new ArrayList();
            for (int i = 0; i<MAXSTONES; i++) {
                stones.add(takeStone());
            }
            p.giveStones(stones);
        }
    }

    public int addPlayer(ServerPlayer player) {
        if (playernum < size) {
            players[playernum] = player;
            playernum += 1;
        }
        player.setGame(this);
        return size-playernum;
    }
    public void removePlayer(ServerPlayer player) {
        ServerPlayer[] newplayers = new ServerPlayer[playernum -1];
        int i=0;
        for (ServerPlayer p : players ) {
            if (!p.equals(player)) {
                newplayers[i] = p;
                i++;
            }
        }
        this.players = newplayers;
        endgame();
    }

    public Boolean hasWinner() {
        return false;
    }

    public ServerPlayer getCurrentPlayer() {
        return currentplayer;
    }
    public int getSize() {
        return size;
    }

    public int getPlayernum() {
        return playernum;
    }

    public boolean isRunning() {
        return running;
    }

    private void sendTurn() {
        broadcast(Protocol.TURN + Protocol.SPLIT + currentplayer.getThisName());
    }

    private Stone takeStone() {
        Stone s = bag.get((int) Math.floor(Math.random() * bag.size()));
        bag.remove(s);
        return s;
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
        running = false;
        server.removeGame(this);
    }

    public void broadcast(String msg) {
        for (ServerPlayer p : players) {
            p.sendMessage(msg);
        }
    }
    //@ requires stones.size() == positions.size();
    public void placeStones(List<Stone> stones, List<Position> positions) throws InvalidMoveException {
        lock.lock();
        try {
            board.makeMoves(positions, stones);
            System.out.println("currentplayer placed stones");
            playerDone.signal();
        } catch (InvalidMoveException e) {
            board = board.deepCopy();
            throw new InvalidMoveException();
        }
        finally {
            lock.unlock();
        }
    }
}
