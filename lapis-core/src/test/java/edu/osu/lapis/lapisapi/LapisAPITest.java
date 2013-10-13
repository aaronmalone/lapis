package edu.osu.lapis.lapisapi;

import org.junit.Test;

import edu.osu.lapis.Lapis;

public class LapisAPITest implements LapisApiTestInterface {

	@Override
	@Test
	public void intializeLapisAPI() {
		// TODO Auto-generated method stub
		
		
		Lapis l = new Lapis();
		
		
		l.initialize("BJ", "http://localhost", "http://192.168.1.2:27001");
		
		

	}

}
