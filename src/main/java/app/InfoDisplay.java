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
import javafx.geometry.Pos;

//Unirest Imports
import kong.unirest.Unirest;
import kong.unirest.HttpResponse;
import kong.unirest.json.JSONObject;

public class InfoDisplay{
	public final String currency;//maybe make private
	private double valueFromBase;
	private boolean dataInitilized = false;
	private JSONObject data;
	private HashMap<String, Double> pastData;

	public InfoDisplay(String currency, Double valueFromBase){
		this.currency = currency;
		this.valueFromBase = valueFromBase;
		this.pastData = new HashMap();
	}

	private void setData(){
		//Iniitilize Data with this currency as base
		HttpResponse<String> response = Unirest.get(Environment.SERVER_URL + "/latest")
												.queryString("base", this.currency).asString();
		this.data = new JSONObject(response.getBody());

		Calendar now = Environment.getCurrentDate();
		int yearNow = now.get(Calendar.YEAR);
		int monthNow = now.get(Calendar.MONTH) + 1;
		int dayNow = now.get(Calendar.DAY_OF_MONTH);
	
		String keyString = yearNow + "-" + monthNow + "-" + dayNow;

		HttpResponse<String> historicalDataResponse = Unirest.get(Environment.SERVER_URL + "/history")
															.queryString("start_at", Environment.START_DATE)
															.queryString("end_at", keyString)
															.queryString("base", Environment.BASE)
															.queryString("symbols", this.currency)
															.asString();

		JSONObject history = new JSONObject(historicalDataResponse.getBody());
		
		Iterator keys = history.getJSONObject("rates").keys();
		ArrayList<String> dates = new ArrayList();
		while(keys.hasNext()){
			dates.add((String)keys.next());
		}
		Collections.sort(dates);


		for(int i = 0; i < dates.size(); i ++){
			Double v =  history.getJSONObject("rates").getJSONObject(dates.get(i)).getDouble(currency);
			//System.out.println(dates.get(i) + " : " +  v);
			pastData.put(dates.get(i), v);
		}

		

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
		Label valueLabel = new Label("" + this.valueFromBase);
		rightTop.getChildren().add(valueLabel);

		topPanel.getChildren().add(leftTop);
		topPanel.getChildren().add(rightTop);

		mainTemp.getChildren().add(topPanel);

		return mainTemp;


	}

	public Pane getDisplay(){
		if (!dataInitilized){
			setData();
		}
		return generateMainBox();
	}

}