package edu.osu.lapis.example;

import static edu.osu.lapis.example.ExampleConstants.NODE_1;
import static edu.osu.lapis.example.ExampleConstants.NODE_1_URL;
import static edu.osu.lapis.example.ExampleConstants.NODE_2;

import java.util.Arrays;

import edu.osu.lapis.LapisApi;
import edu.osu.lapis.util.Sleep;

public class Node1Simulation {
	public static void main(String[] args) {
		
		System.out.println("Node 1 starting...");
		
		LapisApi lapisApi = new LapisApi(NODE_1, NODE_1_URL); //TODO BETTER CONSTRUCTOR
		
		//publish variables
		double[] countArray = new double[]{1,2,3,4,5}; //TODO MAYBE ADD BETTER WAY OF INSTANTIATING NEW ARRAYS
		lapisApi.publishReadOnly("countArray", countArray);
		
		double[] finishedCountingUpFlag = new double[]{ 0 }; 
		lapisApi.publishReadOnly("finishedCountingUpFlag", finishedCountingUpFlag);
		
		double[] node1HasFinishedSimulationFlag = new double[]{ 0 };
		lapisApi.publishReadOnly("node1HasFinishedSimulationFlag", node1HasFinishedSimulationFlag);
		
		double[] node2HasFinishedCountingFlag = new double[]{ 0 }; //TODO RENAME?
		lapisApi.publish("node2HasFinishedCountingFlag", node2HasFinishedCountingFlag);
		
		while(true) { //TODO MAYBE THERE'S A BETTER WAY OF DOING THIS?
			for (int i = 0; i < countArray.length; i++) {
				countArray[i] = countArray[i]+1;
			}
			printArray("countArray", countArray);
			Sleep.sleep(250);
			if(countArray[0] >= 10) {
				finishedCountingUpFlag[0] = 1; //TODO STATIC FOR SETTING FLAGS
				break;
			}
		}
		
		lapisApi.ready();
		
		System.out.println("Done with my counting.\nWaiting for Node 2 to finish...");
		
		while(node2HasFinishedCountingFlag[0] == 0) {
			System.out.println("Waiting for Node 2 to set 'node2HasFinishedCountingFlag' (a published variable of this node)...");
			Sleep.sleep(1500);
		}
		
		System.out.println("Getting Node 2's count array...");
		double[] node2CountArray = lapisApi.getArrayOfDouble(NODE_2, "countArray");
		printArray("Node 2's count array", node2CountArray); //TODO I DON'T THINK VALUE IS CORRECT!
		
		node1HasFinishedSimulationFlag[0] = 1; //TODO IMPROVE
		
		lapisApi.redact("finishFlag");
		lapisApi.redact("node2HasFinishedCountingFlag");
		
		System.out.println("Simulation finished!");
	}
	
	private static void printArray(String name, double[] array) {
		System.out.println(name + ": " + Arrays.toString(array));
	}
}
