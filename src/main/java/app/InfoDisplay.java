package app;

//Java Util Imports
import java.util.Calendar;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//JavaFX imports
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class InfoDisplay{
	public final String currency;//maybe make private
	private double valueFromBase;
	private boolean dataInitilized = false;
	private JSONObject data;
	private ObservableList<DataPoint> currentRelativeValues;
	private ObservableList<DataPoint> pastData;

	public InfoDisplay(String currency, Double valueFromBase){
		this.currency = currency;
		this.valueFromBase = valueFromBase;
		this.currentRelativeValues = FXCollections.observableArrayList();
		this.pastData = FXCollections.observableArrayList();
	}

	//wrapper class for historical data point
	public final class DataPoint{
		private final SimpleStringProperty key;
		private final SimpleDoubleProperty value;
		private final String keyString;
		private final Double valueDouble;

		public DataPoint(String k, Double v){
			this.key = new SimpleStringProperty(k);
			this.value = new SimpleDoubleProperty(v);
			this.keyString = k;
			this.valueDouble = v;
		}

		public String getKey(){
			return this.key.get();
		}

		public Double getValue(){
			return this.value.get();
		}

		public boolean equals(DataPoint other){
			if(other != null 
				&& this.keyString != null 
				&& this.valueDouble != null
				&& other.keyString != null
				&& other.valueDouble != null)
				return this.keyString.equals(other.keyString) && this.valueDouble.equals(other.valueDouble);
			return false;
		}


	}

	public void changeBaseValue(double newVal){
		this.valueFromBase = newVal;
		this.currentRelativeValues = FXCollections.observableArrayList();
		this.pastData = FXCollections.observableArrayList();
		this.dataInitilized = false;
	}

	private void setData(){
		this.data = new JSONObject();
		this.currentRelativeValues = FXCollections.observableArrayList();
		this.pastData = FXCollections.observableArrayList();
		//Iniitilize Data with this currency as base
		//DO ERROR HANDLING
		boolean success = true;
		String[][] dataArgs = {	{"base"},
								{this.currency}};
		this.data = Networking.pull("/latest", dataArgs);
		if(!data.has("error")){
			Iterator currencies = data.getJSONObject("rates").keys();
			while(currencies.hasNext()){
				String c = (String)currencies.next();
				Double v = data.getJSONObject("rates").getDouble(c);
				this.currentRelativeValues.add(new DataPoint(c, v));
			}
		} else {
			this.currentRelativeValues.add(new DataPoint("No Data Available", 0.0));
			success = false;
		}

		Calendar now = Environment.getCurrentDate();
		int yearNow = now.get(Calendar.YEAR);
		int monthNow = now.get(Calendar.MONTH) + 1;
		int dayNow = now.get(Calendar.DAY_OF_MONTH);
	
		String keyString = yearNow + "-" + monthNow + "-" + dayNow;


		String[][] historyArgs = {
						{"start_at", "end_at", "base", "symbols"},
						{Environment.START_DATE, keyString, Environment.BASE, this.currency}};
		JSONObject history = Networking.pull("/history", historyArgs);
		
		if(!history.has("error")){
			Iterator keys = history.getJSONObject("rates").keys();

			ArrayList<String> dates = new ArrayList();
			while(keys.hasNext()){
				dates.add((String)keys.next());
			}
			Collections.sort(dates);
			
			for(int i = 0; i < dates.size(); i ++){
				Double v =  history.getJSONObject("rates").getJSONObject(dates.get(i)).getDouble(currency);
				//System.out.println(dates.get(i) + " : " +  v);
				DataPoint dp = new DataPoint(dates.get(i), v);
				this.pastData.add(dp);
			}
		} else {
			DataPoint dp = new DataPoint("No Data Available", 0.0);
			this.pastData.add(dp);
			success = false;

		}

		if(success)
			this.dataInitilized = true;
	}

	private VBox generateMainBox(){
		VBox mainTemp = new VBox();

		HBox topPanel = new HBox();
		topPanel.setAlignment(Pos.CENTER);

		HBox leftTop = new HBox();
		leftTop.setAlignment(Pos.CENTER_LEFT);
		Label currencyLabel = new Label(this.currency);
		leftTop.getChildren().add(currencyLabel);

		HBox rightTop = new HBox();
		rightTop.setAlignment(Pos.CENTER_RIGHT);
		Label valueLabel = new Label("" + this.valueFromBase + " " + Environment.BASE);
		rightTop.getChildren().add(valueLabel);

		topPanel.getChildren().add(leftTop);
		topPanel.getChildren().add(rightTop);

		

		VBox midPanel = new VBox();
		midPanel.setAlignment(Pos.CENTER);

		TableView relativeTable = new TableView();
		relativeTable.setEditable(false);

		TableColumn<DataPoint, String> currencyCol = new TableColumn("Currency");
		currencyCol.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("key"));

		TableColumn<DataPoint, Double> relValCol = new TableColumn("Value in " + this.currency);
		relValCol.setCellValueFactory(new PropertyValueFactory<DataPoint, Double>("value"));

		relativeTable.getColumns().add(currencyCol);
		relativeTable.getColumns().add(relValCol);
		relativeTable.setItems(this.currentRelativeValues);

		midPanel.getChildren().add(relativeTable);


		VBox bottomPanel = new VBox();
		bottomPanel.setAlignment(Pos.CENTER);
		if(this.pastData.get(0).equals(new DataPoint("No Data Available", 0.0))){
			Label noDataLabel = new Label("No Value History Data Available for this Currency");
			bottomPanel.getChildren().add(noDataLabel);
		}
		else if(!this.currency.equals(Environment.BASE)){
			Label historyTableTitle = new Label("Value History");

			TableView historyTable = new TableView();
			historyTable.setEditable(false);

			TableColumn<DataPoint,String> dateCol = new TableColumn("Date");
			dateCol.setCellValueFactory(new PropertyValueFactory<DataPoint, String>("key"));

			TableColumn<DataPoint, Double> valCol = new TableColumn("Value in " + Environment.BASE);
			valCol.setCellValueFactory(new PropertyValueFactory<DataPoint, Double>("value"));

			historyTable.getColumns().add(dateCol);
			historyTable.getColumns().add(valCol);
			historyTable.setItems(this.pastData);

			bottomPanel.getChildren().add(historyTableTitle);
			bottomPanel.getChildren().add(historyTable);
		}


		mainTemp.getChildren().add(topPanel);
		mainTemp.getChildren().add(midPanel);
		mainTemp.getChildren().add(bottomPanel);

		return mainTemp;


	}

	public Pane getDisplay(){
		if (!dataInitilized){
			setData();
		}
		return generateMainBox();
	}

}

