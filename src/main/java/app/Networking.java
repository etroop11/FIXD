package app;

import java.util.HashMap;

//Unirest Imports
import kong.unirest.json.JSONObject;
import kong.unirest.UnirestException;
import kong.unirest.Unirest;

//Java Net Imports
import java.net.URL;
import java.net.URLConnection;

public class Networking{
	public static final String SERVER_URL = "https://api.exchangeratesapi.io";


	public static JSONObject pull(String append, String[] queries, String[] values){
		if(connected()){
			if(queries.length != values.length || append == null){
				return null;
			} else if(queries.length == 0){
				String finalURL = SERVER_URL + append;
				try{
					JSONObject response = new JSONObject(Unirest.get(finalURL).asString().getBody());
					if(response.has("error")){
						response = new JSONObject();
						JSONObject ratesObj = new JSONObject();
						ratesObj.put("Data Not Available", 0.0);

						response.put("error", "Parsing Error (1)");
						response.put("rates", ratesObj);
					}
					System.out.println("finished");
					return response;
				} catch (UnirestException ue){
					JSONObject errorJSON = new JSONObject();
					errorJSON.put("error", "Error Parsing: " + ue.getMessage());
					System.out.println("finished");
					return errorJSON;
				} catch(Exception e){
					return null;
				}

			} else {
				try{
					HashMap<String, Object> mapPairs = new HashMap();
					for(int i = 0; i < queries.length; i++){
						mapPairs.put(queries[i], values[i]);
					}

					String finalURL = SERVER_URL + append;

					JSONObject response = new JSONObject(Unirest.get(finalURL).queryString(mapPairs).asString().getBody());
					if(response.has("error")){
						response = new JSONObject();

						JSONObject ratesObj = new JSONObject();
						ratesObj.put("Data Not Available", 0.0);

						response.put("error", "Parsing Error (2)");
						response.put("rates", ratesObj);

					}
					System.out.println("finished");
					return response;
				} catch (UnirestException ue){
					JSONObject errorJSON = new JSONObject();
					System.out.println(ue.getMessage());
					errorJSON.put("error", "Error Parsing (3)");
					System.out.println("finished");
					return errorJSON;
				} catch (Exception e){
					return null;
				}
			}
		} else {
			JSONObject response = new JSONObject();
			JSONObject ratesObj = new JSONObject();
			ratesObj.put("Data Not Available", 0.0);

			response.put("error", "No COnnection");
			response.put("rates", ratesObj);
			System.out.println("ERROR 4");
			return response;
		}
	}

	public static JSONObject pull(String append, String[][] pairs){
		if(pairs.length != 2){
			return null;
		}
		return pull(append, pairs[0], pairs[1]);
	}

	public static boolean connected(){
		try{
			URL u = new URL("https://www.google.com");
			System.out.println("trying to connect");
			u.openConnection().connect();
			System.out.println("connected");
		} catch(Exception e){
			System.out.println(e);
			return false;
		}
		return true;
	}

}