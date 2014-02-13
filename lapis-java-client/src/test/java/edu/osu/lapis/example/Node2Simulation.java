package edu.osu.lapis.example;

import static edu.osu.lapis.example.ExampleConstants.NODE_1;
import static edu.osu.lapis.example.ExampleConstants.NODE_1_URL;
import static edu.osu.lapis.example.ExampleConstants.NODE_2;
import static edu.osu.lapis.example.ExampleConstants.NODE_2_URL;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

import edu.osu.lapis.LapisApi;
import edu.osu.lapis.util.Sleep;

public class Node2Simulation {
	public static void main(String[] args) throws TimeoutException {
		
		System.out.println("Node 2 starting...");
		
		LapisApi lapisApi = new LapisApi(NODE_2, NODE_1_URL, NODE_2_URL); //TODO BETTER CONSTRUCTOR
		
		//publish variables
		double[] countArray = new double[5];
		lapisApi.publishReadOnly("countArray", countArray);
		
		lapisApi.waitForReadyNode(NODE_1, 8000);
		
		while(true) {
			System.out.println("Waiting for Node 1 to set its 'finishedCountingUpFlag' variable...");
			double[] node1FinishFlag = lapisApi.getArrayOfDouble("Node1", "finishedCountingUpFlag");
			if(node1FinishFlag[0] == 1) {
				break;
			}
			Sleep.sleep(750);
		}
		
		double[] node1CountArray = lapisApi.getArrayOfDouble(NODE_1, "countArray");
		System.arraycopy(node1CountArray, 0, countArray, 0, countArray.length);
		
		while(countArray[0] > 0) {
			for(int i = 0; i < countArray.length; i++) {
				countArray[i] = countArray[i] - 1;
			}
			System.out.println("countArray"+ ": " + Arrays.toString(countArray));
			Sleep.sleep(200);
		}
		lapisApi.set(NODE_1, "node2HasFinishedCountingFlag", new double[]{1});
		System.out.println("Done with my counting.");
		
		while(true) {
			double[] fin = lapisApi.getArrayOfDouble(NODE_1, "node1HasFinishedSimulationFlag");
			if(fin[0] == 1) {
				break;
			} else {
				System.out.println("Waiting for Node 1 to set its 'node1HasFinishedSimulationFlag'...");
				Sleep.sleep(500);
			}
		}
		
		System.out.println("Done!");
	}
}
