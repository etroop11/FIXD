package app;

import java.util.ArrayList;
import java.util.Iterator;

//Javafx imports
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class Environment extends Application{
	public static String BASE = "USD";
	private JSONObject data;
	private ArrayList<String> currencies = new ArrayList();

	public void start(Stage mainStage){
		BorderPane mainPane = new BorderPane();

		HBox topPane = new HBox();
		VBox sidePane = new VBox();

		if(setData()){
		 	currencies = getKeys();
			System.out.println(currencies);
		} else {
			//Defaiult to no connection/Error screen
		}

		
		Scene s = new Scene(mainPane);
		mainStage.setScene(s);
		mainStage.show();
	}

	public ArrayList<String> getKeys(){//should throw an exception rather than handle natively, use this for now
		if(this.data != null){
			ArrayList<String> strs = new ArrayList();
			Iterator i = data.getJSONObject("rates").keys();
			while(i.hasNext()){
				strs.add((String)i.next());
			}
			return strs;
		}
		return null;
	}

	public boolean setData(){
		try{
			HttpResponse<String> response = Unirest.get("https://api.exchangeratesapi.io/latest")
													.queryString("base", Environment.BASE)
													.asString();
			String text = response.getBody();
			System.out.println(text);
			this.data = new JSONObject(text);
			return true;
		} catch(Exception e){//Make this more specefic for later
			System.out.println(e.getMessage());
			return false;
		}
		
		
	}

	public static void main(String[] args) {
		launch(args);
	}


}