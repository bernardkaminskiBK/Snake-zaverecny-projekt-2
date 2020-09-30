package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import database.DB;
import database.Player;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

/**
 * Koment sluzi na skusku na git hub
 */
public class SnakeFxController implements Initializable {

	@FXML
	private TextArea textArea;
	@FXML
	private TextField playerName;
	@FXML
	private Pane basePane;
	@FXML
	private Pane panePlayerName;
	@FXML
	private Pane optionPane;
	@FXML
	private Pane paneControls;
	@FXML
	private Button btnOkControls;
	@FXML
	private Button btnControls;
	@FXML
	private Button btnOk;
	@FXML
	private Button btnOption;
	@FXML
	private Button btnOptionOk;
	@FXML
	private RadioButton rdBtnBlack;
	@FXML
	private RadioButton rdBtnWhite;
	@FXML
	private RadioButton rdBtnRectSnake;
	@FXML
	private RadioButton rdBtnOvalSnake;
	@FXML
	private RadioButton snakeColorGreen;
	@FXML
	private RadioButton snakeColorOrange;
	@FXML
	private RadioButton snakeColorPurple;
	@FXML
	private RadioButton snakeColorRed;

	private static final int BLACK_BACKGROUND_COLOR = 1;
	private static final int WHITE_BACKGROUND_COLOR = 2;

	private static final int SNAKE_COLOR_GREEN = 1;
	private static final int SNAKE_COLOR_ORANGE = 2;
	private static final int SNAKE_COLOR_PURPLE = 3;
	private static final int SNAKE_COLOR_RED = 4;

	private static final int OVAL_SNAKE = 1;
	private static final int RECTANGLE_SNAKE = 2;

	private static final int MAX_LETTERS_FOR_PLAYER_NAME = 3;

	private Game game;
	private DB db = new DB();
	private ArrayList<Player> players;
	private String setPlayerName;
	static int BACKGROUND_COLOR = 1;
	static int SHAPE_SNAKE = 1;
	static int COLOR_SNAKE = 1;

	@FXML
	public void btnNewGame(ActionEvent event) {
		basePane.setDisable(true);
		basePane.setOpacity(0.3);
		panePlayerName.setVisible(true);

		playerName.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			if (key.getCode() == KeyCode.ENTER) {
				setPlayerName = playerName.getText();
				runGame();
			}
		});

		btnOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				setPlayerName = playerName.getText();
				runGame();
			}
		});
		setTextArea();
	}

	private void runGame() {
		if (!setPlayerName.isEmpty() && setPlayerName.length() >= MAX_LETTERS_FOR_PLAYER_NAME) {
			SnakeFx.mainStage.hide();
			game = new Game(setPlayerName.toUpperCase().substring(0, 3));
			game.run();
			basePane.setDisable(false);
			basePane.setOpacity(1);
			panePlayerName.setVisible(false);
			playerName.setText("");
		}
	}

	@FXML
	public void btnControls(ActionEvent event) {
		basePane.setVisible(false);
		paneControls.setVisible(true);

		btnOkControls.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				paneControls.setVisible(false);
				basePane.setVisible(true);
			}
		});
	}

	@FXML
	public void btnOption(ActionEvent event) {
		basePane.setVisible(false);
		optionPane.setVisible(true);

		rdBtnBlack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				BACKGROUND_COLOR = BLACK_BACKGROUND_COLOR;
			}
		});

		rdBtnWhite.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				BACKGROUND_COLOR = WHITE_BACKGROUND_COLOR;
			}
		});

		rdBtnRectSnake.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SHAPE_SNAKE = RECTANGLE_SNAKE;
			}
		});

		rdBtnOvalSnake.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				SHAPE_SNAKE = OVAL_SNAKE;
			}
		});

		snakeColorGreen.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				COLOR_SNAKE = SNAKE_COLOR_GREEN;
			}
		});

		snakeColorOrange.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				COLOR_SNAKE = SNAKE_COLOR_ORANGE;
			}
		});

		snakeColorPurple.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				COLOR_SNAKE = SNAKE_COLOR_PURPLE;
			}
		});

		snakeColorRed.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				COLOR_SNAKE = SNAKE_COLOR_RED;
			}
		});

		btnOptionOk.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				optionPane.setVisible(false);
				basePane.setVisible(true);
			}
		});

	}

	@FXML
	public void btnQuit(ActionEvent event) {
		System.exit(0);
	}

	@FXML
	public void btnReset(ActionEvent event) {
		for (Player player : players) {
			db.removePlayer(new Player(player.getMeno()));
		}
		setTextArea();
	}

	@FXML
	public void btnRefresh(ActionEvent event) {
		setTextArea();
	}

	public void setTextArea() {
		players = db.getAllPlayers();
		players.sort(Player.SCORE);

		String result = "";
		int lineCounter = 0;
		result += String.format("%-10s", "High scores: ") + "\n";
		result += "----------------------\n";
		for (Player player : players) {
			lineCounter++;
			result += String.format("%-5s%5s%10s", lineCounter + ".", player.getMeno(), player.getScore()) + "\n";
		}
		textArea.setText(result);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setTextArea();
	}
}
