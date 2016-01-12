package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Game {

	private List<Stone> bag;
	private Player[] players;
	private Player currentPlayer;
	private Board board;
	private View view;

	public Game(String[] names) {
		players = new Player[names.length];
		this.board = new Board();
		this.view = new View(this);
		for (int i=0; i<names.length; i++) {
			players[i] = new Player(names[i], this);
		}
	}

	public void start(){
		int i = players.length;
		int j = ((int) Math.random() * (i + 1));
		while (!hasWinner()) {
			currentPlayer = players[j];
			currentPlayer.makeMove();
			j = (j + 1) % i;
			
		}
	}
	
	public Map<int[], PossibleMove> getPossibleMoves() {
		return board.getPossibleMoves();
	}

	
	public Boolean hasWinner() {
		return false;
	}

	public void reset(){
		
	}

	public List<Stone> stones(){
		return bag;
	}
	
	public View getView() {
		return view;
	}
	
	/* returns the amount of stones to the player
	 * @param: player and amount
	 */
	public void giveStones(Player player, int amount) {
		List<Stone> randomStones = new ArrayList<Stone>();
		for (int i = 0; i < amount; i++) {
			int place = ((int) Math.random() * (bag.size() + 1));
			randomStones.add(bag.get(place));
			bag.remove(place);
		}
		player.takeStones(randomStones);
	}

	public Player currentPlayer(){
		return currentPlayer;
	}

	public void showGUI(){
		
	}
	

}
