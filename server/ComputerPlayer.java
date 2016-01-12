package server;

import java.util.List;
import java.util.Map;

public class ComputerPlayer extends Player {
	
	private Strategy strategy;

	public ComputerPlayer(String name, Game game, Strategy strategy) {
		super(name, game);
		this.strategy = strategy;

	}

	@Override
	public void makeMove() {
		Map<Position, PossibleMove> possibleMoves = getGame().getPossibleMoves();
		List<Stone> stones = getStones();
		PossibleMove place = possibleMoves.get(strategy.determineMove());
		for (Stone s : stones) {
			if (place.acceptable(s)) {
				getGame().getBoard().makeMove(s, place);
				break;
			}
		}
	}
}
