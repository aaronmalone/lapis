package edu.osu.lapis;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;

import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import edu.osu.lapis.util.LapisRandoms;


public class LapisFunctionalTest {
	
	private static final int COORDINATOR_PORT = 11122;
	private static final String COORDINATOR_URL = "http://localhost:" + COORDINATOR_PORT;
	private static final int NON_COORDINATOR_PORT = 8899;
	private static final String NON_COORDINATOR_URL = "http://localhost:" + NON_COORDINATOR_PORT;
	
	private LapisApi nonCoordinatorLapis;
	private LapisApi coordinatorLapis;

	public static void main(String[] args) {
		try {
			new LapisFunctionalTest().test();
			System.out.println("Finished functional test successfully.");
			System.exit(0);
		} catch(Throwable t) {
			t.printStackTrace();
			System.exit(-1);
		}
	}
	
	public void test() {
		coordinatorLapis = new LapisApi(getCoordinatorProperties());
		nonCoordinatorLapis = new LapisApi(getNonCoordinatorProperties());
		StopWatch sw = new StopWatch();
		sw.start();
		testInternal();
		sw.stop();
		System.out.println("Actual post-initialization test took " + sw.getTime() + " millis.");
	}
	
	private void testInternal() {
		testReady();
		testNotReady();
		testDimensionMismatch();
		testReadOnly();
		testPublishGetAndSetVariables();
	}
	
	private void testReadOnly() {
		double[] readOnlyDoubles = new double[]{1.0, 2.0, 3.0};
		nonCoordinatorLapis.publishReadOnly("readOnlyDoubles", readOnlyDoubles);
		//set from the same node
		for(int i = 0; i < readOnlyDoubles.length; i++) {
			readOnlyDoubles[i] = 2 * readOnlyDoubles[i];
		}
		Validate.isTrue(Arrays.equals(new double[]{2.0, 4.0, 6.0}, readOnlyDoubles));
		
		//test set from another node -- this should fail
		try {
			coordinatorLapis.set("non-coor", "readOnlyDoubles", new double[]{1.0, 4.0, 9.0});
			shouldNotHaveReachedThisPoint();
		} catch(LapisClientException e) {
			//this is expected
			Validate.isTrue(e.getMessage().contains("read-only"));
		}
	}

	private void testReady() {
		coordinatorLapis.notReady();
		waitWithExpectedFailure(nonCoordinatorLapis, coordinatorLapis.getName());
		coordinatorLapis.ready();
		nonCoordinatorLapis.waitForReadyNode("coord");
		System.out.println("Tested ready() and waitForReadyNode().");
	}
	
	private void testNotReady() {
		nonCoordinatorLapis.ready();
		coordinatorLapis.waitForReadyNode("non-coor");
		nonCoordinatorLapis.notReady();
		waitWithExpectedFailure(coordinatorLapis, nonCoordinatorLapis.getName());
		nonCoordinatorLapis.ready();
		coordinatorLapis.waitForReadyNode("non-coor");
		System.out.println("Tested notReady().");
	}
	
	private void waitWithExpectedFailure(LapisApi waiting, String nodeName) {
		final long timeoutMillis = 100;
		LapisCore.waitingForNodeRetryTime = 5;
		long startTime = System.currentTimeMillis();
		try {
			waiting.waitForReadyNode(nodeName, timeoutMillis);
			shouldNotHaveReachedThisPoint();
		} catch (TimeoutException e) {
			//this was expected... check that timeoutMillis have passed
			Validate.isTrue(System.currentTimeMillis() >= timeoutMillis + startTime);
		}
	}

	private void testPublishGetAndSetVariables() {
		testOneDimArrays();
		testTwoDimArrays();
		testThreeDimArrays();
	}
	
	private void testOneDimArrays() {
		testOneDimByteArray();
		testOneDimBooleanArray();
		testOneDimIntegerArray();
		testOneDimLongArray();
		testOneDimDoubleArray();
	}
	
	private void testTwoDimArrays() {
		testTwoDimByteArray();
		testTwoDimBooleanArray();
		testTwoDimLongArray();
	}

	private void testThreeDimArrays() {
		testThreeDimIntegerArray();
		testThreeDimDoubleArray();
	}

	private void testOneDimByteArray() {
		byte[] bytes = LapisRandoms.getOneDimensionalArrayOfByte();
		nonCoordinatorLapis.publish("bytes", bytes);
		byte[] retrieved = coordinatorLapis.getArrayOfByte("non-coor", "bytes");
		Validate.isTrue(Arrays.equals(bytes, retrieved));
		byte[] different = LapisRandoms.getRandomArrayOfSameDimensions(bytes);
		Validate.isTrue(!Arrays.equals(different, bytes));
		coordinatorLapis.set("non-coor", "bytes", different);
		Validate.isTrue(Arrays.equals(different, bytes));
		System.out.println("Tested one dimensional byte array.");
	}
	
	private void testOneDimBooleanArray() {
		boolean[] bools = new boolean[] {true, false, true, true, true, true, true, false, true, true};
		nonCoordinatorLapis.publish("bools", bools);
		boolean[] boolsRetrieved = coordinatorLapis.getArrayOfBoolean("non-coor", "bools");
		Validate.isTrue(Arrays.equals(bools, boolsRetrieved));
		boolean[] different = LapisRandoms.getRandomArrayOfSameDimensions(bools);
		Validate.isTrue(!Arrays.equals(different, bools));
		coordinatorLapis.set("non-coor", "bools", different);
		Validate.isTrue(Arrays.equals(different, bools));
		System.out.println("Tested one dimensional boolean array.");
	}
	
	private void testOneDimIntegerArray() {
		int[] ints = new int[] {8, 6, 7, -53, 0, 9};
		coordinatorLapis.publish("ints", ints);
		int[] retrieved = nonCoordinatorLapis.getArrayOfInt("coord", "ints");
		Validate.isTrue(Arrays.equals(ints, retrieved));
		int[] different = Arrays.copyOf(ints, ints.length);
		Arrays.sort(different);
		Validate.isTrue(!Arrays.equals(different, ints));
		nonCoordinatorLapis.set("coord", "ints", different);
		Validate.isTrue(Arrays.equals(different, ints));
		System.out.println("Tested one dimensional integer array.");
	}

	private void testOneDimLongArray() {
		long[] longs = LapisRandoms.getOneDimensionalArrayOfLong();
		coordinatorLapis.publish("longs", longs);
		long[] retrieved = nonCoordinatorLapis.getArrayOfLong("coord", "longs");
		Validate.isTrue(Arrays.equals(longs, retrieved));
		long[] different = LapisRandoms.getRandomArrayOfSameDimensions(longs);
		Validate.isTrue(!Arrays.equals(different, longs));
		nonCoordinatorLapis.set("coord", "longs", different);
		Validate.isTrue(Arrays.equals(different, longs));
	}

	private void testOneDimDoubleArray() {
		double[] doubles = LapisRandoms.getOneDimensionalArrayOfDouble();
		nonCoordinatorLapis.publish("doubles", doubles);
		double[] retrieved = coordinatorLapis.getArrayOfDouble("non-coor", "doubles");
		Validate.isTrue(Arrays.equals(doubles, retrieved));
		double[] different = LapisRandoms.getRandomArrayOfSameDimensions(doubles);
		Validate.isTrue(!Arrays.equals(different, doubles));
		coordinatorLapis.set("non-coor", "doubles", different);
		Validate.isTrue(Arrays.equals(different, doubles));
		System.out.println("Tested one dimensional double array.");
	}

	private void testTwoDimByteArray() {
		byte[][] twoDimBytes = LapisRandoms.getRandomArrayOfSameDimensions(new byte[4][5]);
		coordinatorLapis.publish("twoDimBytes", twoDimBytes);
		byte[][] retrieved = nonCoordinatorLapis.getTwoDimensionalArrayOfByte("coord", "twoDimBytes");
		Validate.isTrue(Arrays.deepEquals(twoDimBytes, retrieved)); //look into how this is implemented
		byte[][] different = LapisRandoms.getRandomArrayOfSameDimensions(twoDimBytes);
		Validate.isTrue(!Arrays.deepEquals(different, twoDimBytes));
		nonCoordinatorLapis.set("coord", "twoDimBytes", different);
		Validate.isTrue(Arrays.deepEquals(different, twoDimBytes));
		System.out.println("Tested two dimensional byte array.");
	}
	
	private void testTwoDimBooleanArray() {
		boolean[][] twoDimBooleans = LapisRandoms.getRandomArrayOfSameDimensions(new boolean[16][32]);
		nonCoordinatorLapis.publish("twoDimBooleans", twoDimBooleans);
		boolean[][] retrieved = coordinatorLapis.getTwoDimensionalArrayOfBoolean("non-coor", "twoDimBooleans");
		Validate.isTrue(Arrays.deepEquals(twoDimBooleans, retrieved));
		System.out.println("Tested two dimensional boolean array.");
	}
	
	private void testTwoDimLongArray() {
		long[][] twoDimLongs = LapisRandoms.getRandomArrayOfSameDimensions(new long[7][8]);
		coordinatorLapis.publish("twoDimLongs", twoDimLongs);
		long[][] different = LapisRandoms.getRandomArrayOfSameDimensions(twoDimLongs);
		Validate.isTrue(!Arrays.deepEquals(different, twoDimLongs));
		nonCoordinatorLapis.set("coord", "twoDimLongs", different);
		Validate.isTrue(Arrays.deepEquals(different, twoDimLongs));
		System.out.println("Tested two dimensional long array.");
	}
	
	private void testThreeDimIntegerArray() {
		final int[][][] threeDimInts = LapisRandoms.getRandomArrayOfSameDimensions(new int[8][6][7]);
		nonCoordinatorLapis.publish("threeDimInts", threeDimInts);
		int[][][] retrieved = coordinatorLapis.getThreeDimensionalArrayOfInt("non-coor", "threeDimInts");
		Validate.isTrue(Arrays.deepEquals(threeDimInts, retrieved));
		int[][][] different = LapisRandoms.getRandomArrayOfSameDimensions(threeDimInts);
		Validate.isTrue(!Arrays.deepEquals(different, threeDimInts));
		coordinatorLapis.set("non-coor", "threeDimInts", different);
		Validate.isTrue(Arrays.deepEquals(different, threeDimInts));
		System.out.println("Tested three dimensional integer array.");
	}

	private void testThreeDimDoubleArray() {
		final double[][][] threeDimDoubles = LapisRandoms.getRandomArrayOfSameDimensions(new double[4][8][16]);
		coordinatorLapis.publish("threeDimDoubles", threeDimDoubles);
		double[][][] retrieved = new double[4][8][16];
		Validate.isTrue(!Arrays.deepEquals(threeDimDoubles, retrieved));
		retrieved = nonCoordinatorLapis.getThreeDimensionalArrayOfDouble("coord", "threeDimDoubles");
		Validate.isTrue(Arrays.deepEquals(threeDimDoubles, retrieved));
		final double[][][] different = LapisRandoms.getRandomArrayOfSameDimensions(threeDimDoubles);
		Validate.isTrue(!Arrays.deepEquals(different, threeDimDoubles));
		nonCoordinatorLapis.set("coord", "threeDimDoubles", different);
		Validate.isTrue(Arrays.deepEquals(different, threeDimDoubles));
		System.out.println("Test three dimensional double array.");
	}
	
	private void testDimensionMismatch() {
		testOneDimMismatch();
		testThreeDimMismatch();
	}
	
	private void testOneDimMismatch() {
		String name = "oneDimDoubleArrayLength9";
		double[] da = LapisRandoms.getRandomArrayOfSameDimensions(new double[9]);
		nonCoordinatorLapis.publish(name, da);
		double[] daLen10 = LapisRandoms.getRandomArrayOfSameDimensions(new double[10]);
		try {
			coordinatorLapis.set("non-coor", name, daLen10);
			shouldNotHaveReachedThisPoint();
		} catch(LapisClientExceptionWithStatusCode e) {
			//this is expected
		}
	}

	private void testThreeDimMismatch() {
		String name = "threeDimLongArray614";
		long[][][] la = LapisRandoms.getRandomArrayOfSameDimensions(new long[6][1][4]);
		coordinatorLapis.publish(name, la);
		long[][][] mismatch = LapisRandoms.getRandomArrayOfSameDimensions(new long[8][7][6]);
		try {
			nonCoordinatorLapis.set("coord", name, mismatch);
			shouldNotHaveReachedThisPoint();
		} catch(LapisClientExceptionWithStatusCode e) {
			//this is expected
		}
	}
	
	private void shouldNotHaveReachedThisPoint() {
		throw new ShouldNotHaveReachedThisPointException();
	}
	
	@SuppressWarnings("serial")
	private class ShouldNotHaveReachedThisPointException extends RuntimeException {
		public ShouldNotHaveReachedThisPointException() {
			super("Code execution reached a location which it should not have reached.");
		}
	}
	
	private Properties getCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", "coord");
		p.setProperty("coordinator.url", COORDINATOR_URL);
		p.setProperty("localNodeAddress", COORDINATOR_URL);
		p.setProperty("port", Integer.toString(COORDINATOR_PORT));
		p.setProperty("isCoordinator", Boolean.toString(true));
		return p;
	}

	private Properties getNonCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", "non-coor");
		p.setProperty("coordinator.url", COORDINATOR_URL);
		p.setProperty("localNodeAddress", NON_COORDINATOR_URL);
		p.setProperty("port", Integer.toString(NON_COORDINATOR_PORT));
		p.setProperty("isCoordinator", Boolean.toString(false));
		return p;
	}
}