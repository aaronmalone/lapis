package edu.osu.lapis.data;

import java.util.concurrent.Callable;

public class LapisVariable {

	private final String name;
	private final Callable<?> callable;
	private final Settable settable;
	private final boolean readOnly;

	public LapisVariable(
			String name,
			boolean readOnly,
			Callable<?> callable,
			Settable settable) {
		this.name = name;
		this.readOnly = readOnly;
		this.callable = callable;
		this.settable = readOnly ? null : settable;
	}

	public Callable<?> getCallable() {
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
		assert !readOnly;
		this.settable.set(value);
	}

	public boolean isReadOnly() {
		return readOnly;
	}
}
