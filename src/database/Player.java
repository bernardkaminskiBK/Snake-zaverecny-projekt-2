package database;

import java.util.Comparator;

/**
 * Trieda reprezentuje hraca.
 */
public class Player {

	/** The meno. */
	private String meno;

	/** The score. */
	private int score;
	/**
	 * Porovnava hracov podla ich nahratych bodov.
	 */
	public static Comparator<Player> SCORE = new Comparator<Player>() {
		@Override
		public int compare(Player player1, Player player2) {
			return Integer.compare(player2.getScore(), player1.getScore());
		}
	};

	/**
	 * Vytvara hraca s menom a score.
	 * 
	 * @param meno  meno hraca.
	 * @param score score hraca.
	 */
	public Player(String meno, int score) {
		this.meno = meno;
		this.score = score;
	}

	/**
	 * Vytvara hraca s menom.
	 * 
	 * @param meno meno hraca.
	 */
	public Player(String meno) {
		this.meno = meno;
	}

	/**
	 * Vrati meno hraca.
	 * 
	 * @return meno hraca.
	 */
	public String getMeno() {
		return meno;
	}

	/**
	 * Vrati skore hraca.
	 * 
	 * @return skore hraca.
	 */
	public int getScore() {
		return score;
	}
}
