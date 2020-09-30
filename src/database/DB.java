package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Auto-generated Javadoc
/**
 * 
 * Databaza uklada informacie a hracoch(meno a skore).
 *
 */
public class DB {

	/** The Constant JDBC_DRIVER. */
	private static final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

	/** The Constant URL. */
	private static final String URL = "jdbc:derby:SnakeFxDB;create=true";

	/** The Constant USERNAME. */
	private static final String USERNAME = "";

	/** The Constant PASSWORD. */
	private static final String PASSWORD = "";

	/** The conn. */
	private Connection conn = null; // Zostrojime spojenie (most)

	/** The create statement. */
	private Statement createStatement = null;

	/** The dbmd. */
	private DatabaseMetaData dbmd = null;

	/**
	 * Konstruktor vytvara spojenie s databazou derby a tabulku so stlpcami hrac a
	 * skore.
	 */
	public DB() {
		// Skuska spojenia
		try {
			conn = DriverManager.getConnection(URL);
			System.out.println("Connection Successful.");
		} catch (SQLException e) {
			System.out.println("" + e);
			System.out.println("There is something wrong with the connection.");
			Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, e);
		}

		// Ak je spojenie tak vytvorime "dodavku".
		if (conn != null) {
			try {
				createStatement = conn.createStatement();
			} catch (SQLException e) {
				System.out.println("There is something wrong with creating the create statement");
				e.printStackTrace();
			}
		}
		// Testujeme databasu ci je prazdny ? Checkujeme ci existuje data table.
		try {
			dbmd = conn.getMetaData();
		} catch (SQLException e) {
			System.out.println("There is something wrong with creating DatabaseMetaData.");
		}

		try {
			ResultSet rs = dbmd.getTables(null, "APP", "PLAYERS", null);
			if (!rs.next()) {
				String sql = "create table players(name varchar(20), score varchar(30))";
				createStatement.execute(sql);
			}
		} catch (SQLException e) {
			System.out.println("There is something wrong with creating the data table.");
			e.printStackTrace();
		}
	}

	/**
	 * Vrati vsetkych hracov z databazy.
	 * 
	 * @return vsetky hraci.
	 */
	public ArrayList<Player> getAllPlayers() {
		String sql = "select * from players";
		ArrayList<Player> players = null;
		try {
			ResultSet rs = createStatement.executeQuery(sql);
			players = new ArrayList<Player>();
			while (rs.next()) {
				String name = rs.getString("name");
				int score = rs.getInt("score");
				Player actualPlayer = new Player(name, score);
				players.add(actualPlayer);
			}
		} catch (SQLException e) {
			System.out.println("There is something wrong with get all players.");
			e.printStackTrace();
		}
		return players;
	}

	/**
	 * Pridava noveho hraca do databazy.
	 * 
	 * @param player novy hrac.
	 */
	public void addNewPlayer(Player player) {
		String sql = "insert into players values (?,?)";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setString(1, player.getMeno());
			preparedStatement.setInt(2, player.getScore());
			preparedStatement.execute();
		} catch (SQLException e) {
			System.out.println("There is something wrong with add new player to database.");
			e.printStackTrace();
		}
	}

	/**
	 * Odstranuje hraca/hracov z databazy podla mena.
	 * 
	 * @param player hrac.
	 */
	public void removePlayer(Player player) {
		String sql = "delete from players where name = '" + player.getMeno() + "'";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.execute();
		} catch (SQLException e) {
			System.out.println("There is something wrong with delete players from database.");
			e.printStackTrace();
		}
	}
}
