package edu.osu.lapis;


public class LapisFunctionalTest {
	
	private Lapis nonCoordinatorLapis;
	private Lapis coordinatorLapis;

	public static void main(String[] args) {
		boolean[] booleanArray = new boolean[10];
		double[][] twoDimensionalDoubleArray = new double[7][5];
		Lapis lapis = new Lapis();
		lapis.publish("booleanArray", booleanArray);
		lapis.publish("twoDimDoubleArray", twoDimensionalDoubleArray);
	}
}
