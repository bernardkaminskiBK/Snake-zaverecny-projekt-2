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

/**
 * The class represents the game of the snake and then sets the whole game.
 */
public class Game {

	private static final int Y_POSITION_BACKGROUND_WHITE = 0;
	private static final int X_POSITION_BACKGROUND_WHITE = 0;

	private static final int Y_POSITION_BACKGROUND_BLACK = 0;
	private static final int X_POSITION_BACKGROUND_BLACK = 0;

	private static final int Y_POSITION_TEXT_PAUSE = 250;
	private static final int X_POSITON_TEXT_PAUSE = 180;
	private static final int FONT_SIZE_PAUSE = 50;

	private static final int Y_POSITION_TEXT_SCORE = 30;
	private static final int X_POSITION_TEXT_SCORE = 10;
	private static final int FONT_SIZE_SCORE = 30;

	private static final int LEFT_SIDE_BORDER = 0;
	private static final int UPPER_LIMIT = 0;

	private static final int WIDTH = 20;
	private static final int HEIGHT = 20;
	private static final int CORNERSIZE = 25;
	private static final int FIRST_CORNER_IN_ARRAY_SNAKE = 0;

	private int speed = 5;
	private int foodcolor = 0;
	private int foodX = 0;
	private int foodY = 0;

	private List<Corner> snake = new ArrayList<>();
	private Dir direction = Dir.left;
	private boolean gameOver = false;
	private Random rand = new Random();
	private Stage snakeStage;
	private AnimationTimer timer;
	private GraphicsContext graphicsContext;
	private int scoreResult = 0;
	private DB db = new DB();
	private String playerName;
	private boolean paused;

	/**
	 * By calling the constructor, the game is set to the so-called the initial
	 * state of the game.
	 */
	public Game() {
		reset();
	}

	/**
	 * Set the player name.
	 * 
	 * @param playerName name of the player.
	 */
	public Game(String playerName) {
		this.playerName = playerName;
	}

	/**
	 * Game window settings. Starts the whole game.
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
	 * Set the game to its initial state.
	 */
	private void reset() {
		speed = 5;
		snake = new ArrayList<>();
		gameOver = false;
	}

	/**
	 * Generates a ball (the ball represents snake food), which is placed on random
	 * x and y coordinates. When consuming the food increases the speed of the game
	 * and sets a random color of the food.
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
	 * The method includes the AnimationTimer class, which allows to create a timer,
	 * that allows to make a moving animation and speed up the movement of the snake
	 * on the canvas.
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
	 * Sets the initial state of the snake. The initial size of the snake consists
	 * of three parts.
	 */
	private void initialShapeOfSnake() {
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
	}

	/**
	 * Sets the inputs from the keyboard that are responsible for controlling the
	 * game.
	 * 
	 * @param scene add to scene game control settings.
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
	 * The tick method checks if the end of the game is in case the end of the game
	 * is added players to the database.
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
	 * The method checks the snake according to the x and y coordinates where it is
	 * located right on the boards and evaluates the end of the game accordingly. If
	 * the snake exceeds its limits up, down, right, left on the canvas according to
	 * the set width and height the valid program evaluates the game as Game over
	 * 
	 * @param direction movement of the snake on the canvas.
	 */
	public void isGameOver(Dir direction) {

		switch (direction) {
		case up:
			snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y--;
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y < UPPER_LIMIT) {
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
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x < LEFT_SIDE_BORDER) {
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
	 * The method checks the compliance of the snake x / y from the food x / y if
	 * the compliance is evaluated by the program match (the snake ate its food)
	 * adds another part to the snake and raises scores of a player. It then calls
	 * the newFood() method.
	 */
	private void eatFood() {
		if (foodX == snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x && foodY == snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y) {
			snake.add(new Corner(-1, -1));
			scoreResult++;
			newFood();
		}
	}

	/**
	 * The method checks whether the snake have collided with each other. If so, the
	 * game is over.
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
	 * Sets the background of the playing area according to the user selection
	 * (Black / White.)
	 */
	private void setBackground() {
		switch (SnakeFxController.BACKGROUND_COLOR) {
		case 1:
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.fillRect(X_POSITION_BACKGROUND_BLACK, Y_POSITION_BACKGROUND_BLACK, WIDTH * CORNERSIZE,
					HEIGHT * CORNERSIZE);
			break;
		case 2:
			graphicsContext.setFill(Color.WHITE);
			graphicsContext.fillRect(X_POSITION_BACKGROUND_WHITE, Y_POSITION_BACKGROUND_WHITE, WIDTH * CORNERSIZE,
					HEIGHT * CORNERSIZE);
			break;
		}
	}

	/**
	 * Sets the style and color of the Score font and the total number of points
	 * collected.
	 */
	private void setScoreStyle() {
		switch (SnakeFxController.BACKGROUND_COLOR) {
		case 1:
			graphicsContext.setFill(Color.WHITE);
			graphicsContext.setFont(new Font("", FONT_SIZE_SCORE));
			graphicsContext.fillText("Score: " + (speed - 6), X_POSITION_TEXT_SCORE, Y_POSITION_TEXT_SCORE);
			break;
		case 2:
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.setFont(new Font("", FONT_SIZE_SCORE));
			graphicsContext.fillText("Score: " + (speed - 6), X_POSITION_TEXT_SCORE, Y_POSITION_TEXT_SCORE);
			break;
		}

	}

	/**
	 * The method sets the size of the food and sets the color of the food by
	 * randomly selecting from a color option.
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
	 * The method sets the color and size of the snake.
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
	 * Returns the selected color according to the user.
	 * 
	 * @return selected color.
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
	 * Method by pressing the P button stops the animation timer (Pause). If the
	 * user presses the P button on the keyboard again, the timer starts again and
	 * the game continues.
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
					graphicsContext.setFont(new Font("", FONT_SIZE_PAUSE));
					graphicsContext.fillText("PAUSE", X_POSITON_TEXT_PAUSE, Y_POSITION_TEXT_PAUSE);
					timer.stop();
				}
			}
		});
	}

	/**
	 * Returns the total number of player points collected.
	 * 
	 * @return total number of points.
	 */
	public int getScoreResult() {
		return scoreResult;
	}
}
