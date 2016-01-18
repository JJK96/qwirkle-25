package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import shared.Space;
import shared.Stone;
import shared.PossibleMove;
import shared.Position;
import shared.Protocol;
import shared.Protocol;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerPlayer extends Thread {

    private BufferedReader in;
    private BufferedWriter out;
    private String name;
    private int points;
	private List<Stone> stones;
	private ServerGame game;
    private String options;
    private Server server;

    public ServerPlayer(Socket sock, Server server) throws IOException{
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        points = 0;
        stones = new ArrayList<Stone>();
        options = "";
        this.server = server;

    }

    @Override
    public void run() {
        String line = null;
        try {
            while ((line = in.readLine()) != null) {
                String[] words = line.split(Protocol.SPLIT);
                if (words[0].equals(Protocol.JOINAANTAL)) {
                    server.joinGame(this, Integer.parseInt(words[1]));
                }
            }
            shutdown();
        } catch (IOException e) {
            shutdown();
        }
    }

    public int register() throws IOException{
        String line = in.readLine();
        String[] words = line.split(Protocol.SPLIT);
        if (words.length >= 2 && words[0].equals(Protocol.REGISTER)) {
            name = words[1];
            if (words.length > 2) {
                for (int i = 2; i<words.length; i++) {
                    options += words[i] + Protocol.SPLIT;
                }
            }
            acknowledge();
            sendPlayers();
            return 1;
        }
        return 0;
    }
    public void acknowledge() {
        sendMessage(Protocol.ACKNOWLEDGE + Protocol.SPLIT + options);
    }
    public void sendMessage(String msg) {
        try {
            out.write(msg);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void error(int code) {
        sendMessage(Protocol.ERROR + Protocol.SPLIT + code);
    }

    public String getThisName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public List<Stone> getStones() {
        return stones;
    }

    public ServerGame getGame() {
        return game;
    }
    public void setGame(ServerGame game) {
        this.game = game;
    }

    public String getOptions() {
        return options;
    }
    public void sendPlayers() {
        sendMessage(Protocol.PLAYERS + Protocol.SPLIT + server.getPlayers());
    }
    public boolean inGame() {

        return getGame() != null;
    }

    public void shutdown() {
        try {
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        server.removePlayer(this);
    }
}
