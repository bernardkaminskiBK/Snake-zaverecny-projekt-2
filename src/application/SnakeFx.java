package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// TODO: Auto-generated Javadoc
/**
 * 
 * Trieda spusta hru a nastavuje hlavne okno(Menu).
 * 
 */
public class SnakeFx extends Application {

	/** The main stage. */
	static Stage mainStage;

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 */
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

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
