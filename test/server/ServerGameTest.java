package test.server;

import org.junit.Before;
import org.junit.Test;
import server.*;

import static org.junit.Assert.*;

/**
 * Created by jjk on 1/27/16.
 */
public class ServerGameTest {
    ServerGame game;

    @Before
    public void setUp() {
        Server server = new Server(5900);
        game = new ServerGame(2, server);
    }

    @Test
    public void testInit() throws Exception {
        assert game.getBag().size() == 3 * 36;
        assert game.getBoard() != null;
    }

    @Test
    public void testRun() throws Exception {

    }

    @Test
    public void testDetermineFirstPlayer() throws Exception {

    }

    @Test
    public void testGiveInitialStones() throws Exception {

    }

    @Test
    public void testAddPlayer() throws Exception {

    }

    @Test
    public void testRemovePlayer() throws Exception {

    }

    @Test
    public void testEnd() throws Exception {

    }

    @Test
    public void testHasWinner() throws Exception {

    }

    @Test
    public void testGetCurrentPlayer() throws Exception {

    }

    @Test
    public void testGetSize() throws Exception {

    }

    @Test
    public void testGetPlayernum() throws Exception {

    }

    @Test
    public void testIsRunning() throws Exception {

    }

    @Test
    public void testGetPlayerNames() throws Exception {

    }

    @Test
    public void testEndgame() throws Exception {

    }

    @Test
    public void testBroadcast() throws Exception {

    }

    @Test
    public void testPlaced() throws Exception {

    }

    @Test
    public void testTraded() throws Exception {

    }

    @Test
    public void testPlaceStones() throws Exception {

    }

    @Test
    public void testGetWinner() throws Exception {

    }

    @Test
    public void testGotAllStones() throws Exception {

    }

    @Test
    public void testTrade() throws Exception {

    }

    @Test
    public void testTakeSomeStones() throws Exception {

    }
}