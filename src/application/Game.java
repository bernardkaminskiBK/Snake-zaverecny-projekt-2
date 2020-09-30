package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import database.DB;
import database.Player;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * 
 * Trieda reprezuntuje hru snake nasledne nastavuje celu hru.
 *
 */
public class Game {

	/** The speed. */
	private int speed = 5;

	/** The foodcolor. */
	private int foodcolor = 0;

	/** The food X. */
	private int foodX = 0;

	/** The food Y. */
	private int foodY = 0;

	/** The snake. */
	private List<Corner> snake = new ArrayList<>();

	/** The direction. */
	private Dir direction = Dir.left;

	/** The game over. */
	private boolean gameOver = false;

	/** The rand. */
	private Random rand = new Random();

	/** The snake stage. */
	private Stage snakeStage;

	/** The timer. */
	private AnimationTimer timer;

	/** The graphics context. */
	private GraphicsContext graphicsContext;

	/** The score result. */
	private int scoreResult = 0;

	/** The db. */
	private DB db = new DB();

	/** The player name. */
	private String playerName;

	/** The paused. */
	private boolean paused;

	/** The Constant WIDTH. */
	private static final int WIDTH = 20;

	/** The Constant HEIGHT. */
	private static final int HEIGHT = 20;

	/** The Constant CORNERSIZE. */
	private static final int CORNERSIZE = 25;

	/** The Constant FIRST_CORNER_IN_ARRAY_SNAKE. */
	private static final int FIRST_CORNER_IN_ARRAY_SNAKE = 0;

	/**
	 * Volanim konstruktora sa hra nastavy do tzv. pociatocneho stavu hry.
	 */
	public Game() {
		reset();
	}

	/**
	 * Nastavy meno hraca.
	 * 
	 * @param playerName meno hraca.
	 */
	public Game(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Nastavy hracske okno. Spusta celu hru.
	 */
	public void run() {
		newFood();

		VBox root = new VBox();
		Canvas canvas = new Canvas(WIDTH * CORNERSIZE, HEIGHT * CORNERSIZE);

		graphicsContext = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		timer();

		Scene scene = new Scene(root, WIDTH * CORNERSIZE, HEIGHT * CORNERSIZE);
		setMoveControl(scene);
		initialShapeOfSnake();

		snakeStage = new Stage();
		snakeStage.setScene(scene);
		snakeStage.setTitle("Snake run");
		snakeStage.getIcons().add(new Image("obrazky/snake-icon.png"));
		snakeStage.setResizable(false);
		snakeStage.show();

		gamePause();
	}

	/**
	 * Nastavy hru do pociatocneho stavu.
	 */
	private void reset() {
		speed = 5;
		snake = new ArrayList<>();
		gameOver = false;
	}

	/**
	 * Generuje gulicku(gulicka reprezentuje hadie jedlo/food) ktora sa umiestnuje
	 * na nahodnych x-vych a y-vych suradniciach. Pri konzumacii tzv. foodo sa
	 * navysuje rychlost priebehu hry a nastavy sa nahodna farba foodo.
	 */
	private void newFood() {
		start: while (true) {
			foodX = rand.nextInt(WIDTH);
			foodY = rand.nextInt(HEIGHT);

			for (Corner c : snake) {
				if (c.x == foodX && c.y == foodY) {
					continue start;
				}
			}
			foodcolor = rand.nextInt(6);
			speed++;
			break;
		}
	}

	/**
	 * V metode sa nachadza trieda AnimationTimer ktory umoznuje vytvorit casovac
	 * ktory umoznoje spravit pohyblivu animaciu a zrychlovat pohyb hadika na
	 * platne.
	 */
	private void timer() {
		timer = new AnimationTimer() {
			long lastTick = 0;

			@Override
			public void handle(long now) {
				if (lastTick == 0) {
					lastTick = now;
					tick();
					return;
				}
				if (now - lastTick > 1000000000 / speed) {
					lastTick = now;
					tick();
				}
			}
		};
		timer.start();
	}

	/**
	 * Nastavuje zaciatocny stav hadika. Zaciatocny velkost hadika sa sklada z troch
	 * casti.
	 */
	private void initialShapeOfSnake() {
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
	}

	/**
	 * Nastavuje inputy z klavesnice ktore su zodpovedne za ovladanie hry.
	 * 
	 * @param scene scene nastavenie ovladanie hry.
	 */
	private void setMoveControl(Scene scene) {
		scene.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			if (key.getCode() == KeyCode.W) {
				direction = Dir.up;
			}
			if (key.getCode() == KeyCode.A) {
				direction = Dir.left;
			}
			if (key.getCode() == KeyCode.S) {
				direction = Dir.down;
			}
			if (key.getCode() == KeyCode.D) {
				direction = Dir.right;
			}
		});
	}

	/**
	 * Metoda tick kontroluje ci je koniec hry v pripade ak je koniec hry prida
	 * hraca do databazy.
	 */
	private void tick() {
		if (gameOver) {
			db.addNewPlayer(new Player(playerName, getScoreResult()));
			JOptionPane.showMessageDialog(null, "Game over");
			snakeStage.close();
			SnakeFx.mainStage.show();
			timer.stop();
			return;
		}

		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;
		}

		isGameOver(direction);
		eatFood();
		selfDestroy();
		setBackground();
		setScoreStyle();
		setRandomFoodColor();
		setSnakeColorAndSize();
	}

	/**
	 * Metoda kontroluje hadika podla x-ovych a y-ovych surdanic kde sa nachadza
	 * prave na platne a podla toho vyhodnocuje koniec hry. Ak hadik presiahne svoje
	 * limity hore, dole, pravo, vlavo na platne podla nastavenej sirky a vysky
	 * platna program vyhodnocuje hru za Game over.
	 * 
	 * @param direction pohyb smeru hadika na platne.
	 */
	public void isGameOver(Dir direction) {

		switch (direction) {
		case up:
			snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y--;
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y < 0) {
				gameOver = true;
			}
			break;
		case down:
			snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y++;
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y > HEIGHT) {
				gameOver = true;
			}
			break;
		case left:
			snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x--;
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x < 0) {
				gameOver = true;
			}
			break;
		case right:
			snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x++;
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x > WIDTH) {
				gameOver = true;
			}
			break;
		}
	}

	/**
	 * Metoda kontroluje zhodu hadika x/y z food x/y ak je zhoda program vyhodnocuje
	 * zhodu(hadik zjedol svoje jedlo) pridava dalsiu cast k hadikovi a navysuje
	 * skore hracovi o jednicku. Nasledne vola metodu newFood().
	 */
	private void eatFood() {
		if (foodX == snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x && foodY == snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y) {
			snake.add(new Corner(-1, -1));
			scoreResult++;
			newFood();
		}
	}

	/**
	 * Metoda kontroluje pripad ak hadik sa narazil do seba v tom pripade nastavuje
	 * premennu game over na true, hra sa konci.
	 */
	private void selfDestroy() {
		for (int i = 1; i < snake.size(); i++) {
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x == snake.get(i).x
					&& snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y == snake.get(i).y) {
				gameOver = true;
			}
		}
	}

	/**
	 * Nastavuje pozadie hracskej plochy podla vyberu pouzivatela Cierna/Biela.
	 */
	private void setBackground() {
		switch (SnakeFxController.BACKGROUND_COLOR) {
		case 1:
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.fillRect(0, 0, WIDTH * CORNERSIZE, HEIGHT * CORNERSIZE);
			break;
		case 2:
			graphicsContext.setFill(Color.WHITE);
			graphicsContext.fillRect(0, 0, WIDTH * CORNERSIZE, HEIGHT * CORNERSIZE);
			break;
		}
	}

	/**
	 * Nastavuje styl a farbu pisma Score a celkovy pocet pozbieranych bodov pocas
	 * trvania hry na obrazovku.
	 */
	private void setScoreStyle() {
		switch (SnakeFxController.BACKGROUND_COLOR) {
		case 1:
			graphicsContext.setFill(Color.WHITE);
			graphicsContext.setFont(new Font("", 30));
			graphicsContext.fillText("Score: " + (speed - 6), 10, 30);
			break;
		case 2:
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.setFont(new Font("", 30));
			graphicsContext.fillText("Score: " + (speed - 6), 10, 30);
			break;
		}

	}

	/**
	 * Metoda nastavuje velkost jedla(food) a nahodnym vyberom z moznosti farieb
	 * nastavy farbu jedla(food).
	 */
	private void setRandomFoodColor() {
		Color cc = Color.RED;
		switch (foodcolor) {
		case 0:
			cc = Color.PURPLE;
			break;
		case 1:
			cc = Color.LIGHTBLUE;
			break;
		case 2:
			cc = Color.YELLOW;
			break;
		case 3:
			cc = Color.PINK;
			break;
		case 4:
			cc = Color.ORANGE;
			break;
		case 5:
			cc = Color.FORESTGREEN;
			break;
		}
		graphicsContext.setFill(cc);
		graphicsContext.fillOval(foodX * CORNERSIZE, foodY * CORNERSIZE, CORNERSIZE, CORNERSIZE);
	}

	/**
	 * Metoda nastavuje farbu a velkost hadika.
	 */
	private void setSnakeColorAndSize() {
		for (Corner c : snake) {
			switch (SnakeFxController.SHAPE_SNAKE) {
			case 1:
				graphicsContext.setFill(Color.LINEN);
				graphicsContext.fillOval(c.x * CORNERSIZE, c.y * CORNERSIZE, CORNERSIZE - 1, CORNERSIZE - 1);
				graphicsContext.setFill(setSnakeColor());
				graphicsContext.fillOval(c.x * CORNERSIZE, c.y * CORNERSIZE, CORNERSIZE - 2, CORNERSIZE - 2);
				break;
			case 2:
				graphicsContext.setFill(Color.LINEN);
				graphicsContext.fillRect(c.x * CORNERSIZE, c.y * CORNERSIZE, CORNERSIZE - 1, CORNERSIZE - 1);
				graphicsContext.setFill(setSnakeColor());
				graphicsContext.fillRect(c.x * CORNERSIZE, c.y * CORNERSIZE, CORNERSIZE - 2, CORNERSIZE - 2);
				break;
			}
		}
	}

	/**
	 * Vrati zvolenu farbu podla pouzivatela. Nasledne podla vyberu nastavy farbu
	 * hadika.
	 * 
	 * @return zvolena farba.
	 */
	private Color setSnakeColor() {
		Color cc = null;
		switch (SnakeFxController.COLOR_SNAKE) {
		case 1:
			cc = Color.GREEN;
			break;
		case 2:
			cc = Color.ORANGE;
			break;
		case 3:
			cc = Color.PURPLE;
			break;
		case 4:
			cc = Color.RED;
			break;
		}
		return cc;
	}

	/**
	 * Metoda stlacenim tlacitka P zastavy animation timer(Pausne). Ak pouzivatel
	 * znova stlaci tlacitko P na klavesnicy znovu sa spusti casovac a hra sa
	 * pokracuje dalej.
	 */
	public void gamePause() {
		snakeStage.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			if (key.getCode() == KeyCode.P) {
				if (paused) {
					paused = false;
					timer.start();
				} else {
					paused = true;
					graphicsContext.setFill(Color.DARKGREEN);
					graphicsContext.setFont(new Font("", 50));
					graphicsContext.fillText("PAUSE", 180, 250);
					timer.stop();
				}
			}
		});
	}

	/**
	 * Vrati celkovy pocet pozbieranych bodov hraca.
	 * 
	 * @return cekovy pocet bodov.
	 */
	public int getScoreResult() {
		return scoreResult;
	}
}
