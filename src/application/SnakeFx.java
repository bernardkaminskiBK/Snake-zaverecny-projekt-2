package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class SnakeFx extends Application {
	/**
	 * Tento komment sluzi ako skuska na push a commit na github 28.9.2020
	 */
	static Stage mainStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("SnakeFxView.fxml"));
			Scene scene = new Scene(root);
			primaryStage.setTitle("Snake");
			primaryStage.getIcons().add(new Image("obrazky/snake-icon.png"));
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.show();
			mainStage = primaryStage;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
