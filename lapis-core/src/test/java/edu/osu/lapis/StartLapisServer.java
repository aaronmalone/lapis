package edu.osu.lapis;

import edu.osu.lapis.Lapis;

public class StartLapisServer {

	public static void main(String[] args) {
		boolean[] booleanArray = new boolean[10];
		double[][] twoDimensionalDoubleArray = new double[7][5];
		Lapis lapis = new Lapis();
		lapis.publish("booleanArray", booleanArray);
		lapis.publish("twoDimDoubleArray", twoDimensionalDoubleArray);
	}
}
