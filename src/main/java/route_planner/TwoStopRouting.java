package route_planner;

import java.util.concurrent.Callable;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import server.Initialization;

public class TwoStopRouting implements Callable<JSONObject> {
	
	double originLat;
	double originLon;
	double wptLat;
	double wptLon;
	double poiLat;
	double poiLon;
	double destLat;
	double destLon;

	boolean returnPolyline;
	boolean returnInstruction;
	
	String tag;
	String mode;
	
	public TwoStopRouting(double _originLat, double _originLon, double _wptLat, double _wptLon, double _poiLat, double _poiLon, double _destLat, double _destLon, 
			String _tag, boolean _returnPolyline, boolean _returnInstruction, String _mode){
		
		originLat = _originLat;
		originLon = _originLon;
		wptLat = _wptLat;
		wptLon = _wptLon;
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
			result.put("WptLat",poiLat);
			result.put("WptLon",poiLon);
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
		Route route2 = Initialization.path_planner.getRoute(poiLat,poiLon,wptLat,wptLon,returnPolyline,returnInstruction,mode);		
//		Route route3 = Initialization.path_planner.getRoute(wptLat,wptLon,poiLat,poiLon,returnPolyline,returnInstruction,mode);
		Route route4 = Initialization.path_planner.getRoute(poiLat,poiLon,destLat,destLon,returnPolyline,returnInstruction,mode);		

//		if (route1 != null && route2 != null && route3 != null && route4 != null){
		if (route1 != null && route2 != null && route4 != null){

			// merge two routes
			Route route = new Route(route1,route4);
	    	result.put("Status","Complete");
//	    	result.put("WPTDistance",route2.distance + route3.distance);
//	    	result.put("WPTTime",route2.time + route3.time);
	    	result.put("WPTDistance",route2.distance);
	    	result.put("WPTTime",route2.time);
	    	result.put("Status","Complete");
	    	result.put("Route",JSONDescription.getRouteDescription(route));

		} else {
			result.put("Status","Route generator error. No feasible route in any form of transportation can be found between your origin and destination.");
		}
		
		return result;
	}

}
