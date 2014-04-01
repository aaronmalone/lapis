package edu.osu.lapis.example;

import static edu.osu.lapis.Flags.FLAG_VALUE_TRUE;
import static edu.osu.lapis.Flags.evaluateFlagValue;
import static edu.osu.lapis.example.ExampleConstants.NODE_1;
import static edu.osu.lapis.example.ExampleConstants.NODE_1_URL;
import static edu.osu.lapis.example.ExampleConstants.NODE_2;
import static edu.osu.lapis.example.ExampleConstants.NODE_2_URL;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.Validate;

import edu.osu.lapis.LapisApi;
import edu.osu.lapis.util.Sleep;

/**
 * An example of a multi-node LAPIS network using the LAPIS Java client.<br>
 * Start this after starting Node1Simulation.<br>
 * This class is based on node2simulation.m in LAPIS's MATLAB files.
 */
public class Node2Simulation {
	public static void main(String[] args) throws TimeoutException {
		
		System.out.println("Node 2 starting...");
		
		LapisApi lapisApi = new LapisApi(NODE_2, NODE_1_URL, NODE_2_URL);
		
		Validate.isTrue(lapisApi.doHeartbeatCheckReturnNodeIsLive(NODE_1));
		
		//publish variable
		double[] countArray = lapisApi.initializeAndPublishReadOnlyDoubleArray("countArray", new double[5]); 
		
		lapisApi.waitForReadyNode(NODE_1, 8000);

		//wait for Node 1 to finish its counting
		double[] node1FinishFlag;
		do {
			System.out.println("Waiting for Node 1 to set its 'finishedCountingUpFlag' variable...");
			node1FinishFlag = lapisApi.getArrayOfDouble("Node1", "finishedCountingUpFlag");
			Sleep.sleep(750);
		} while(evaluateFlagValue(node1FinishFlag));
		
		//retrieve countArray from Node 1
		double[] node1CountArray = lapisApi.getArrayOfDouble(NODE_1, "countArray");
		System.arraycopy(node1CountArray, 0, countArray, 0, countArray.length);
		
		while(countArray[0] > 0) {
			for(int i = 0; i < countArray.length; i++) {
				countArray[i] = countArray[i] - 1;
			}
			System.out.println("countArray"+ ": " + Arrays.toString(countArray));
			Sleep.sleep(200);
		}
		lapisApi.set(NODE_1, "node2HasFinishedCountingFlag", FLAG_VALUE_TRUE);
		System.out.println("Done with my counting.");
		
		while(true) {
			double[] fin = lapisApi.getArrayOfDouble(NODE_1, "node1HasFinishedSimulationFlag");
			if(evaluateFlagValue(fin)) {
				break;
			} else {
				System.out.println("Waiting for Node 1 to set its 'node1HasFinishedSimulationFlag'...");
				Sleep.sleep(750);
			}
		}
		
		System.out.println("Done!");
	}
}
