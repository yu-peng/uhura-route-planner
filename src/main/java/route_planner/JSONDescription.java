package route_planner;

import java.util.LinkedList;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONDescription {
	
	public static JSONObject getRouteDescription(Route route) throws JSONException{
		
	    JSONObject description = new JSONObject();

		description.put("name", route.name);
		description.put("duration", route.time);
		description.put("distance", route.distance);
		description.put("cost", route.cost);		
						    
//		addPathDescription(description,route.instructions);	
		addPolyline(description,route.polyline);
				
		return description;
	}
	
	public static void addPathDescription(JSONObject description, LinkedList<String> instructions) throws JSONException{
		
		if (!instructions.isEmpty()){
				
		    JSONArray jsonInstructions = new JSONArray();
	    	
	    	while (!instructions.isEmpty()){
	    		String instruction = instructions.poll();
		    	String[] elements = instruction.split(";");
		    	
	    		if (elements.length == 7){
	    			
				    JSONObject jsonInstruction = new JSONObject();
				    jsonInstruction.put("instruction", elements[0]);
				    jsonInstruction.put("distance", elements[1]);
				    jsonInstruction.put("time", elements[2]);
				    jsonInstruction.put("firstLat", elements[3]);
				    jsonInstruction.put("firstLon", elements[4]);
				    jsonInstruction.put("lastLat", elements[5]);
				    jsonInstruction.put("lastLon", elements[6]);

				    jsonInstructions.put(jsonInstruction);
				    
	    		} else if (elements.length == 3){
	    			
				    JSONObject jsonInstruction = new JSONObject();
				    jsonInstruction.put("instruction", elements[0]);
				    jsonInstruction.put("distance", elements[1]);
				    jsonInstruction.put("time", elements[2]);

				    jsonInstructions.put(jsonInstruction);
				    
	    		} else if (elements.length == 1){
	    			
				    JSONObject jsonInstruction = new JSONObject();
				    jsonInstruction.put("instruction", elements[0]);
				    jsonInstructions.put(jsonInstruction);

	    		}
	    		
	    	}	
	    	
		    description.put("instructions", jsonInstructions);
		}		
	}
	
	public static void addPolyline(JSONObject description, LinkedList<double[]> polyline) throws JSONException{
		
		StringBuilder sb = new StringBuilder();
		
		while (!polyline.isEmpty()){
			double[] point = polyline.poll();
			if (!polyline.isEmpty()){
				sb.append(point[0] + "," + point[1] + ",");
			} else {
				sb.append(point[0] + "," + point[1]);
			}
		}
		
		description.put("polyline", sb.toString());
	}
}
