package edu.osu.lapis.serialize;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteStreams;

public class LapisUtils {

	private LapisUtils() {
		//do not instantiate
	}
	
	public static byte[] toByteArray(InputStream inputStream) {
		try {
			return ByteStreams.toByteArray(inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	public static String toString(Readable readable) {
		try {
			return CharStreams.toString(readable);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	*/
}
