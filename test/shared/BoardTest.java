package shared;

import static org.junit.Assert.*;

/**
 * Created by jjk on 1/26/16.
 */
public class BoardTest {
    Board b;
    @org.junit.Before
    public void setUp() throws Exception {
        b = new Board();
        b.makeMove(new Position(0,0), new Stone(Stone.Shape.c, Stone.Color.B));
        b.makeMove(new Position(1,0), new Stone(Stone.Shape.c, Stone.Color.G));
        b.makeMove(new Position(2,0), new Stone(Stone.Shape.c, Stone.Color.O));
        b.makeMove(new Position(3,0), new Stone(Stone.Shape.c, Stone.Color.O));
        b.makeMove(new Position(0,1), new Stone(Stone.Shape.d, Stone.Color.B));
        b.makeMove(new Position(0,2), new Stone(Stone.Shape.o, Stone.Color.B));
    }

    @org.junit.Test
    public void testReset() throws Exception {

    }

    @org.junit.Test
    public void testGetPossibleMoves() throws Exception {

    }

    @org.junit.Test
    public void testGetStones() throws Exception {

    }

    @org.junit.Test
    public void testDeepCopy() throws Exception {

    }

    @org.junit.Test
    public void testIsValidMove() throws Exception {

    }

    @org.junit.Test
    public void testIsValidMove1() throws Exception {

    }

    @org.junit.Test
    public void testIsValidMove2() throws Exception {

    }

    @org.junit.Test
    public void testMakeMove() throws Exception {

    }

    @org.junit.Test
    public void testMakeMove1() throws Exception {

    }

    @org.junit.Test
    public void testMakeMoves() throws Exception {

    }

    @org.junit.Test
    public void testSameColumn() throws Exception {

    }

    @org.junit.Test
    public void testSameRow() throws Exception {

    }

    @org.junit.Test
    public void testAllStonesOneRow() throws Exception {

    }

    @org.junit.Test
    public void testAllOneRow() throws Exception {

    }

    @org.junit.Test
    public void testConnectedRow() throws Exception {

    }

    @org.junit.Test
    public void testConnectedColumn() throws Exception {

    }

    @org.junit.Test
    public void testMakeMove2() throws Exception {

    }

    @org.junit.Test
    public void testAddPossibleMove() throws Exception {

    }

    @org.junit.Test
    public void testGetBoundaries() throws Exception {

    }

    @org.junit.Test
    public void testToString() throws Exception {

    }

    @org.junit.Test
    public void testToString1() throws Exception {

    }

    @org.junit.Test
    public void testCalculatePoints() throws Exception {

    }
}