package Database;

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

public class DB {

	private static final String JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";
	private static final String URL = "jdbc:derby:SnakeFxDB;create=true";
	private static final String USERNAME = "";
	private static final String PASSWORD = "";
	private Connection conn = null; // Zostrojime spojenie (most)
	private Statement createStatement = null;
	private DatabaseMetaData dbmd = null;

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

	public void removePlayer(Player player) {
		String sql = "delete from players where name = '" + player.getMeno() + "'";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.execute();
		} catch (SQLException e) {
			System.out.println("There is something wrong with delete contacts from database.");
			e.printStackTrace();
		}
	}
}
