package app;

//Java utils
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;

//Javafx imports
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class Environment extends Application{
	public static final String SERVER_URL = "https://api.exchangeratesapi.io";
	public static String BASE = "USD";
	public static String START_DATE = "2000-01-01";
	private static Calendar currentDate;

	private JSONObject data;
	private ArrayList<String> currencies = new ArrayList();

	private Calendar selectedDate;
	private InfoDisplay[] infoDisplays;
	private InfoDisplay currentDisplay;

	public void start(Stage mainStage){
		//Defaults init
		dataInitHelper();

		BorderPane mainPane = new BorderPane();

		HBox topPane = new HBox();
		
		mainPane.setLeft(genLeftPanel());
		mainPane.setTop(genTopPanel());
		mainPane.setCenter(this.currentDisplay.getDisplay());
		Scene s = new Scene(mainPane);
		mainStage.setScene(s);
		mainStage.show();
	}

	//shoudl check bound and stuff
	private void dataInitHelper(){
		currentDate = Calendar.getInstance();
		this.selectedDate = currentDate;
		
		this.setData();

		this.currencies = getKeys("rates");
		
		this.createInfoDisplays();

		this.currentDisplay = this.infoDisplays[0];

		System.out.println("DataInit Finished");
	}

	private HBox genTopPanel(){
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER_RIGHT);

		Label fromLabel = new Label("Base Currency : ");

		ComboBox currentCurrencyBox = new ComboBox();
		currentCurrencyBox.setItems(FXCollections.observableList(this.currencies));
		currentCurrencyBox.setValue(currentCurrencyBox.getItems().get(currentCurrencyBox.getItems().indexOf(Environment.BASE)));

		Label dateLabel = new Label("   At Date : Month : ");
		TextField monthField = new TextField("" + (selectedDate.get(Calendar.MONTH) + 1));
		
		Label dayLabel = new Label(" Day : ");
		TextField dayField = new TextField("" + selectedDate.get(Calendar.DAY_OF_MONTH));

		Label yearLabel = new Label(" Year : ");
		TextField yearField = new TextField("" + selectedDate.get(Calendar.YEAR));

		Button goButton = new Button("Apply");

		hb.getChildren().add(fromLabel);
		hb.getChildren().add(currentCurrencyBox);
		hb.getChildren().add(dateLabel);
		hb.getChildren().add(monthField);
		hb.getChildren().add(dayLabel);
		hb.getChildren().add(dayField);
		hb.getChildren().add(yearLabel);
		hb.getChildren().add(yearField);
		hb.getChildren().add(goButton);
		return hb;
	}

	private VBox genLeftPanel(){
		VBox sidePane =  new VBox();
		
		ObservableList<String> obsList = FXCollections.observableList(this.currencies);
		sidePane.getChildren().add(new ListView(obsList));

		return sidePane;
	}




	public ArrayList<String> getKeys(String forKey){
		if(this.data != null){
			ArrayList<String> strs = new ArrayList();
			Iterator i = data.getJSONObject(forKey).keys();
			while(i.hasNext()){
				strs.add((String)i.next());
			}
			return strs;
		}
		return null;
	}

	public void setData(){
			HttpResponse<String> response = Unirest.get(SERVER_URL + "/latest")
													.queryString("base", Environment.BASE)
													.asString();
			String text = response.getBody();
			this.data = new JSONObject(text);
	}

	private void createInfoDisplays(){
		this.infoDisplays = new InfoDisplay[this.currencies.size()];
		for(int i = 0; i < this.infoDisplays.length; i ++){
			String key = this.currencies.get(i);
			Double val = this.data.getJSONObject("rates").getDouble(key);
			//System.out.println("Key : " + key + " | val : " + val);
			this.infoDisplays[i] = new InfoDisplay(key, val);
		}
	}

	public static Calendar getCurrentDate(){
		return currentDate;
	}

	public static void main(String[] args) {
		launch(args);
	}


}