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
import javafx.scene.layout.BorderPane;
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
	public final String currency;
	private double valueFromBase;
	private boolean dataInitilized = false;
	private JSONObject data;
	private ObservableList<DataPoint> currentRelativeValues;
	private ObservableList<DataPoint> pastData;

	/**
	 * Constructor for An InfoDisplay of a particular currency
	 * **Note : the data in the display tables will not be initialized until they are requested by the user,
	 * and cached after they have been
	 * @param currency The String Key for a currency
	 * @param valueFromBase Relative value from the Environment base currency
	 */
	public InfoDisplay(String currency, Double valueFromBase){
		this.currency = currency;
		this.valueFromBase = valueFromBase;
		this.currentRelativeValues = FXCollections.observableArrayList();
		this.pastData = FXCollections.observableArrayList();
	}

	/**
	 * Wrapper class for a datapoint used in the TableViews
	 */
	public final class DataPoint{
		private final SimpleStringProperty key;
		private final SimpleDoubleProperty value;
		private final String keyString;
		private final Double valueDouble;

		/**
		 * Constructor for a DataPoint
		 * @param k key for the DataPoint
		 * @param v value for the DataPoint
		 */
		public DataPoint(String k, Double v){
			this.key = new SimpleStringProperty(k);
			this.value = new SimpleDoubleProperty(v);
			this.keyString = k;
			this.valueDouble = v;
		}

		/**
		 * Getter for Key
		 * @return value of key
		 */
		public String getKey(){
			return this.key.get();
		}

		/**
		 * Getter for Value
		 * @return value of value
		 */
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

	/**
	 * Change the relative value from the Environment base currency
	 * @param newVal new value to change to
	 */
	public void changeBaseValue(double newVal){
		this.valueFromBase = newVal;
		this.currentRelativeValues = FXCollections.observableArrayList();
		this.pastData = FXCollections.observableArrayList();
		this.dataInitilized = false;
	}

	//Helper method to set data
	private void setData(){
		this.data = new JSONObject();
		this.currentRelativeValues = FXCollections.observableArrayList();
		this.pastData = FXCollections.observableArrayList();

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

	//Helper method to generate the main Pane to display data
	private VBox generateMainBox(){
		VBox mainTemp = new VBox();
		Style.setMidPaneStyle(mainTemp);
		
		BorderPane topPanel = new BorderPane();


		HBox leftTop = new HBox();
		leftTop.setAlignment(Pos.CENTER_LEFT);

		Label currencyLabel = new Label(this.currency);
		Style.setMidPaneStyle(currencyLabel);
		leftTop.getChildren().add(currencyLabel);

		HBox rightTop = new HBox();
		Label valueLabel = new Label("= " + this.valueFromBase + " " + Environment.BASE);
		Style.setMidPaneStyle(valueLabel);
		rightTop.getChildren().add(valueLabel);

		topPanel.setLeft(leftTop);
		topPanel.setRight(rightTop);

		

		VBox midPanel = new VBox();
		midPanel.setAlignment(Pos.CENTER);

		TableView relativeTable = new TableView();
		Style.setDefaultStyle(relativeTable);
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
			Style.setMidPaneStyle(noDataLabel);
			bottomPanel.getChildren().add(noDataLabel);
		}
		else if(!this.currency.equals(Environment.BASE)){
			Label historyTableTitle = new Label("Value History");
			Style.setMidPaneStyle(historyTableTitle);

			TableView historyTable = new TableView();
			Style.setDefaultStyle(historyTable);
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

	/**
	 * If the data in the tables has not been initialized it will be initialized and the display for that data will be returned
	 * @return display pane
	 */
	public Pane getDisplay(){
		if (!dataInitilized){
			setData();
		}
		return generateMainBox();
	}

}

