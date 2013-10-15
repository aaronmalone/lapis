package edu.osu.lapis.data;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public enum LapisDataType {
	INTEGER,
	LONG,
	DOUBLE,
	BYTE,
	BOOLEAN,
	
	ONE_DIMENSIONAL_ARRAY_OF_INTEGER,
	ONE_DIMENSIONAL_ARRAY_OF_LONG,
	ONE_DIMENSIONAL_ARRAY_OF_DOUBLE,
	ONE_DIMENSIONAL_ARRAY_OF_BYTE,
	ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN,
	
	TWO_DIMENSIONAL_ARRAY_OF_INTEGER,
	TWO_DIMENSIONAL_ARRAY_OF_LONG,
	TWO_DIMENSIONAL_ARRAY_OF_DOUBLE,
	TWO_DIMENSIONAL_ARRAY_OF_BYTE,
	TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN,
	
	THREE_DIMENSIONAL_ARRAY_OF_INTEGER,
	THREE_DIMENSIONAL_ARRAY_OF_LONG,
	THREE_DIMENSIONAL_ARRAY_OF_DOUBLE,
	THREE_DIMENSIONAL_ARRAY_OF_BYTE,
	THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN;
	
	private static Set<LapisDataType> ARRAY_TYPES = Collections.unmodifiableSet(EnumSet.of(
			ONE_DIMENSIONAL_ARRAY_OF_INTEGER,
			ONE_DIMENSIONAL_ARRAY_OF_LONG,
			ONE_DIMENSIONAL_ARRAY_OF_DOUBLE,
			ONE_DIMENSIONAL_ARRAY_OF_BYTE,
			ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN,
			
			TWO_DIMENSIONAL_ARRAY_OF_INTEGER,
			TWO_DIMENSIONAL_ARRAY_OF_LONG,
			TWO_DIMENSIONAL_ARRAY_OF_DOUBLE,
			TWO_DIMENSIONAL_ARRAY_OF_BYTE,
			TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN,
			
			THREE_DIMENSIONAL_ARRAY_OF_INTEGER,
			THREE_DIMENSIONAL_ARRAY_OF_LONG,
			THREE_DIMENSIONAL_ARRAY_OF_DOUBLE,
			THREE_DIMENSIONAL_ARRAY_OF_BYTE,
			THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN));
	
	private final static Map<LapisDataType, Class<?>> typeToClassMap;
	private final static Map<Class<?>, LapisDataType> classToTypeMap;
	
	static {
		EnumMap<LapisDataType, Class<?>> typeMap = new EnumMap<>(LapisDataType.class);
		typeMap.put(INTEGER, Integer.TYPE);
		typeMap.put(LONG, Long.TYPE);
		typeMap.put(DOUBLE, Double.TYPE);
		typeMap.put(BYTE, Byte.TYPE);
		typeMap.put(BOOLEAN, Boolean.TYPE);

		typeMap.put(ONE_DIMENSIONAL_ARRAY_OF_INTEGER, int[].class);
		typeMap.put(ONE_DIMENSIONAL_ARRAY_OF_LONG, long[].class);
		typeMap.put(ONE_DIMENSIONAL_ARRAY_OF_DOUBLE, double[].class);
		typeMap.put(ONE_DIMENSIONAL_ARRAY_OF_BYTE, byte[].class);
		typeMap.put(ONE_DIMENSIONAL_ARRAY_OF_BOOLEAN, boolean[].class);

		typeMap.put(TWO_DIMENSIONAL_ARRAY_OF_INTEGER, int[][].class);
		typeMap.put(TWO_DIMENSIONAL_ARRAY_OF_LONG, long[][].class);
		typeMap.put(TWO_DIMENSIONAL_ARRAY_OF_DOUBLE, double[][].class);
		typeMap.put(TWO_DIMENSIONAL_ARRAY_OF_BYTE, byte[][].class);
		typeMap.put(TWO_DIMENSIONAL_ARRAY_OF_BOOLEAN, boolean[][].class);

		typeMap.put(THREE_DIMENSIONAL_ARRAY_OF_INTEGER, int[][][].class);
		typeMap.put(THREE_DIMENSIONAL_ARRAY_OF_LONG, long[][][].class);
		typeMap.put(THREE_DIMENSIONAL_ARRAY_OF_DOUBLE, double[][][].class);
		typeMap.put(THREE_DIMENSIONAL_ARRAY_OF_BYTE, byte[][][].class);
		typeMap.put(THREE_DIMENSIONAL_ARRAY_OF_BOOLEAN, boolean[][][].class);
		
		typeToClassMap = Collections.unmodifiableMap(typeMap);
		Map<Class<?>, LapisDataType> classMap = new HashMap<>(); //the beautiful diamond operator!
		for(Entry<LapisDataType, Class<?>> entry : typeMap.entrySet()) {
			classMap.put(entry.getValue(), entry.getKey());
		}
		classMap.put(Integer.class, INTEGER);
		classMap.put(Long.class, LONG);
		classMap.put(Double.class, DOUBLE);
		classMap.put(Boolean.class, BOOLEAN);
		classMap.put(Byte.class, BYTE);
		classToTypeMap = Collections.unmodifiableMap(classMap);
	}
	
	public static LapisDataType getTypeForObject(Object obj) {
		return classToTypeMap.get(obj.getClass());
	}
	
	public static Class<?> getClassForType(LapisDataType type) {
		return typeToClassMap.get(type);
	}
	
	public static Set<LapisDataType> getArrayTypes() {
		return ARRAY_TYPES;
	}
	
	public boolean isArrayType() {
		return ARRAY_TYPES.contains(this);
	}
}