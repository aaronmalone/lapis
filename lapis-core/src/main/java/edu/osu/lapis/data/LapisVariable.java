package edu.osu.lapis.data;

import static edu.osu.lapis.data.LapisPermission.READ_ONLY;
import static edu.osu.lapis.data.LapisPermission.READ_WRITE;

import java.util.concurrent.Callable;

public class LapisVariable {
	
	private final String name;
	private final Callable<Object> callable;
	private final Settable settable;
	private final LapisPermission lapisPermission;
	
	public LapisVariable(String name, Callable<Object> callable) {
		this(name, READ_ONLY, callable, null);
	}
	
	public LapisVariable(
			String name,
			LapisPermission lapisPermission,
			Callable<Object> callable, 
			Settable settable) {
		this.name = name;
		this.lapisPermission = lapisPermission;
		this.callable = callable;
		this.settable = lapisPermission == READ_WRITE ? settable : null;
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

	public LapisPermission getLapisPermission() {
		return lapisPermission;
	}
}
