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

	/**
	 * Base Pull method that generates a server request and returns the data
	 * @param append String to be appended to main URL
	 * @param queries the Query titles in order
	 * @param values the Query values in order
	 * @return A JSON object with the requested information. If there is no connection, or another error
	 * :  returns formatted JSON object detailing the error
	 */
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
					return response;
				} catch (UnirestException ue){
					JSONObject errorJSON = new JSONObject();
					errorJSON.put("error", "Error Parsing: " + ue.getMessage());
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
					return response;
				} catch (UnirestException ue){
					JSONObject errorJSON = new JSONObject();
					errorJSON.put("error", "Error Parsing (3)");
					return errorJSON;
				} catch (Exception e){
					return null;
				}
			}
		} else {
			JSONObject response = new JSONObject();
			JSONObject ratesObj = new JSONObject();
			ratesObj.put("Data Not Available", 0.0);
			response.put("error", "No Connection");
			response.put("rates", ratesObj);
			return response;
		}
	}

	/**
	 * Convenience method for Pulling data from the Server
	 * @param append the string to be appended to the url
	 * @param pairs Query name and value pairs
	 * @return A JSON object with the requested information. If there is no connection, or another error
	 *  :  returns formatted JSON object detailing the error
	 */
	public static JSONObject pull(String append, String[][] pairs){
		if(pairs.length != 2){
			if(pairs[0].length != pairs[1].length)
			return null;
		} else if(pairs[0].length != pairs[1].length) {
			return null;
		}
		return pull(append, pairs[0], pairs[1]);
	}

	//Check the connection
	private static boolean connected(){
		try{
			URL u = new URL("https://www.google.com");
			u.openConnection().connect();
		} catch(Exception e){
			return false;
		}
		return true;
	}

}