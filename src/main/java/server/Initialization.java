package server;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import route_planner.PathPlanner;

/**
 * Functions that are required for initializing the
 * path planner.
 * 
 * @author Peng Yu
 *
 */
public class Initialization implements ServletContextListener{

	static String hopperFolder = "/graphhopper_maps/";
	public static PathPlanner path_planner = null;
	
	public void contextInitialized(ServletContextEvent arg0) {
		loadMap();
	}
	
	public void loadMap(){
		path_planner = new PathPlanner(hopperFolder,hopperFolder+"map.osm");
	}
	
	public void contextDestroyed(ServletContextEvent arg0) {

		Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
				LOG.log(Level.INFO, String.format("deregistering jdbc driver: %s", driver));
			} catch (SQLException e) {
				LOG.log(Level.SEVERE, String.format("Error deregistering driver %s", driver), e);
			}

		}

	}
}
