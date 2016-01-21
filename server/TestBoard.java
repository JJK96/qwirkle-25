package server;

import shared.*;
import shared.Position;

/**
 * Created by jjk on 1/13/16.
 */
public class TestBoard {
	public static void main(String[] args) {
		Board b = new Board();
		PossibleMove p = b.getPossibleMoves().get(new Position(0, 0));
		b.makeMove(new Stone(Stone.Shape.o, Stone.Color.R), p);
		b.makeMove(1, 0, new Stone(Stone.Shape.o, Stone.Color.O));
		b.makeMove(2, 0, new Stone(Stone.Shape.o, Stone.Color.Y));
		b.makeMove(0, -1, new Stone(Stone.Shape.v, Stone.Color.R));
		System.out.println(b);
	}
}
