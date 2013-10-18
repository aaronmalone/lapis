package edu.osu.lapis.data;

@SuppressWarnings("serial")
public class LocalVariableMetaData extends VariableMetaData {
	
	private boolean ready = true; //ready by default
	private Object reference;

	public LocalVariableMetaData() {
		//default constructor
	}
	
	public LocalVariableMetaData(Object reference) {
		super();
		LapisDataType type = LapisDataType.getTypeForObject(reference);
		if(type != null) {
			setType(type);
		} else {
			throw new IllegalArgumentException("Object of type " + reference.getClass() 
					+ " does not correspond to any LAPIS data type: " + reference);
		}
		setDimension(Dimensions.getDimensions(reference));
		setReference(reference);
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
}
