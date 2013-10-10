package edu.osu.lapis.data;


public class GlobalDataUtility  { /*implements GlobalDataInterface {
	/*
	
	private final Map<VariableFullName, VariableMetaData> globalDataMap = 
			Collections.synchronizedMap(new HashMap<VariableFullName, VariableMetaData>());
	private NetworkTable networkTable; //TODO SET
	
	private Object getVariable(String fullName, LapisDataType type) {
		VariableFullName varName = new VariableFullName(fullName);
		VariableMetaData metaData = globalDataMap.get(varName);
		if(metaData == null || metaData.getLapisDataType() != type) {
			return checkNodeAndGetVariable(varName, type);
		} else {
			LapisNode lapisNode = networkTable.getNodesMap().get(varName.getModelName()); //TODO IMPROVE THIS
			/* TODO RENAME return queryNode(lapisNode, varName, type);
		}
	}

	private Object checkNodeAndGetVariable(VariableFullName varName, LapisDataType type) {
		String modelName = varName.getModelName();
		LapisNode lapisNode = networkTable.getNodesMap().get(modelName); //TODO IMPROVE THIS
		updateVariableMetaDataForNode(lapisNode, globalDataMap);
		VariableMetaData metaData = globalDataMap.get(varName);
		if(metaData == null) {
			throw new IllegalArgumentException("LAPIS node " + modelName 
					+ " does not have a published variable named " + varName.getLocalName());
		} else if(metaData.getLapisDataType() != type) {
			throw new IllegalArgumentException("Published variable " + varName 
					+ " is of type " +  metaData.getLapisDataType() + ", not "
					+ type);
		} else {
//			return queryNode(lapisNode, varName, type);
		}
	}
//
//	//TODO REMOVE
//	private Object queryNode(LapisNode lapisNode, VariableFullName varName, LapisDataType type) {
//		// TODO Auto-generated method stub
//		
//	}
//	
	private void updateVariableMetaDataForNode(LapisNode lapisNode, Map<VariableFullName, VariableMetaData> globalDataMap) {
		// TODO Auto-generated method stub
		
	}

	public int getInt(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte getByte(String fullName) {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getBoolean(String fullName) {
		// TODO Auto-generated method stub
		return false;
	}

	public int[] getArrayOfInt(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long[] getArrayOfLong(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[] getArrayOfDouble(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getArrayOfByte(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean[] getArrayOfBoolean(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[][] getTwoDimensionalArrayOfInt(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long[][] getTwoDimensionalArrayOfLong(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][] getTwoDimensionalArrayOfDouble(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[][] getTwoDimensionalArrayOfByte(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public int[][][] getThreeDimensionalArrayOfInt(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public long[][][] getThreeDimensionalArrayOfLong(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
}
