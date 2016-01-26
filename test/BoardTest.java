package test;


import org.junit.Before;
import org.junit.Test;
import shared.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by jjk on 1/26/16.
 */
public class BoardTest {
    Board b;
    List<Stone> stonelist;
    Stone testStone;
    Stone testStone1; //can be placed at 4,0
    Stone testStone2; //can be placed at 5,0 after testStone1
    @Before
    public void setUp() throws Exception {
        b = new Board();
        b.makeMove(new Position(0,0), new Stone(Stone.Shape.c, Stone.Color.B));
        b.makeMove(new Position(0,1), new Stone(Stone.Shape.c, Stone.Color.G));
        b.makeMove(new Position(0,2), new Stone(Stone.Shape.c, Stone.Color.O));
        b.makeMove(new Position(0,3), new Stone(Stone.Shape.c, Stone.Color.P));
        b.makeMove(new Position(1,0), new Stone(Stone.Shape.d, Stone.Color.B));
        b.makeMove(new Position(2,0), new Stone(Stone.Shape.o, Stone.Color.B));
        b.makeMove(new Position(3,0), new Stone(Stone.Shape.s, Stone.Color.B));
        stonelist = new ArrayList<>();
        for (Stone.Color c : Stone.Color.values()) {
            for (Stone.Shape s : Stone.Shape.values()) {
                for (int i = 0; i < 3; i++) {
                    stonelist.add(new Stone(s, c));
                }
            }
        }
        testStone = new Stone(Stone.Shape.d, Stone.Color.G); //test stone, can be placed at 1,1
        testStone1 = new Stone(Stone.Shape.v, Stone.Color.B);
        testStone2 = new Stone(Stone.Shape.x, Stone.Color.B);
    }

    @Test
    public void testReset() throws Exception {
        b.reset();
        assert(b.getStones().isEmpty());
        assert(b.getPossibleMoves().size() == 1);
    }

    @Test
    public void testDeepCopy() throws Exception {
        Board deepcopy = b.deepCopy();
        assert(b.getStones().toString().equals(deepcopy.getStones().toString()));
        assert(b.getPossibleMoves().toString().equals(deepcopy.getPossibleMoves().toString()));
    }

    @Test
    public void testIsValidMove() throws Exception {
        Stone s1 = new Stone(Stone.Shape.c, Stone.Color.R);
        assertFalse(b.isValidMove(0,0, s1));
        assertFalse(b.isValidMove(4,0, s1));
        assert(b.isValidMove(0,-1, s1));
        assertFalse(b.isValidMove(1,1, s1));
        assert(b.isValidMove(1,1, testStone));
    }

    @Test
    public void testIsValidMove1() throws Exception {
        Stone s1 = new Stone(Stone.Shape.c, Stone.Color.R);
        assertFalse(b.isValidMove(new Position(0,0), s1));
        assertFalse(b.isValidMove(new Position(4,0), s1));
        assertTrue(b.isValidMove(new Position(0,-1), s1));
        assertFalse(b.isValidMove(new Position(1,1), s1));
        assertTrue(b.isValidMove(new Position(1,1), testStone));
    }

    @Test
    public void testIsValidMove2() throws Exception {
        List<PossibleMove> pmlist = new ArrayList<>(b.getPossibleMoves().values());
        for (PossibleMove p : pmlist) {
            for (Stone s : stonelist) {
                if (p.acceptable(s)) {
                    assertTrue(b.isValidMove(p,s));
                }
            }
        }
    }

    @Test
    public void testMakeMove() throws Exception {
        b.makeMove(0,0, testStone);
        assertFalse(b.getStones().containsValue(testStone));
        b.makeMove(0,-1, testStone);
        assertFalse(b.getStones().containsValue(testStone));
        b.makeMove(1,1, testStone);
        assert(b.getStones().containsValue(testStone));
        assert(b.getPossibleMoves().containsKey(new Position(1,2)));
        assert(b.getPossibleMoves().containsKey(new Position(2,1)));
    }

    @Test
    public void testMakeMove1() throws Exception {
        b.makeMove(new Position(1,1), testStone);
        assert(b.getStones().containsValue(testStone));
        assert(b.getPossibleMoves().containsKey(new Position(1,2)));
    }

    @Test
    public void testMakeMoves() throws Exception {
        List<Position> positions = new ArrayList<>();
        List<Stone> stones = new ArrayList<>();
        stones.add(testStone);
        positions.add(new Position(0, 0));
        try {
            b.makeMoves(positions, stones);
        } catch (InvalidMoveException e) {
            assertFalse(b.deepCopy().getStones().containsValue(testStone));
            setUp();
        }
        positions = new ArrayList<>();
        positions.add(new Position(1,1));
        b.makeMoves(positions, stones);
        assert(b.getStones().containsValue(testStone));
        stones = new ArrayList<>();
        positions = new ArrayList<>();
        stones.add(testStone2);
        positions.add(new Position(5,0));
        stones.add(testStone1);
        positions.add(new Position(4,0));
        b.makeMoves(positions, stones);
        assert(b.getStones().containsValue(testStone1));
        assert(b.getStones().containsValue(testStone2));

    }

    @Test
    public void testSameColumn() throws Exception {

    }

    @Test
    public void testSameRow() throws Exception {

    }

    @Test
    public void testAllStonesOneRow() throws Exception {

    }

    @Test
    public void testAllOneRow() throws Exception {

    }

    @Test
    public void testConnectedRow() throws Exception {

    }

    @Test
    public void testConnectedColumn() throws Exception {

    }

    @Test
    public void testMakeMove2() throws Exception {
        b.makeMove(testStone, b.getPossibleMoves().get(new Position(1,1)));
        assertTrue(b.getStones().containsValue(testStone));
        assertTrue(b.getPossibleMoves().containsKey(new Position(1,2)));
    }

    @Test
    public void testAddPossibleMove() throws Exception {

    }

    @Test
    public void testGetBoundaries() throws Exception {

    }

    @Test
    public void testToString() throws Exception {

    }

    @Test
    public void testToString1() throws Exception {

    }

    @Test
    public void testCalculatePoints() throws Exception {

    }
}