package edu.osu.lapis.example;

import edu.osu.lapis.LapisApi;
import edu.osu.lapis.util.Sleep;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;

import static edu.osu.lapis.Flags.evaluateFlagValue;
import static edu.osu.lapis.Flags.getFlag;
import static edu.osu.lapis.Flags.setFlagTrue;
import static edu.osu.lapis.example.ExampleConstants.*;

/**
 * An example of a multi-node LAPIS network using the LAPIS Java client.<br>
 * Start this before starting Node2Simulation.<br>
 * This class is based on node1simulation.m in LAPIS's MATLAB files.
 */
public class Node1Simulation {
	public static void main(String[] args) {
		
		System.out.println("Node 1 starting...");
		
		LapisApi lapisApi = new LapisApi(NODE_1, NODE_1_URL);
		Validate.isTrue(!lapisApi.doHeartbeatCheckReturnNodeIsLive(NODE_2));
		
		//publish variables
		double[] countArray = lapisApi.initializeAndPublishReadOnlyDoubleArray("countArray", 1,2,3,4,5);
		
		double[] finishedCountingUpFlag = getFlag(); 
		lapisApi.publishReadOnly("finishedCountingUpFlag", finishedCountingUpFlag);
		
		double[] node1HasFinishedSimulationFlag = getFlag();
		lapisApi.publishReadOnly("node1HasFinishedSimulationFlag", node1HasFinishedSimulationFlag);
		
		double[] node2HasFinishedCountingFlag = getFlag(); //set by Node 2
		lapisApi.publish("node2HasFinishedCountingFlag", node2HasFinishedCountingFlag);
		
		while(countArray[0] < 10) {
			for (int i = 0; i < countArray.length; i++) {
				countArray[i] = countArray[i]+1;
			}
			printArray("countArray", countArray);
			Sleep.sleep(250);
		}
		setFlagTrue(finishedCountingUpFlag);
		
		lapisApi.ready();
		
		System.out.println("Done with my counting.\nWaiting for Node 2 to finish...");
		
		while(evaluateFlagValue(node2HasFinishedCountingFlag)) {
			System.out.println("Waiting for Node 2 to set 'node2HasFinishedCountingFlag' (a published variable of this node)...");
			Sleep.sleep(1500);
		}
		
		System.out.println("Getting Node 2's count array...");
		double[] node2CountArray = lapisApi.getArrayOfDouble(NODE_2, "countArray");
		printArray("Node 2's count array", node2CountArray);
		
		setFlagTrue(node1HasFinishedSimulationFlag);
		
		lapisApi.redact("finishFlag");
		lapisApi.redact("node2HasFinishedCountingFlag");
		
		Validate.isTrue(lapisApi.doHeartbeatCheckReturnNodeIsLive(NODE_2));
		
		System.out.println("Simulation finished!");
	}
	
	private static void printArray(String name, double[] array) {
		System.out.println(name + ": " + Arrays.toString(array));
	}
}
