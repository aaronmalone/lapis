package edu.osu.lapis.data;

public interface GlobalDataInterface {
	
	public int getInt(String fullName) ;
	
	public long getLong(String fullName) ;
	
	public double getDouble(String fullName) ;
	
	public byte getByte(String fullName) ;
	
	public boolean getBoolean(String fullName) ;
	
	public int[] getArrayOfInt(String fullName) ;
	
	public long[] getArrayOfLong(String fullName) ;
	
	public double[] getArrayOfDouble(String fullName) ;
	
	public byte[] getArrayOfByte(String fullName) ;
	
	public boolean[] getArrayOfBoolean(String fullName) ;
	
	public int[][] getTwoDimensionalArrayOfInt(String fullName) ;
	
	public long[][] getTwoDimensionalArrayOfLong(String fullName) ;
	
	public double[][] getTwoDimensionalArrayOfDouble(String fullName) ;
	
	public byte[][] getTwoDimensionalArrayOfByte(String fullName) ;
	
	public boolean[][] getTwoDimensionalArrayOfBoolean(String fullName) ;
	
	public int[][][] getThreeDimensionalArrayOfInt(String fullName) ;
	
	public long[][][] getThreeDimensionalArrayOfLong(String fullName) ;
	
	public double[][][] getThreeDimensionalArrayOfDouble(String fullName) ;
	
	public byte[][][] getThreeDimensionalArrayOfByte(String fullName) ;
	
	public boolean[][][] getThreeDimensionalArrayOfBoolean(String fullName) ;
}
