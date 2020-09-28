package database;

import java.util.Comparator;

public class Player {

	private String meno;
	private int score;

	public static Comparator<Player> SCORE = new Comparator<Player>() {
		@Override
		public int compare(Player player1, Player player2) {
			return Integer.compare(player2.getScore(), player1.getScore());
		}
	};

	public Player(String meno, int score) {
		this.meno = meno;
		this.score = score;
	}

	public Player(String meno) {
		this.meno = meno;
	}

	public String getMeno() {
		return meno;
	}

	public int getScore() {
		return score;
	}

	@Override
	public String toString() {
		return "Player [meno=" + meno + ", score=" + score + "]";
	}

}
