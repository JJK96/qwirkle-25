package client;

import java.util.List;
import shared.*;

public interface Strategy {

	public int determineMove(ClientGame game, List<Stone> stones);
}
