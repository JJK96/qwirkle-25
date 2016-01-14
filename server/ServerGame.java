package server;

/**
 * Created by jjk on 1/14/16.
 */
public class ServerGame{
    ServerPlayer[] players;
    /**
     * Creates a game with the names of the players.
     *
     * @param players
     */
    public ServerGame(ServerPlayer[] players) {
        this.players = players;
    }
}
