package server;

import client.LittleBetterStrategy;
import com.sun.org.glassfish.external.arc.Taxonomy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import shared.Protocol;
import shared.Stone;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jjk on 1/27/16.
 */
public class ServerTest{
    BufferedWriter toServer;
    BufferedReader fromServer;
    Server s;
    Thread t1;
    Socket sock;
    int times = 0;
    @Before
    public void setUp() {
        if (times == 0) {
            t1 = new Thread(new Runnable() {

                @Override
                public void run() {
                    s = new Server(5000);
                    s.run();
                }
            });
            t1.start();
        }
        times++;
       try {
            InetAddress hostname = InetAddress.getByName("localhost");
            sock =  new Socket(hostname, 5000);
            toServer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void correctTest() {
        sendMessage("hello jjk");
        assertEquals(readMessage(), "hello_from_the_other_side ");
        assertEquals(readMessage(), "players ");
        assertEquals(readMessage(), "joinlobby jjk ");
        sendMessage("join 1");
        assertEquals(readMessage(), "start jjk ");
        String stones = readMessage();
        String[] stonelist = stones.split(" ");
        assertTrue (stones.startsWith("newstones"));
        assertTrue (stonelist.length == 7);
        assertEquals(readMessage(), "turn jjk");
        ServerGame game = s.getGames().get(0);
        List<Stone> move = new LittleBetterStrategy(1).getMove(game.getBoard().deepCopy(), game.getCurrentPlayer().getStones());
        String message = "place ";
        String stoneString = "";
        for (Stone s : move) {
            stoneString += s.toUsableString() + " " + s.getPosition().toUsableString() + " ";
        }
        message += stoneString;
        sendMessage(message);
        assertTrue(readMessage().startsWith("newstones"));
        assertEquals(readMessage(), "placed jjk " + move.size() + " " + stoneString);
        assertEquals("turn jjk", readMessage());
    }

    private void sendMessage(String msg) {
        try {
            toServer.write(msg);
            toServer.newLine();
            toServer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readMessage() {
        String message = null;
        try {
            message = fromServer.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return message;
    }

}