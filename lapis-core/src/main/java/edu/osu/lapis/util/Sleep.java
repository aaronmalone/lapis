package edu.osu.lapis.util;

import java.util.concurrent.TimeUnit;


public class Sleep {
	public static void sleep(long millis) {
		try {
			TimeUnit.MILLISECONDS.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
