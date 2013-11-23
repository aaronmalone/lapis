package edu.osu.lapis.sim;

import java.util.Arrays;
import java.util.Properties;

import edu.osu.lapis.JavaLapis;
import edu.osu.lapis.util.Sleep;

public class Sim {
	
	private static JavaLapis lapisApi = new JavaLapis(getProperties());

	public static void main(String[] args) {
		
		double[] node2copy = new double[5];
		lapisApi.publish("node2copy", node2copy);
		
		double[] ready = new double[1]; 
		lapisApi.publish("ready", ready);
	
		ready[0] = 1; //true
		
		String finishFlagVariableFullName = "finishFlag@Node1";
		double fin = lapisApi.getDouble(finishFlagVariableFullName);
		while(Math.abs(fin - 1) > 0.01) {
			System.out.println("Node1 has not yet finished increasing the values in its array.");
			Sleep.sleep(2500);
			fin = lapisApi.getDouble(finishFlagVariableFullName);
		}
		
		System.out.println("node2copy: " + Arrays.toString(node2copy));
		System.out.println("decrementing...");
		
		while(true) {
			System.out.println("'node2copy': " + Arrays.toString(node2copy));
			System.out.println("decreasing...");
			boolean allLessThanZero = true;
			for (int i = 0; i < node2copy.length; i++) {
				double val = node2copy[i]-1;
				node2copy[i] = val;
				if(val >= 0) allLessThanZero = false;
			}
			if(allLessThanZero)
				break;
			Sleep.sleep(999);
		}
		System.out.println("'node2copy': " + Arrays.toString(node2copy));
		
		System.out.println("Telling Node1 that I'm finished...");
		lapisApi.set("simFinishFlag@Node1", Double.valueOf(1));
		
		Sleep.sleep(99999999);
		System.out.println("Finished.");
		System.exit(0);
	}

	private static Properties getProperties() {
		Properties p = new Properties();
		p.setProperty("name", "Node2");
		p.setProperty("port", "8888");
		p.setProperty("isCoordinator", Boolean.FALSE.toString());
		p.setProperty("coordinator.url", "http://localhost:7777");
		return p;
	}
}
