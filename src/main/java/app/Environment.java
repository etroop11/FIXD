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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.event.EventHandler;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class Environment extends Application{
	//public static final String SERVER_URL = "https://api.exchangeratesapi.io";
	public static String BASE = "CAD";
	public static String START_DATE = "2000-01-01";
	private static Calendar START_DATE_FORMAT;
	private static Calendar currentDate;
	private static boolean dataCorrect = true;

	private JSONObject data;
	private ArrayList<String> currencies = new ArrayList();

	private Calendar selectedDate;
	private InfoDisplay[] infoDisplays;
	private InfoDisplay currentDisplay;
	private BorderPane mainPane;

	public void start(Stage mainStage){
		//Defaults init
			dataInitHelper();
			this.mainPane = new BorderPane();
			
			

			mainPane.setCenter(this.currentDisplay.getDisplay());

			if(dataCorrect){
				mainPane.setLeft(genLeftPanel());
				mainPane.setTop(genTopPanel());
			}

			Scene s = new Scene(mainPane);
			mainStage.setScene(s);
			mainStage.show();
	

	}

	//shoudl check bound and stuff
	private void dataInitHelper(){
		currentDate = Calendar.getInstance();
		this.selectedDate = currentDate;
		
		this.START_DATE_FORMAT = Calendar.getInstance();
		this.START_DATE_FORMAT.clear();
		this.START_DATE_FORMAT.set(2000, 1, 1);

		this.setData();

		this.currencies = getKeys("rates");
		

		this.createInfoDisplays();
		this.currentDisplay = this.infoDisplays[0];

		//System.out.println("DataInit Finished");
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
		goButton.setOnAction(event -> {
			//format text

			String year = yearField.getText(), month = monthField.getText(), day = dayField.getText();
			String errorMessage = "";
			Calendar tryDate;
			String newBase;
			JSONObject tempData;
			JSONObject ratesData;

			try{
				int yearNum = Integer.parseInt(year);
				int monthNum = Integer.parseInt(month) - 1;
				int dayNum = Integer.parseInt(day);

				tryDate = Calendar.getInstance();
				tryDate.setLenient(false);
				tryDate.set(yearNum, monthNum, dayNum);
				tryDate.getTime();

				String baseString = "";
				if(tryDate.before(currentDate) && tryDate.after(START_DATE_FORMAT)){
					baseString = "/" + tryDate.get(Calendar.YEAR) + "-"
									+ (tryDate.get(Calendar.MONTH) + 1)+ "-"
									+ tryDate.get(Calendar.DAY_OF_MONTH);
				} else if(this.currentDate.get(Calendar.YEAR) == yearNum 
							&& this.currentDate.get(Calendar.MONTH) == monthNum
							&& this.currentDate.get(Calendar.DAY_OF_MONTH) == dayNum){
					baseString = "/latest";
				} else {
					throw new Exception("Selected Date is not in Range.");
				}
			
				
				newBase = (String)currentCurrencyBox.getValue();

				
				String[][] newBaseArgs = {	{"base"}, 
											{newBase}};
				tempData = Networking.pull(baseString, newBaseArgs);
				
				if(tempData.has("error")){
					throw new Exception("Data not retrievable.");
				}

				ratesData = tempData.getJSONObject("rates");

				if(tryDate == null || newBase == null || tempData == null || ratesData == null){
					throw new Exception("Error retreiving Data");
				}

		 	} catch (Exception e){
				Alert a = new Alert(Alert.AlertType.ERROR);
				a.show();
				return;
			}


			for(int i = 0; i < this.infoDisplays.length; i ++){
				if(ratesData.has(this.infoDisplays[i].currency)){
					Double d = ratesData.getDouble(this.infoDisplays[i].currency);
					this.infoDisplays[i].changeBaseValue(d);
				} else {
					this.infoDisplays[i].changeBaseValue(0.0);
				}
			}
			this.selectedDate = tryDate;
			this.data = tempData;
			Environment.BASE = newBase;




			this.mainPane.setCenter(this.currentDisplay.getDisplay());


				
		});

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
		ListView view = new ListView(obsList);
		view.getSelectionModel().selectedItemProperty().addListener(
			(obs, oldVal, newVal) -> {
				for(InfoDisplay inf : this.infoDisplays){
					if(inf.currency.equals(newVal)){
						this.currentDisplay = inf;
						this.mainPane.setCenter(currentDisplay.getDisplay());
						break;
					}
				}
			});
		sidePane.getChildren().add(view);

		return sidePane;
	}




	public ArrayList<String> getKeys(String forKey){
		if(!this.data.has("error")){
			ArrayList<String> strs = new ArrayList();
			Iterator i = data.getJSONObject(forKey).keys();
			while(i.hasNext()){
				strs.add((String)i.next());
			}
			return strs;
		}
		return new ArrayList();
	}

	public void setData(){
			String[][] dataArgs = {	{"base"},
								{Environment.BASE}};

			this.data = Networking.pull("/latest", dataArgs);

	}

	private void createInfoDisplays(){
		if(this.currencies.size() != 0){
			this.infoDisplays = new InfoDisplay[this.currencies.size()];
			for(int i = 0; i < this.infoDisplays.length; i ++){
				String key = this.currencies.get(i);
				Double val = this.data.getJSONObject("rates").getDouble(key);
				//System.out.println("Key : " + key + " | val : " + val);
				this.infoDisplays[i] = new InfoDisplay(key, val);
			}
		} else {
			this.infoDisplays = new InfoDisplay[1];
			this.infoDisplays[0] = new ErrorDisplay();
			this.dataCorrect = false;
		}
	}

	public static Calendar getCurrentDate(){
		return currentDate;
	}

	public static void main(String[] args) {
		launch(args);
	}


}