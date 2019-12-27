package app;

//Javafx imports
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class Environment extends Application{
	public static String BASE = "USD";

	public void start(Stage mainStage){
		BorderPane mainPane = new BorderPane();

		try{
			HttpResponse<String> response = Unirest.get("https://api.exchangeratesapi.io/latest")
													.queryString("base", Environment.BASE)
													.asString();
			String text = response.getBody();
			System.out.println(text);
			JSONObject js =  new JSONObject(text);

			System.out.println(js.getJSONObject("rates").get("CAD"));

		} catch (Exception e){
			System.out.println(e.getMessage());
		}

		Scene s = new Scene(mainPane);
		mainStage.setScene(s);
		mainStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}


}