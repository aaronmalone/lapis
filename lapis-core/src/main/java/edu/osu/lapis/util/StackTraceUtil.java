package edu.osu.lapis.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class StackTraceUtil {
	public static String getStrackTraceAsString(Throwable throwable) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(outputStream);
		throwable.printStackTrace(printStream);
		String s = outputStream.toString();
		return s.replace("\r\n", "\n").trim();
	}	
}
