package edu.osu.lapis;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonPropertiesParser {
	
	public static Properties parseJsonProperties(String fileName) {
		JsonObject jsonObject = getTopLevelJsonObject(fileName);
		return getPropertiesForJsonObject(jsonObject);
	}
	
	private static JsonObject getTopLevelJsonObject(String fileName) {
		Reader reader;
		try {
			reader = Files.newBufferedReader(new File(fileName).toPath(), Charset.defaultCharset());
		} catch (IOException e) {
			throw new RuntimeException("Error readig properties file: " + fileName, e);
		}
		JsonParser jsonParser = new JsonParser();
		JsonElement jsonElement = jsonParser.parse(reader);
		if(jsonElement.isJsonObject()) {
			return jsonElement.getAsJsonObject();
		} else {
			throw new IllegalArgumentException("JSON in " + fileName + " is not JSON object.");
		}
	}
	
	private static Properties getPropertiesForJsonObject(JsonObject jsonObject) {
		Properties properties = new Properties();
		for(Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			if(value.isJsonPrimitive()) {
				properties.setProperty(key, value.getAsString()); //TODO IS getAsString() THE RIGHT METHOD TO USE?
			} else if(value.isJsonObject()) {
				properties.putAll(getPropertiesWithPrefix(key, value.getAsJsonObject()));
			} else {
				throw new IllegalArgumentException("Can't handle JSON element: " + value.getClass());
			}
		}
		return properties;
	}

	private static Map<Object, Object> getPropertiesWithPrefix(String keyPrefix, JsonObject jsonObject) {
		String prefix = getPrefix(keyPrefix);
		Properties prefixProperties = new Properties();
		Properties objectProperties = getPropertiesForJsonObject(jsonObject);
		for(Object keyObj : objectProperties.keySet()) {
			String key = (String) keyObj;
			prefixProperties.put(prefix + key, objectProperties.get(keyObj));
		}
		return prefixProperties;
	}

	private static String getPrefix(String keyPrefix) {
		return keyPrefix.trim().isEmpty() ? "" : keyPrefix.trim() + ".";
	}
}
