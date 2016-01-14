package server;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerPlayer extends Thread {

    private BufferedReader in;
    private BufferedWriter out;
    private String name;
    private int points;
	private List<Stone> stones;
	private Game game;
    private String options;

    public ServerPlayer(Socket sock) throws IOException{
        in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
        points = 0;
        stones = new ArrayList<Stone>();
        options = "";
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
}
