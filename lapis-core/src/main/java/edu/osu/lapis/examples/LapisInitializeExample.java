package edu.osu.lapis.examples;

import edu.osu.lapis.Lapis;

public class LapisInitializeExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {


		Lapis l = new Lapis();		
		l.initialize("node1", "http://localhost", "http://192.168.1.2:27001");


	}

}
