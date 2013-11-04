package edu.osu.lapis;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.Validate;


public class LapisFunctionalTest {
	
	private Lapis nonCoordinatorLapis;
	private Lapis coordinatorLapis;

	public static void main(String[] args) {		
		new LapisFunctionalTest().test();
	}
	
	public void test() {
		coordinatorLapis = new Lapis(getCoordinatorProperties());
		nonCoordinatorLapis = new Lapis(getNonCoordinatorProperties());
		testPublishVariables();
	}

	private void testPublishVariables() {
		testPublishOneDimArrays();
	}
	
	private void testPublishOneDimArrays() {
		testOneDimBooleanArray();
		testOneDimIntegerArray();
	}

	private void testOneDimBooleanArray() {
		boolean[] bools = new boolean[] {true, false, true};
		nonCoordinatorLapis.publish("bools", bools);
		boolean[] boolsRetrieved = coordinatorLapis.getArrayOfBoolean("bools@non-coor");
		Validate.isTrue(Arrays.equals(bools, boolsRetrieved));
		System.out.println("Tested one dimensional boolean arrays.");
	}
	
	private void testOneDimIntegerArray() {
		int[] ints = new int[] {8, 6, 7, -53, 0, 9};
		coordinatorLapis.publish("ints", ints);
		int[] retrieved = nonCoordinatorLapis.getArrayOfInt("ints@coord");
		Validate.isTrue(Arrays.equals(ints, retrieved));
		System.out.println("Tested one dimensional integer arrays.");
	}

	private Properties getNonCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", "non-coor");
		p.setProperty("coordinator.url", "http://localhost:12345");
		p.setProperty("port", "8888");
		p.setProperty("isCoordinator", Boolean.toString(false));
		return p;
	}

	private Properties getCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", "coord");
		p.setProperty("coordinator.url", "http://localhost:12345");
		p.setProperty("port", "12345");
		p.setProperty("isCoordinator", Boolean.toString(true));
		return p;
	}
}
