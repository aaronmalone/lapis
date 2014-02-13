package edu.osu.lapis;

import org.apache.commons.lang3.Validate;

/**
 * Class that provides static helper methods to ease the use of length-one 
 * arrays of type double as flags within LAPIS.<br>
 * Recommended usage is to import methods from this class statically into your
 * own class files.
 */
public class Flags {
			
	/** The value of a 'true' flag. */
	public static final double[] FLAG_VALUE_TRUE = getFlagTrue();
	
	/** The value of a flag that is 'false'. */
	public static final double[] FLAG_VALUE_FALSE = getFlag();
	
	/**
	 * Returns a new double array, of length one, for use as a flag. The initial 
	 * value of the flag is false (the only array element is set to zero).
	 */
	public static double[] getFlag() {
		return new double[]{ 0 };
	}
	
	/**
	 * Returns new a double array, of length one, for use as a flag. The initial 
	 * value of the flag is true (the only array element is set to one).
	 */
	public static double[] getFlagTrue() {
		return new double[]{ 1 };
	}
	
	/**
	 * Returns new a double array, of length one, for use as a flag.
	 */
	public static double[] getFlag(boolean initialValue) {
		return initialValue ? getFlagTrue() : getFlag();
	}
	
	/**
	 * Sets a double array 'flag' to true.
	 */
	public static void setFlagTrue(double[] flag) {
		validateFlagLength1(flag);
		flag[0] = 1;
	}
	
	/**
	 * Sets a double array 'flag' to false. 
	 */
	public static void setFlagFalse(double[] flag) {
		validateFlagLength1(flag);
		flag[0] = 0;
	}

	/**
	 * Sets a double array 'flag' to the specified boolean value. 
	 */
	public static void setFlag(double[] flag, boolean flagValue) {
		validateFlagLength1(flag);
		if(flagValue) {
			setFlagTrue(flag);
		} else {
			setFlagFalse(flag);
		}
	}
	
	/**
	 * Tests whether a double array 'flag' is true.
	 */
	public static boolean evaluateFlagValue(double[] flag) {
		validateFlagLength1(flag);
		return Math.abs(flag[0]) < 0.0001;
	}
	
	private static void validateFlagLength1(double[] flag) {
		Validate.isTrue(flag.length == 1, "Array passed as flag has length other than 1: ", flag.length);
	}
}