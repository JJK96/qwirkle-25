package server;

/**
 * Created by jjk on 1/13/16.
 */
public class testBoard {
    public static void main(String[] args) {
        Board b = new Board();
        PossibleMove p = b.getPossibleMoves().get(new Position(0,0));
        b.makeMove(new Stone(Stone.Shape.ONE, Stone.Color.ONE), p);
        b.makeMove(1,0,new Stone(Stone.Shape.ONE, Stone.Color.TWO));
        b.makeMove(2,0,new Stone(Stone.Shape.ONE, Stone.Color.THREE));
        b.makeMove(0,-1, new Stone(Stone.Shape.THREE, Stone.Color.ONE));
        System.out.println(b);

    }
}
