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
	private int width = 20;
	private int height = 20;
	private int foodX = 0;
	private int foodY = 0;
	private int cornersize = 25;
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

	public Game() {
		reset();
	}

	public Game(String playerName) {
		this.playerName = playerName;
	}

	public void run() {
		newFood();

		VBox root = new VBox();
		Canvas canvas = new Canvas(width * cornersize, height * cornersize);

		graphicsContext = canvas.getGraphicsContext2D();
		root.getChildren().add(canvas);

		timer();

		Scene scene = new Scene(root, width * cornersize, height * cornersize);
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
			foodX = rand.nextInt(width);
			foodY = rand.nextInt(height);

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
		snake.add(new Corner(width / 2, height / 2));
		snake.add(new Corner(width / 2, height / 2));
		snake.add(new Corner(width / 2, height / 2));
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

		for (int i = snake.size() - 1; i >= 1; i--) {
			snake.get(i).x = snake.get(i - 1).x;
			snake.get(i).y = snake.get(i - 1).y;
		}

		switch (direction) {
		case up:
			snake.get(0).y--;
			if (snake.get(0).y < 0) {
				gameOver = true;
			}
			break;
		case down:
			snake.get(0).y++;
			if (snake.get(0).y > height) {
				gameOver = true;
			}
			break;
		case left:
			snake.get(0).x--;
			if (snake.get(0).x < 0) {
				gameOver = true;
			}
			break;
		case right:
			snake.get(0).x++;
			if (snake.get(0).x > width) {
				gameOver = true;
			}
			break;
		}
		eatFood();
		selfDestroy();
		setBackground();
		setScoreStyle();
		setRandomFoodColor();
		setSnakeColorAndSize();
	}

	private void eatFood() {
		if (foodX == snake.get(0).x && foodY == snake.get(0).y) {
			snake.add(new Corner(-1, -1));
			scoreResult++;
			newFood();
		}
	}

	private void selfDestroy() {
		for (int i = 1; i < snake.size(); i++) {
			if (snake.get(0).x == snake.get(i).x && snake.get(0).y == snake.get(i).y) {
				gameOver = true;
			}
		}
	}

	private void setBackground() {
		switch (SnakeFxController.BACKGROUND_COLOR) {
		case 1:
			graphicsContext.setFill(Color.BLACK);
			graphicsContext.fillRect(0, 0, width * cornersize, height * cornersize);
			break;
		case 2:
			graphicsContext.setFill(Color.WHITE);
			graphicsContext.fillRect(0, 0, width * cornersize, height * cornersize);
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
		graphicsContext.fillOval(foodX * cornersize, foodY * cornersize, cornersize, cornersize);
	}

	private void setSnakeColorAndSize() {
		for (Corner c : snake) {
			switch (SnakeFxController.SHAPE_SNAKE) {
			case 1:
				graphicsContext.setFill(Color.LINEN);
				graphicsContext.fillOval(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
				graphicsContext.setFill(setSnakeColor());
				graphicsContext.fillOval(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
				break;
			case 2:
				graphicsContext.setFill(Color.LINEN);
				graphicsContext.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 1, cornersize - 1);
				graphicsContext.setFill(setSnakeColor());
				graphicsContext.fillRect(c.x * cornersize, c.y * cornersize, cornersize - 2, cornersize - 2);
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
