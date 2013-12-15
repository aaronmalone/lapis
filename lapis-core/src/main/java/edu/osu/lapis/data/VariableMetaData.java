package edu.osu.lapis.data;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

@SuppressWarnings("serial")
public class VariableMetaData implements Serializable {
	
	private static final Map<Class<?>,String> classToTypeNameMap;
	
	static {
		String[] NUMBERS_TO_WORDS = new String[] {"zero", "one", "two", "three"};
		Map<Character, String> charToWordMap = Maps.newTreeMap();
		charToWordMap.put('I', "integer");
		charToWordMap.put('J', "long");
		charToWordMap.put('D', "double");
		charToWordMap.put('Z', "boolean");
		charToWordMap.put('B', "byte");
		classToTypeNameMap = Maps.newHashMap();
		for(int dim = 1; dim < 4; ++dim) {
			String brackets = StringUtils.repeat('[', dim);
			for(Map.Entry<Character, String> charToWord : charToWordMap.entrySet()) {
				Class<?> cls = forName(brackets + charToWord.getKey());
				String description = NUMBERS_TO_WORDS[dim] + " dimensional array of " + charToWord.getValue();
				classToTypeNameMap.put(cls,  description);
			}
		}
	}
	
	private static Class<?> forName(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private String name;
	private LapisPermission lapisPermission = LapisPermission.READ_WRITE;
	private String type;
	
	public LapisPermission getLapisPermission() {
		return lapisPermission;
	}
	public void setLapisPermission(LapisPermission lapisPermission) {
		this.lapisPermission = lapisPermission;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setType(Class<?> type) {
		this.setType(classToTypeNameMap.get(type));
	}
}