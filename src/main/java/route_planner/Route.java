package route_planner;

import java.util.LinkedList;

public class Route {
	
	public String name ="";
	public double distance = 0;
	public double time = 0;
	public double cost = 0;
	
	public LinkedList<double[]> polyline = new LinkedList<double[]>();
	public LinkedList<String> instructions = new LinkedList<String>();
		
	public Route(String _name){
		name = _name;
	}
	
	public Route(Route route1, Route route2){
		
		name = route1.name + "->" + route2.name;
		time = route1.time + route2.time;
		distance = route1.distance + route2.distance;
		cost = route1.cost + route2.cost;
		
		polyline = new LinkedList<double[]>(route1.polyline);
		polyline.addAll(route2.polyline);
		
		instructions = new LinkedList<String>(route1.instructions);
		instructions.addAll(route2.instructions);
	}
	
	public Route(Route route1, Route route2, Route route3, Route route4){
		
		name = route1.name + "->" + route2.name + "->" + route3.name + "->" + route4.name;
		time = route1.time + route2.time + route3.time + route4.time;
		distance = route1.distance + route2.distance + route3.distance + route4.distance;
		cost = route1.cost + route2.cost + route3.cost + route4.cost;
		
		polyline = new LinkedList<double[]>(route1.polyline);
		polyline.addAll(route2.polyline);
		polyline.addAll(route3.polyline);
		polyline.addAll(route4.polyline);

		instructions = new LinkedList<String>(route1.instructions);
		instructions.addAll(route2.instructions);
		instructions.addAll(route3.instructions);
		instructions.addAll(route4.instructions);

	}
	
	public void setTime(double _time){
		time = _time;
	}
	
	public void setDistance(double _distance){
		distance = _distance;
	}
	
	public void setCost(double _cost){
		cost = _cost;
	}

	public void addPolylinePoint(double[] newPoint){
		
		polyline.addLast(newPoint);
	}
	
	public void addFirstPolylinePoint(double[] newPoint){
		
		polyline.addFirst(newPoint);
	}
	
	public void addInstruction(String newInstruction){
		
		instructions.addLast(newInstruction);
	}
	
	public void print(){
		
		System.out.println("Route name:\t" + name);
		System.out.println("Route distance:\t" + distance + " miles");
		System.out.println("Route time:\t" + time + " minutes");
		System.out.println("Route cost:\t$" + cost);		
		
		System.out.println("Polylines:\n" + polyline.size());
		System.out.println("Instructions:\n" + instructions.size());

	}

}
