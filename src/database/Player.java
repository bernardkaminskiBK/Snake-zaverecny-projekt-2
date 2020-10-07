package database;

import java.util.Comparator;

/**
 * The class represents the player.
 */
public class Player {

	private String name;
	private int score;

	/**
	 * Compares players according to their recorded points.
	 */
	public static Comparator<Player> SCORE = new Comparator<Player>() {
		@Override
		public int compare(Player player1, Player player2) {
			return Integer.compare(player2.getScore(), player1.getScore());
		}
	};

	/**
	 * Create a player with a name and score.
	 * 
	 * @param name  of the player.
	 * @param score of the player.
	 */
	public Player(String meno, int score) {
		this.name = meno;
		this.score = score;
	}

	/**
	 * Create a player with a name.
	 * 
	 * @param name of the player.
	 */
	public Player(String name) {
		this.name = name;
	}

	/**
	 * Return the name of the player.
	 * 
	 * @return name of the player.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the score of the player.
	 * 
	 * @return score of the player.
	 */
	public int getScore() {
		return score;
	}
}
