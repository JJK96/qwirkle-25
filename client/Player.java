package client;

import java.util.*;

import shared.*;

public class Player extends Observable {

	private String name;
	private int points;
	private List<Stone> stones;
	private ClientGame game;

	/**
	 * Create a player with specified name and game.
	 * 
	 * @param name
	 */
	public Player(String name, ClientGame game) {
		stones = new ArrayList<Stone>();
		points = 0;
		this.name = name;
		this.game = game;

	}

	/**
	 * The move the player makes.
	 */
	public void makeMove() {

	}

	/**
	 * Gets the game the player plays.
	 * 
	 * @return game
	 */
	public ClientGame getGame() {
		return game;
	}

	/**
	 * Gets the name of the player.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the stones the player has.
	 * 
	 * @return stones
	 */
	public List<Stone> getStones() {
		return stones;
	}

	/**
	 * Creates a string representation of the given stones. Before every
	 * stone is a number added so choosing is easy using the view.
	 * 
	 * @return String representation of the stones with a number for every
	 *         stone.
	 */
	public String stonesToString(List<Stone> stonelist) {
		String stoneString = "";
        for (int i = 0; i < stonelist.size(); i++) {
            stoneString += i + " " + stonelist.get(i).toString() + "\n";
        }
		return stoneString;
	}

	/**
	 * Get the string representation of the stones of the player.
	 * 
	 * @return stonesToString(stones)
	 */
	public String thisStonesToString() {
		return stonesToString(stones);
	}

	/**
	 * Adds the given stones to the stones of the player.
	 *
	 * @param
	 */
	public void takeStones(List<Stone> stonesToTake) {
		for (Stone s : stonesToTake) {
			stones.add(s);
		}
	}
	
	/**
	 * Removes the specified stones from the players stones.
	 * 
	 * @param stoneList
	 */
	public void removeStones(List<Stone> stoneList) {
		for (Stone s : stoneList) {
			stones.remove(s);
		}
	}

	/**
	 * Removes the specified stone from the players stones.
	 * 
	 * @param stone
	 */
	public void removeStone(Stone stone) {
		this.stones.remove(stone);
	}

	/**
	 * Add the given points to the players points.
	 * 
	 * @param
	 */
	public void addPoints(int pointsToAdd) {
		this.points = this.points + pointsToAdd;
	}

	/**
	 * Gets the points of the player.
	 * 
	 * @return points
	 */
	public int getPoints() {
		return points;
	}
	
	/**
	 * Returns an adapted list of possiblemoves containing only moves on which the player can actually
	 * play given his stones. And the stones that are placed already.
	 *
	 * @param pmlist
	 * @param stonesOfPlayer
	 * @param stonesplaced
	 * @return a list of possiblemoves where the player can place a stone.
	 */
	public static List<PossibleMove> adaptPossibleMoves(List<PossibleMove> pmlist,
					List<Stone> stonesOfPlayer, List<Stone> stonesplaced, Board b) {
		List<PossibleMove> newpmlist = new ArrayList<>();
		for (PossibleMove p : pmlist) {
			boolean oneRow = true;
			if (!stonesplaced.isEmpty()) {
				List<Space> spacelist = new ArrayList<>();
				spacelist.addAll(stonesplaced);
				spacelist.add(p);
				oneRow = b.allOneRow(spacelist);
			}
			if (oneRow && hasStones(p, stonesOfPlayer)) {
				newpmlist.add(p);
			}
		}
		return newpmlist;
	}
	
	/**
	 * Determines if the player can trade.
	 * 
	 * @param stonesplaced
	 * @return true if 
	 */
	public boolean canTrade(int stonesplaced, boolean traded) {
		return game.getMoveCount() != 1 && stonesplaced == 0 && !traded;
	}

	/**
	 * Determines if the player can end his turn.
	 * @param stonesplaced
	 * @return 	true if the player is allowed to end his turn
	 * 			false if the player may not end his turn at this point in the game.
     */
	public boolean canEnd(int stonesplaced) {
		return game.getMoveCount() != 1 && stonesplaced != 0;
	}

	/**
	 * Determines if a player has one or more stones that can be played 
	 * on the specified possible move.
	 * 
	 * @param p
	 * @param stonesOfPlayer
	 * @return 	true if the player has such a stone.
	 * 			false otherwise.
	 */
	public static boolean hasStones(PossibleMove p, List<Stone> stonesOfPlayer) {
        for (Stone s : stonesOfPlayer) {
            if (p.acceptable(s)) {
				return true;
            }
        }
		return false;
	}

	/**
	 * Determines which stones the player can place on the specified possible move.
	 * 
	 * @param stonelist
	 * @param place
	 * @return All stones that can be laid on the specified possible move
	 */
	public static List<Stone> adaptStones(List<Stone> stonelist, PossibleMove place) {
		List<Stone> acceptableStones = new ArrayList<>();
		for (Stone s : stonelist) {
			if (place.acceptable(s)) {
				acceptableStones.add(s);
			}
		}
		return acceptableStones;
	}

}
