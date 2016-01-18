package client;

import java.util.List;
import shared.*;

public interface Strategy {

	public void determineMove(ClientGame game, List<Stone> stones);
}
