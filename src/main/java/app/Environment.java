package app;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

public class Environment extends Application{

	public void start(Stage mainStage){
		Scene s = new Scene(new BorderPane());

		mainStage.setScene(s);
		mainStage.show();
	}

	public static void main(String[] args) {
		System.out.println("HELLO");
		launch(args);
	}


}