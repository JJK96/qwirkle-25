package test.server;

import org.junit.Before;
import org.junit.Test;
import server.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

/**
 * Created by jjk on 1/27/16.
 */
public class ServerTest{
    BufferedWriter toServer;
    BufferedReader fromServer;
    @Before
    public void setUp() {
        Thread t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                Server s = new Server(5000);
                s.run();
            }
        });
        t1.start();
        try {
            InetAddress hostname = InetAddress.getByName("localhost");
            Socket sock = new Socket(hostname, 5000);
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
        assert readMessage().equals("hello_from_the_other_side jjk");
        assert readMessage().equals("players");
        assert readMessage().equals("joinlobby jjk");
        sendMessage("join 1");
        assert readMessage().equals("start jjk");
        String stones = readMessage();
        String[] stonelist = stones.split(" ");
        assert stones.startsWith("newstones");
        assert stonelist.length == 7;
        assert readMessage().equals("turn jjk");
        sendMessage("place " + stonelist[1]);

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