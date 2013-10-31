package edu.osu.lapis.data;

public class LapisVariable {
	
	private boolean ready = true; //ready by default
	private Object reference;
	private VariableMetaData variableMetaData;

	public LapisVariable() {
		//default constructor
	}
	
	public LapisVariable(String name, Object reference) {
		LapisDataType type = LapisDataType.getTypeForObject(reference);
		if(type == null) {
			throw new IllegalArgumentException("Object of type " + reference.getClass() 
					+ " does not correspond to any LAPIS data type: " + reference);
		}
		setReference(reference);
		variableMetaData = new VariableMetaData();
		variableMetaData.setName(name);
		variableMetaData.setType(type);
		variableMetaData.setDimension(Dimensions.getDimensions(reference));
		variableMetaData.setLapisPermission(LapisPermission.READ_WRITE);
	}
	
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public Object getReference() {
		return reference;
	}
	public void setReference(Object reference) {
		this.reference = reference;
	}
	public LapisDataType getType() {
		return this.variableMetaData.getType();
	}
	public int[] getDimension() {
		return this.variableMetaData.getDimension();
	}
	public String getName() {
		return this.variableMetaData.getName();
	}
	public LapisPermission getLapisPermission() {
		return this.variableMetaData.getLapisPermission();
	}

	@Override
	public String toString() {
		//TODO IMPROVE, MAYBE?
		return "LapisVariable(" + variableMetaData.getName() + "," + variableMetaData.getType() + ")," + reference + ")";
	}
}
