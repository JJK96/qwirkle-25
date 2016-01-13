package server;

import java.util.List;

public interface Strategy {

	public int determineMove(Game game, List<Stone> stones);
}
