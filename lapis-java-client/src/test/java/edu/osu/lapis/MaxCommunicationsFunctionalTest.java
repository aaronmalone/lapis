package edu.osu.lapis;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.time.StopWatch;

public class MaxCommunicationsFunctionalTest {

	//TODO FACTOR SOME OF THIS STUFF OUT TO A SEPARATE CLASS
	private static final int COORDINATOR_PORT = 11222;
	private static final String COORDINATOR_URL = "http://localhost:" + COORDINATOR_PORT;
	private static final int NON_COORDINATOR_PORT = 8998;
	private static final String NON_COORDINATOR_URL = "http://localhost:" + NON_COORDINATOR_PORT;
	
	private static Properties getCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", "coord");
		p.setProperty("coordinator.url", COORDINATOR_URL);
		p.setProperty("localNodeAddress", COORDINATOR_URL);
		p.setProperty("port", Integer.toString(COORDINATOR_PORT));
		p.setProperty("isCoordinator", Boolean.toString(true));
		return p;
	}

	private static Properties getNonCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", "non-coor");
		p.setProperty("coordinator.url", COORDINATOR_URL);
		p.setProperty("localNodeAddress", NON_COORDINATOR_URL);
		p.setProperty("port", Integer.toString(NON_COORDINATOR_PORT));
		p.setProperty("isCoordinator", Boolean.toString(false));
		return p;
	}
	
	public static void main(String[] args) {
		StopWatch sw = new StopWatch();
		sw.start();
		LapisApi coordinator = new LapisApi(getCoordinatorProperties());
		LapisApi nonCoordinator = new LapisApi(getNonCoordinatorProperties());
		final double[] doub = new double[] { 0 };
		coordinator.publish("doub", doub);
		for(int i = 0; i < 30000; ++i) {
			System.out.println("doub: " + Arrays.toString(doub));
			double value = nonCoordinator.getArrayOfDouble("coord", "doub")[0];
			double[] newValue = new double[]{value+1};
			nonCoordinator.set("coord", "doub", newValue);
		}
		sw.stop();
		System.out.println("Test took " + sw.getTime() + " millis.");
		System.exit(0);
	}
}
