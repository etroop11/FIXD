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
import javafx.collections.ObservableList;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class Environment extends Application{
	public static String BASE = "USD";
	private JSONObject data;
	private ArrayList<String> currencies = new ArrayList();

	public void start(Stage mainStage){

		//shoudl read from file to store Default data
		BorderPane mainPane = new BorderPane();

		HBox topPane = new HBox();
		

		VBox sidePane = new VBox();

		if(setData()){
		 	this.currencies = getKeys();
			
		} else {
			//Defaiult to no connection/Error screen
		}


		mainPane.setLeft(genLeftPanel());
		
		Scene s = new Scene(mainPane);
		mainStage.setScene(s);
		mainStage.show();
	}

	private HBox genTopPanel(){

		return null;
	}

	private VBox genLeftPanel(){
		VBox sidePane =  new VBox();
		
		ObservableList<String> obsList = FXCollections.observableList(this.currencies);
		sidePane.getChildren().add(new ListView(obsList));

		return sidePane;
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