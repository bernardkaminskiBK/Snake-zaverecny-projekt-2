package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

import Database.DB;
import Database.Player;
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

public class Game {

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

	private static final int WIDTH = 20;
	private static final int HEIGHT = 20;
	private static final int CORNERSIZE = 25;
	private static final int FIRST_CORNER_IN_ARRAY_SNAKE = 0;

	public Game() {
		reset();
	}

	public Game(String playerName) {
		this.playerName = playerName;
	}

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

	private void reset() {
		speed = 5;
		snake = new ArrayList<>();
		gameOver = false;
	}

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

	private void initialShapeOfSnake() {
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
		snake.add(new Corner(WIDTH / 2, HEIGHT / 2));
	}

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

	private void tick() {
		if (gameOver) {
			db.addNewPlayer(new Player(playerName, getScoreResult()));
			JOptionPane.showMessageDialog(null, "Game over");
			snakeStage.close();
			SnakeFx.mainStage.show();
			timer.stop();
			return;
		}

		isGameOver(direction);
		eatFood();
		selfDestroy();
		setBackground();
		setScoreStyle();
		setRandomFoodColor();
		setSnakeColorAndSize();
	}

	public void isGameOver(Dir direction) {
		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;
		}

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

	private void eatFood() {
		if (foodX == snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x && foodY == snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y) {
			snake.add(new Corner(-1, -1));
			scoreResult++;
			newFood();
		}
	}

	private void selfDestroy() {
		for (int i = 1; i < snake.size(); i++) {
			if (snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).x == snake.get(i).x
					&& snake.get(FIRST_CORNER_IN_ARRAY_SNAKE).y == snake.get(i).y) {
				gameOver = true;
			}
		}
	}

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
		}
		return cc;
	}

	public void gamePause() {
		snakeStage.addEventFilter(KeyEvent.KEY_PRESSED, key -> {
			if (key.getCode() == KeyCode.P) {
				timer.stop();
				graphicsContext.setFill(Color.DARKGREEN);
				graphicsContext.setFont(new Font("", 50));
				graphicsContext.fillText("PAUSE", 180, 250);
			} else {
				timer.start();
			}
		});
	}

	public int getScoreResult() {
		return scoreResult;
	}
}
