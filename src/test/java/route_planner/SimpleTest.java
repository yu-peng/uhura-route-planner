package route_planner;

/**
 * Unit test for simple App.
 */
public class SimpleTest
{
    /**
     * Test the load and save of the map.
     *
     * @param testName name of the test case
     */
    public static void main(String[] args)
    
    {
    	System.out.println("Loading start");
    	long start_time = System.currentTimeMillis();
    	
//        PathPlanner michigan_planner = new PathPlanner("D:/Dropbox/Code/Applications/route-planner/michigan_maps","D:/Downloads/michigan-latest.osm");
//        PathPlanner california_planner = new PathPlanner("D:/Dropbox/Code/Applications/route-planner/california_maps","D:/Downloads/california-latest.osm");
        PathPlanner california_planner = new PathPlanner("G:/Map/north_america_maps","G:/Map/north-america-latest.osm");

        System.out.println("Loading complete: " + (System.currentTimeMillis() - start_time));
        
//        Route route = michigan_planner.findPolylineCar(42.300219, -83.714756, 42.226784, -83.346686);
        Route route = california_planner.findPolylineCar(37.379398, -121.997164, 37.369313, -121.928724, false, false);
         
        System.out.println("Routing complete: " + (System.currentTimeMillis() - start_time));

        route.print();

    }

}
