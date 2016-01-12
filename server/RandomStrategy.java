package server;

import java.util.Map;

public class RandomStrategy implements Strategy {

	public RandomStrategy() {
	}

	@Override
	public String getName() {
		return "Random";
	}

	@Override
	public int determineMove() {
		Map<Position, PossibleMove> possibleMoves = getGame().getPossibleMoves();
		return (int) Math.random() * (possibleMoves.size() + 1);
	}

}
