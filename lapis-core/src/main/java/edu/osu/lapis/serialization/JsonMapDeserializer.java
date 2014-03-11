package edu.osu.lapis.serialization;

import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Deserialization of maps requires some special logic. It has been placed here 
 * to keep JsonSerialization clean.
 */
public class JsonMapDeserializer {
	
	private static final Object EMPTY_DOUBLE_ARRAY = new double[0];
	
	private static final Predicate<Object> doublePredicate = Predicates.instanceOf(Double.class);
	
	private static final Predicate<Object> doubleArrayPredicate = Predicates.instanceOf(double[].class);
	
	public static Map<String, Object> deserializeMap(JsonObject jsonObject) {
		Map<String, Object> map = Maps.newHashMap();
		for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement jsonElem = entry.getValue();
			map.put(key, deserializeJsonElement(jsonElem));
		}
		return map;
	}
	
	private static Object deserializeJsonElement(JsonElement jsonElement) {
		if(jsonElement.isJsonPrimitive()) {
			JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
			return deserializeJsonPrimitive(primitive);
		} else if(jsonElement.isJsonArray()) {
			return deserializeJsonArray(jsonElement.getAsJsonArray());
		} else if(jsonElement.isJsonNull()) {
			return null;
		} else {
			throw new IllegalArgumentException("Cannot deserialize JsonElement: " + jsonElement);
		}
	}
	
	private static Object deserializeJsonPrimitive(JsonPrimitive jsonPrimitive) {
		if(jsonPrimitive.isNumber()) {
			return jsonPrimitive.getAsDouble();
		} else if(jsonPrimitive.isString()) {
			return jsonPrimitive.getAsString();
		} else {
			throw new IllegalArgumentException("Cannot handle non-number primitive: " + jsonPrimitive);
		}
	}
	
	private static Object deserializeJsonArray(JsonArray jsonArray) {
		int size = jsonArray.size();
		if(size == 0) {
			return EMPTY_DOUBLE_ARRAY;
		} else {
			Object[] elements = deserializeJsonArrayElements(size, jsonArray);
			if(allDoubleArrays(elements)) {
				double[][] daa = new double[size][];
				for(int i = 0; i < size; ++i) {
					daa[i] = (double[])elements[i];
				}
				return daa;
			} else if(allDoubles(elements)) {
				return toDoubleArray(elements);
			} else {
				throw new IllegalArgumentException("Unable to handle JSON array: " + jsonArray);
			}
		}
	}

	private static Object[] deserializeJsonArrayElements(final int length, JsonArray jsonArray) {
		Object[] elements = new Object[length];
		for(int i = 0; i < length; ++i) {
			JsonElement elem = jsonArray.get(i);
			elements[i] = deserializeJsonElement(elem);
		}
		return elements;
	}
	
	private static double[] toDoubleArray(Object[] array) {
		double[] da = new double[array.length];
		for(int i = 0; i < array.length; ++i) {
			da[i] = (Double) array[i];
		}
		return da;
	}
	
	private static boolean allDoubleArrays(Object[] array) {
		return arrayForAll(array, doubleArrayPredicate);
	}
	
	private static boolean allDoubles(Object[] array) {
		return arrayForAll(array, doublePredicate);
	}
	
	private static boolean arrayForAll(Object[] array, Predicate<Object> predicate) {
		for(Object element : array) {
			if(!predicate.apply(element)) {
				return false;
			}
		} 
		return true;
	}
}
