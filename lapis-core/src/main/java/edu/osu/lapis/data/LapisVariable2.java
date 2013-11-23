package edu.osu.lapis.data;

import java.util.concurrent.Callable;

public class LapisVariable2 {
	
	private final String name;
	private final LapisDataType lapisDataType;
	private final Callable<Object> callable;
	private final Settable settable;
	private final int[] dimensions;
	
	public LapisVariable2(
			String name,
			LapisDataType lapisDataType,
			int[] dimensions,
			Callable<Object> callable, 
			Settable settable) {
		this.name = name;
		this.lapisDataType = lapisDataType;
		this.dimensions = dimensions;
		this.callable = callable;
		this.settable = settable;
	}

	public Callable<Object> getCallable() {
		return callable;
	}

	public Settable getSettable() {
		return settable;
	}
	
	public String getName() {
		return name;
	}

	public LapisDataType getLapisDataType() {
		return lapisDataType;
	}

	public int[] getDimensions() {
		return dimensions;
	}

	public Object getValue() {
		try {
			return this.callable.call();
		} catch (Exception e) {
			throw new RuntimeException("Error getting value.", e);
		}
	}
	
	public void setValue(Object value) {
		this.settable.set(value);
	}
}
