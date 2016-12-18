package route_planner;

import java.util.concurrent.Callable;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import server.Initialization;

public class OneStopRouting implements Callable<JSONObject> {
	
	double originLat;
	double originLon;
	double poiLat;
	double poiLon;
	double destLat;
	double destLon;

	boolean returnPolyline;
	boolean returnInstruction;
	
	String tag;
	String mode;
	
	public OneStopRouting(double _originLat, double _originLon, double _poiLat, double _poiLon, double _destLat, double _destLon, String _tag,
			boolean _returnPolyline, boolean _returnInstruction, String _mode){
		
		originLat = _originLat;
		originLon = _originLon;
		poiLat = _poiLat;
		poiLon = _poiLon;
		destLat = _destLat;
		destLon = _destLon;
		
		returnPolyline = _returnPolyline;
		returnInstruction = _returnInstruction;
		
		tag = _tag;
		mode = _mode;
	}

	public JSONObject call() throws JSONException{
		
		return solve();
	}
	
	public JSONObject solve() throws JSONException{
		
		JSONObject result = new JSONObject();
		
		// Record the initial parameters
		
		try {
			
			result.put("OriginLat",originLat);
			result.put("OriginLon",originLon);
			result.put("POILat",poiLat);
			result.put("POILon",poiLon);
			result.put("DestinationLat",destLat);
			result.put("DestinationLon",destLon);
			result.put("Mode",mode);
			result.put("Tag",tag);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		Route route1 = Initialization.path_planner.getRoute(originLat,originLon,poiLat,poiLon,returnPolyline,returnInstruction,mode);		
		Route route2 = Initialization.path_planner.getRoute(poiLat,poiLon,destLat,destLon,returnPolyline,returnInstruction,mode);		

		if (route1 != null && route2 != null){
			
			// merge two routes
			Route route = new Route(route1,route2);
	    	result.put("Status","Complete");
	    	result.put("Route",JSONDescription.getRouteDescription(route));

		} else {
			result.put("Status","Route generator error. No feasible route in any form of transportation can be found between your origin and destination.");
		}
		
		return result;
	}

}
