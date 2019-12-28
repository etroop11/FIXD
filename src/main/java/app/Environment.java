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
	public static String BASE = "USD";
	private JSONObject data;
	private ArrayList<String> currencies = new ArrayList();
	private Calendar currentDate;
	private Calendar selectedDate;
	public void start(Stage mainStage){
		//Defaults init
		dataInitHelper();

		BorderPane mainPane = new BorderPane();

		HBox topPane = new HBox();
		
		mainPane.setLeft(genLeftPanel());
		mainPane.setTop(genTopPanel());
		
		Scene s = new Scene(mainPane);
		mainStage.setScene(s);
		mainStage.show();
	}

	private void dataInitHelper(){
		this.currentDate = Calendar.getInstance();
		this.selectedDate = currentDate;
		
		setData();
		this.currencies = getKeys("rates");
	}

	private HBox genTopPanel(){
		HBox hb = new HBox();
		hb.setAlignment(Pos.CENTER_RIGHT);

		Label fromLabel = new Label("Base Currency : ");

		ComboBox currentCurrencyBox = new ComboBox();
		currentCurrencyBox.setItems(FXCollections.observableList(this.currencies));
		currentCurrencyBox.setValue(currentCurrencyBox.getItems().get(currentCurrencyBox.getItems().indexOf(Environment.BASE)));

		Label dateLabel = new Label("   At Date : Day : ");
		TextField dayField = new TextField("" + selectedDate.get(Calendar.DAY_OF_MONTH));

		Label monthLabel = new Label(" Month : ");
		TextField monthField = new TextField("" + (selectedDate.get(Calendar.MONTH) + 1));

		Label yearLabel = new Label(" Year : ");
		TextField yearField = new TextField("" + selectedDate.get(Calendar.YEAR));

		Button goButton = new Button("Apply");

		hb.getChildren().add(fromLabel);
		hb.getChildren().add(currentCurrencyBox);
		hb.getChildren().add(dateLabel);
		hb.getChildren().add(dayField);
		hb.getChildren().add(monthLabel);
		hb.getChildren().add(monthField);
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

	public ArrayList<String> getKeys(String forKey){//should throw an exception rather than handle natively, use this for now
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
		
			HttpResponse<String> response = Unirest.get("https://api.exchangeratesapi.io/latest")
													.queryString("base", Environment.BASE)
													.asString();
			String text = response.getBody();
			this.data = new JSONObject(text);
		
	}

	public static void main(String[] args) {
		launch(args);
	}


}