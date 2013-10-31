package edu.osu.lapis.transmission;

import java.io.IOException;
import java.io.InputStream;

import org.restlet.Message;

import com.google.common.io.ByteStreams;

public class RenameMeUtil {

	public static byte[] messageEntityToBytes(Message message) {
		try (InputStream stream = message.getEntity().getStream()){
			return ByteStreams.toByteArray(stream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
