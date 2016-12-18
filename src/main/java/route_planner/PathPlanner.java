package route_planner;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.Instruction;
import com.graphhopper.util.InstructionList;
import com.graphhopper.util.PointList;

/**
 * Given the origin and destination specified in lat/log,
 * find the shortest path between them using the given mode.
 * 
 * @author Peng Yu
 *
 */
public class PathPlanner {
	
	public static GraphHopper graphHopperRouter = null;
	
	public PathPlanner(String graphFolder, String osmFile){
		loadOpenStreetMaps(graphFolder, osmFile);
	}

	public void loadOpenStreetMaps(String graphFolder, String osmFile){
		
		// create singleton
		graphHopperRouter = new GraphHopper().forServer();
		graphHopperRouter.setInMemory();
		graphHopperRouter.setOSMFile(osmFile);
		
		// where to store graphhopper files?
		graphHopperRouter.setGraphHopperLocation(graphFolder);
		graphHopperRouter.setEncodingManager(new EncodingManager("CAR,BIKE,FOOT"));

		// now this can take minutes if it imports or a few seconds for loading
		// of course this is dependent on the area you import
		graphHopperRouter.importOrLoad();
	}
	
	public Route getRoute(double lat1, double lon1, double lat2, double lon2, boolean returnPolyline, boolean returnInstruction, String mode){
		
		Route newRoute = null;
		
		if (!mode.equalsIgnoreCase("driving") 
				&& !mode.equalsIgnoreCase("biking")
				&& !mode.equalsIgnoreCase("walking")){
			System.out.println("Unknown mode " + mode);
			return null;
		}
		
		if (mode.equalsIgnoreCase("driving")){
			
			newRoute = findPolylineCar(lat1,lon1,lat2,lon2,returnPolyline,returnInstruction);
	        
		} else if (mode.equalsIgnoreCase("biking")){
			
			newRoute = findPolylineBike(lat1,lon1,lat2,lon2,returnPolyline,returnInstruction);

		} else if (mode.equalsIgnoreCase("walking")){

			newRoute = findPolylineFoot(lat1,lon1,lat2,lon2,returnPolyline,returnInstruction);

		}		
		
		if (newRoute == null){
			
			// we do not know about the from or to location.
			// use greater circle distance for it.
			
			double distance = distBetween(lat1, lon1, lat2, lon2);
			newRoute = new Route(mode + ": Graphhopper: ["+lat1+","+lon1+"]--["+lat2+","+lon2+"]");
        	newRoute.setDistance(distance); // return distance in miles
        	newRoute.setCost(0); // return cost in dollars

    		if (mode.equalsIgnoreCase("driving")){
    			// 40mph
            	newRoute.setTime(round(distance/40*60.0,1)); // return time in minutes
    	        
    		} else if (mode.equalsIgnoreCase("biking")){
    			// 10mph
            	newRoute.setTime(round(distance/10*60.0,1)); // return time in minutes

    		} else if (mode.equalsIgnoreCase("walking")){
    			// 3mph
            	newRoute.setTime(round(distance/3*60,1)); // return time in minutes

    		}
        	
        	if (returnPolyline){
            	
                newRoute.addFirstPolylinePoint(new double[]{lat1,lon1});
                newRoute.addPolylinePoint(new double[]{lat2,lon2});
        	}
		}
		
		
		return newRoute;
	}
	
	public static double distBetween(double lat1, double lng1, double lat2, double lng2) {
		
	    double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	            * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;

	    return dist;
	}
	
	public Route findPolylineCar(double lat1, double lon1, double lat2, double lon2, boolean returnPolyline, boolean returnInstruction){
		
		Route newRoute = new Route("driving: Graphhopper: ["+lat1+","+lon1+"]--["+lat2+","+lon2+"]");
        GHResponse ph = graphHopperRouter.route(new GHRequest(lat1, lon1, lat2, lon2).setVehicle(EncodingManager.CAR));

        return extractRoute(ph, newRoute, lat1, lon1, lat2, lon2,returnPolyline,returnInstruction);        
	}
	
	public Route findPolylineBike(double lat1, double lon1, double lat2, double lon2, boolean returnPolyline, boolean returnInstruction){
		
		Route newRoute = new Route("bicycling: Graphhopper: ["+lat1+","+lon1+"]--["+lat2+","+lon2+"]");
        GHResponse ph = graphHopperRouter.route(new GHRequest(lat1, lon1, lat2, lon2).setVehicle(EncodingManager.BIKE));
    
        return extractRoute(ph, newRoute, lat1, lon1, lat2, lon2,returnPolyline,returnInstruction);
	}

	
	public Route findPolylineFoot(double lat1, double lon1, double lat2, double lon2, boolean returnPolyline, boolean returnInstruction){
				
		Route newRoute = new Route("walking: Graphhopper: ["+lat1+","+lon1+"]--["+lat2+","+lon2+"]");
        GHResponse ph = graphHopperRouter.route(new GHRequest(lat1, lon1, lat2, lon2).setVehicle(EncodingManager.FOOT));

        return extractRoute(ph, newRoute, lat1, lon1, lat2, lon2,returnPolyline,returnInstruction);
    }
	

    public Route extractRoute(GHResponse ph, Route newRoute, double originLat, double originLon, double destLat, double destLon,
    		boolean returnPolyline, boolean returnInstruction){
    	
        if (!ph.hasErrors()){
        	        	
        	newRoute.setTime(round(ph.getMillis()/60000.0,1)); // return time in minutes
        	newRoute.setDistance(round(ph.getDistance()/1609.3,1)); // return distance in miles
        	newRoute.setCost(0); // return cost in dollars

        	if (returnPolyline){
            	PointList polylinePoints = ph.getPoints();
            	
                for (int idx=0; idx < polylinePoints.size(); idx++){
                    newRoute.addPolylinePoint(new double[]{polylinePoints.getLatitude(idx),polylinePoints.getLongitude(idx)});
                }
                
                // add the origin and destination
                newRoute.addFirstPolylinePoint(new double[]{originLat,originLon});
                newRoute.addPolylinePoint(new double[]{destLat,destLon});
        	}
            
            if (returnInstruction){
                InstructionList instructions = ph.getInstructions();
                
                for (int idx=0; idx < instructions.size(); idx++){
                	newRoute.addInstruction(externalizeInstruction(instructions.get(idx)));
                }   
            }
            
        	return newRoute;
        	
        } else {
        	
        	return null;
        }
		
	}
	

	public String externalizeInstruction(Instruction instruction){
		
		StringBuilder instructionString = new StringBuilder();
		
		/*
	    TURN_SHARP_LEFT = -3;
	    public static final int TURN_LEFT = -2;
	    public static final int TURN_SLIGHT_LEFT = -1;
	    public static final int CONTINUE_ON_STREET = 0;
	    public static final int TURN_SLIGHT_RIGHT = 1;
	    public static final int TURN_RIGHT = 2;
	    public static final int TURN_SHARP_RIGHT = 3;
	    public static final int FINISH = 4;
		*/
		
		switch(instruction.getSign()){
			case Instruction.TURN_SHARP_LEFT: instructionString.append("Sharp left turn to "+instruction.getName()); break;
			case Instruction.TURN_LEFT: instructionString.append("Turn left to "+instruction.getName()); break;
			case Instruction.TURN_SLIGHT_LEFT: instructionString.append("Slightly turn left to "+instruction.getName()); break;
			case Instruction.CONTINUE_ON_STREET: instructionString.append("Continue on "+instruction.getName()); break;
			case Instruction.TURN_SLIGHT_RIGHT: instructionString.append("Slightly turn right to "+instruction.getName()); break;
			case Instruction.TURN_RIGHT: instructionString.append("Turn right to "+instruction.getName()); break;
			case Instruction.TURN_SHARP_RIGHT: instructionString.append("Sharp right turn to "+instruction.getName()); break;
			case 4: return "";
		}

		instructionString.append(";"+instruction.getDistance()/1609.3+" miles");
		instructionString.append(";"+instruction.getTime()/60000.0+" minutes");
		
		PointList points = instruction.getPoints();
		
		instructionString.append(";"+getFirstLat(points));
		instructionString.append(";"+getFirstLon(points));
		
		instructionString.append(";"+getLastLat(points));
		instructionString.append(";"+getLastLon(points));

		return instructionString.toString();
	}
	
	private double round (double value, int precision) {
	    int scale = (int) Math.pow(10, precision);
	    return (double) Math.round(value * scale) / scale;
	}
	
    /**
     * Latitude of the location where this instruction should take place.
     */
    public double getFirstLat(PointList points)
    {
        return points.getLatitude(0);
    }

    /**
     * Longitude of the location where this instruction should take place.
     */
    public double getFirstLon(PointList points)
    {
        return points.getLongitude(0);
    }

    public double getLastLat(PointList points)
    {
        return points.getLatitude(points.size() - 1);
    }

    public double getLastLon(PointList points)
    {
        return points.getLongitude(points.size() - 1);
    }
	
}
