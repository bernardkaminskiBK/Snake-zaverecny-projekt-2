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

// TODO: Auto-generated Javadoc
/**
 * 
 * Trieda nastavuje funkcionalitu a kontrolu v hlavnej okne(Menu).
 *
 */
public class SnakeFxController implements Initializable {

	/** The text area. */
	@FXML
	private TextArea textArea;

	/** The player name. */
	@FXML
	private TextField playerName;

	/** The pane controls. */
	@FXML
	private Pane basePane, panePlayerName, optionPane, paneControls;

	/** The btn option ok. */
	@FXML
	private Button btnOkControls, btnControls, btnOk, btnOption, btnOptionOk;

	/** The snake color red. */
	@FXML
	private RadioButton rdBtnBlack, rdBtnWhite, rdBtnRectSnake, rdBtnOvalSnake, snakeColorGreen, snakeColorOrange,
			snakeColorPurple, snakeColorRed;

	/** The Constant BLACK_BACKGROUND_COLOR. */
	private static final int BLACK_BACKGROUND_COLOR = 1;

	/** The Constant WHITE_BACKGROUND_COLOR. */
	private static final int WHITE_BACKGROUND_COLOR = 2;

	/** The Constant SNAKE_COLOR_GREEN. */
	private static final int SNAKE_COLOR_GREEN = 1;

	/** The Constant SNAKE_COLOR_ORANGE. */
	private static final int SNAKE_COLOR_ORANGE = 2;

	/** The Constant SNAKE_COLOR_PURPLE. */
	private static final int SNAKE_COLOR_PURPLE = 3;

	/** The Constant SNAKE_COLOR_RED. */
	private static final int SNAKE_COLOR_RED = 4;

	/** The Constant OVAL_SNAKE. */
	private static final int OVAL_SNAKE = 1;

	/** The Constant RECTANGLE_SNAKE. */
	private static final int RECTANGLE_SNAKE = 2;

	/** The Constant MAX_LETTERS_FOR_PLAYER_NAME. */
	private static final int MAX_LETTERS_FOR_PLAYER_NAME = 3;

	/** The game. */
	private Game game;

	/** The db. */
	private DB db = new DB();

	/** The players. */
	private ArrayList<Player> players;

	/** The set player name. */
	private String setPlayerName;

	/** The background color. */
	static int BACKGROUND_COLOR = 1;

	/** The shape snake. */
	static int SHAPE_SNAKE = 1;

	/** The color snake. */
	static int COLOR_SNAKE = 1;

	/**
	 * Odchytavac udalosti zaznamenava tlacenie tlacitka new game v menu ktora
	 * spusta hru.
	 * 
	 * @param event udalost kliknutia tlacitka New game v menu.
	 */
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

	/**
	 * Zavolanim tejto metody sa spusti hra. Za podmienku ze hrac musi zadat hracske
	 * meno. Ma povolene zadania mena iba max 3 pismenka.
	 */
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

	/**
	 * Odchytavac udalosti btnControls s tlacenim prepne z hlavneho menu do moznosti
	 * controls kde nasledne je pouzivatel informovany o ovladani hry.
	 * 
	 * @param event udalost kliknutie na tlacitko Controls v menu.
	 */
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

	/**
	 * Odchytavac udalosti zaznamenava kliknutie na tlacitko btnOption nasledne z
	 * moznosti hlavneho menu pouzivatela prepne do sekcie nastavenia(options). Kde
	 * ma moznost vyber z roznych nastaveni. Zmena farby pozadia a hadika.
	 * 
	 * @param event udalost kliknutie na tlacitko option v menu.
	 */
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

	/**
	 * Stlacenim tlacitka quit v menu program ukonci pracu s appkou.
	 * 
	 * @param event udalost kliknutie na tlacitko quit/koniec hry.
	 */
	@FXML
	public void btnQuit(ActionEvent event) {
		System.exit(0);
	}

	/**
	 * Posluchac udalosti btnReset s kliknutim resetuje vsetky zaznamy v databaze.
	 * 
	 * @param event kliknutie na tlacitko reset v menu/reset vsetky zaznamy.
	 */
	@FXML
	public void btnReset(ActionEvent event) {
		for (Player player : players) {
			db.removePlayer(new Player(player.getMeno()));
		}
		setTextArea();
	}

	/**
	 * Odchytavac udalosti kliknutim na tlacitko Refresh v menu znovu nacita obsah
	 * databazy.
	 * 
	 * @param event kliknutie na tlacitko refresh nacitanie aktualneho stavu
	 *              databazy.
	 */
	@FXML
	public void btnRefresh(ActionEvent event) {
		setTextArea();
	}

	/**
	 * Nastavy a zobrazi podla nahratych bodovs informacie do textoveho pola v menu
	 * o hracoch(meno, skore).
	 */
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

	/**
	 * Vola sa na inicializaciu root po uplnom spracovani jeho korenoveho prvku.
	 *
	 * @param location  the location
	 * @param resources the resources
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setTextArea();
	}
}
