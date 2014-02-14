package edu.osu.lapis;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.time.StopWatch;

import edu.osu.lapis.exception.LapisClientException;
import edu.osu.lapis.exception.LapisClientExceptionWithStatusCode;
import edu.osu.lapis.util.LapisRandoms;


public class LapisFunctionalTest {
	
	private Logger logger = Logger.getLogger(getClass());
	
	private static final String COORDINATOR_NODE_NAME = "coord";
	private static final String NON_COORDINATOR_NODE_NAME = "non-coor";
	
	private static final String COORDINATOR_URL = "http://localhost:11122";
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
		coordinatorLapis = new LapisApi(COORDINATOR_NODE_NAME, COORDINATOR_URL);
		nonCoordinatorLapis = new LapisApi(getNonCoordinatorProperties());
		StopWatch sw = new StopWatch();
		sw.start();
		testImpl();
		sw.stop();
		logger.info("Actual post-initialization test took " + sw.getTime() + " millis.");
	}
	
	private void testImpl() {
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
			coordinatorLapis.set(NON_COORDINATOR_NODE_NAME, "readOnlyDoubles", new double[]{1.0, 4.0, 9.0});
			shouldNotHaveReachedThisPoint();
		} catch(LapisClientException e) {
			//this is expected
			Validate.isTrue(e.getMessage().contains("read-only"));
		}
		logger.info("Tested read-only functionality.");
	}

	private void testReady() {
		coordinatorLapis.notReady();
		waitWithExpectedFailure(nonCoordinatorLapis, coordinatorLapis.getName());
		coordinatorLapis.ready();
		nonCoordinatorLapis.waitForReadyNode(COORDINATOR_NODE_NAME);
		logger.info("Tested ready() and waitForReadyNode().");
	}
	
	private void testNotReady() {
		nonCoordinatorLapis.ready();
		coordinatorLapis.waitForReadyNode(NON_COORDINATOR_NODE_NAME);
		nonCoordinatorLapis.notReady();
		waitWithExpectedFailure(coordinatorLapis, nonCoordinatorLapis.getName());
		nonCoordinatorLapis.ready();
		coordinatorLapis.waitForReadyNode(NON_COORDINATOR_NODE_NAME);
		logger.info("Tested notReady().");
	}
	
	private void waitWithExpectedFailure(LapisApi waiting, String nodeName) {
		final long timeoutMillis = 50;
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
		testStrings();
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
		byte[] retrieved = coordinatorLapis.getArrayOfByte(NON_COORDINATOR_NODE_NAME, "bytes");
		Validate.isTrue(Arrays.equals(bytes, retrieved));
		byte[] different = LapisRandoms.getRandomArrayOfSameDimensions(bytes);
		Validate.isTrue(!Arrays.equals(different, bytes));
		coordinatorLapis.set(NON_COORDINATOR_NODE_NAME, "bytes", different);
		Validate.isTrue(Arrays.equals(different, bytes));
		logger.info("Tested one dimensional byte array.");
	}
	
	private void testOneDimBooleanArray() {
		boolean[] bools = new boolean[] {true, false, true, true, true, true, true, false, true, true};
		nonCoordinatorLapis.publish("bools", bools);
		boolean[] boolsRetrieved = coordinatorLapis.getArrayOfBoolean(NON_COORDINATOR_NODE_NAME, "bools");
		Validate.isTrue(Arrays.equals(bools, boolsRetrieved));
		boolean[] different = LapisRandoms.getRandomArrayOfSameDimensions(bools);
		Validate.isTrue(!Arrays.equals(different, bools));
		coordinatorLapis.set(NON_COORDINATOR_NODE_NAME, "bools", different);
		Validate.isTrue(Arrays.equals(different, bools));
		logger.info("Tested one dimensional boolean array.");
	}
	
	private void testOneDimIntegerArray() {
		int[] ints = new int[] {8, 6, 7, -53, 0, 9};
		coordinatorLapis.publish("ints", ints);
		int[] retrieved = nonCoordinatorLapis.getArrayOfInt(COORDINATOR_NODE_NAME, "ints");
		Validate.isTrue(Arrays.equals(ints, retrieved));
		int[] different = Arrays.copyOf(ints, ints.length);
		Arrays.sort(different);
		Validate.isTrue(!Arrays.equals(different, ints));
		nonCoordinatorLapis.set(COORDINATOR_NODE_NAME, "ints", different);
		Validate.isTrue(Arrays.equals(different, ints));
		logger.info("Tested one dimensional integer array.");
	}

	private void testOneDimLongArray() {
		long[] longs = LapisRandoms.getOneDimensionalArrayOfLong();
		coordinatorLapis.publish("longs", longs);
		long[] retrieved = nonCoordinatorLapis.getArrayOfLong(COORDINATOR_NODE_NAME, "longs");
		Validate.isTrue(Arrays.equals(longs, retrieved));
		long[] different = LapisRandoms.getRandomArrayOfSameDimensions(longs);
		Validate.isTrue(!Arrays.equals(different, longs));
		nonCoordinatorLapis.set(COORDINATOR_NODE_NAME, "longs", different);
		Validate.isTrue(Arrays.equals(different, longs));
	}

	private void testOneDimDoubleArray() {
		double[] doubles = nonCoordinatorLapis.initializeAndPublishDoubleArray(
				"doubles", LapisRandoms.getOneDimensionalArrayOfDouble());
		double[] retrieved = coordinatorLapis.getArrayOfDouble(NON_COORDINATOR_NODE_NAME, "doubles");
		Validate.isTrue(Arrays.equals(doubles, retrieved));
		double[] different = LapisRandoms.getRandomArrayOfSameDimensions(doubles);
		Validate.isTrue(!Arrays.equals(different, doubles));
		coordinatorLapis.set(NON_COORDINATOR_NODE_NAME, "doubles", different);
		Validate.isTrue(Arrays.equals(different, doubles));
		logger.info("Tested one dimensional double array.");
	}

	private void testTwoDimByteArray() {
		byte[][] twoDimBytes = LapisRandoms.getRandomArrayOfSameDimensions(new byte[4][5]);
		coordinatorLapis.publish("twoDimBytes", twoDimBytes);
		byte[][] retrieved = nonCoordinatorLapis.getTwoDimensionalArrayOfByte(COORDINATOR_NODE_NAME, "twoDimBytes");
		Validate.isTrue(Arrays.deepEquals(twoDimBytes, retrieved)); //look into how this is implemented
		byte[][] different = LapisRandoms.getRandomArrayOfSameDimensions(twoDimBytes);
		Validate.isTrue(!Arrays.deepEquals(different, twoDimBytes));
		nonCoordinatorLapis.set(COORDINATOR_NODE_NAME, "twoDimBytes", different);
		Validate.isTrue(Arrays.deepEquals(different, twoDimBytes));
		logger.info("Tested two dimensional byte array.");
	}
	
	private void testTwoDimBooleanArray() {
		boolean[][] twoDimBooleans = LapisRandoms.getRandomArrayOfSameDimensions(new boolean[16][32]);
		nonCoordinatorLapis.publish("twoDimBooleans", twoDimBooleans);
		boolean[][] retrieved = coordinatorLapis.getTwoDimensionalArrayOfBoolean(NON_COORDINATOR_NODE_NAME, "twoDimBooleans");
		Validate.isTrue(Arrays.deepEquals(twoDimBooleans, retrieved));
		logger.info("Tested two dimensional boolean array.");
	}
	
	private void testTwoDimLongArray() {
		long[][] twoDimLongs = LapisRandoms.getRandomArrayOfSameDimensions(new long[7][8]);
		coordinatorLapis.publish("twoDimLongs", twoDimLongs);
		long[][] different = LapisRandoms.getRandomArrayOfSameDimensions(twoDimLongs);
		Validate.isTrue(!Arrays.deepEquals(different, twoDimLongs));
		nonCoordinatorLapis.set(COORDINATOR_NODE_NAME, "twoDimLongs", different);
		Validate.isTrue(Arrays.deepEquals(different, twoDimLongs));
		logger.info("Tested two dimensional long array.");
	}
	
	private void testThreeDimIntegerArray() {
		final int[][][] threeDimInts = LapisRandoms.getRandomArrayOfSameDimensions(new int[8][6][7]);
		nonCoordinatorLapis.publish("threeDimInts", threeDimInts);
		int[][][] retrieved = coordinatorLapis.getThreeDimensionalArrayOfInt(NON_COORDINATOR_NODE_NAME, "threeDimInts");
		Validate.isTrue(Arrays.deepEquals(threeDimInts, retrieved));
		int[][][] different = LapisRandoms.getRandomArrayOfSameDimensions(threeDimInts);
		Validate.isTrue(!Arrays.deepEquals(different, threeDimInts));
		coordinatorLapis.set(NON_COORDINATOR_NODE_NAME, "threeDimInts", different);
		Validate.isTrue(Arrays.deepEquals(different, threeDimInts));
		logger.info("Tested three dimensional integer array.");
	}

	private void testThreeDimDoubleArray() {
		final double[][][] threeDimDoubles = LapisRandoms.getRandomArrayOfSameDimensions(new double[4][8][16]);
		coordinatorLapis.publish("threeDimDoubles", threeDimDoubles);
		double[][][] retrieved = new double[4][8][16];
		Validate.isTrue(!Arrays.deepEquals(threeDimDoubles, retrieved));
		retrieved = nonCoordinatorLapis.getThreeDimensionalArrayOfDouble(COORDINATOR_NODE_NAME, "threeDimDoubles");
		Validate.isTrue(Arrays.deepEquals(threeDimDoubles, retrieved));
		final double[][][] different = LapisRandoms.getRandomArrayOfSameDimensions(threeDimDoubles);
		Validate.isTrue(!Arrays.deepEquals(different, threeDimDoubles));
		nonCoordinatorLapis.set(COORDINATOR_NODE_NAME, "threeDimDoubles", different);
		Validate.isTrue(Arrays.deepEquals(different, threeDimDoubles));
		logger.info("Test three dimensional double array.");
	}
	
	private void testStrings() {
		String pubString = RandomStringUtils.randomAlphanumeric(10);
		coordinatorLapis.publishReadOnly("pubString", pubString);
		String retrieved = nonCoordinatorLapis.getString(COORDINATOR_NODE_NAME, "pubString");
		Validate.isTrue(StringUtils.equals(pubString, retrieved));
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
			coordinatorLapis.set(NON_COORDINATOR_NODE_NAME, name, daLen10);
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
			nonCoordinatorLapis.set(COORDINATOR_NODE_NAME, name, mismatch);
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
	
	private Properties getNonCoordinatorProperties() {
		Properties p = new Properties();
		p.setProperty("name", NON_COORDINATOR_NODE_NAME);
		p.setProperty("coordinator.url", COORDINATOR_URL);
		p.setProperty("localNodeAddress", NON_COORDINATOR_URL);
		p.setProperty("port", Integer.toString(NON_COORDINATOR_PORT));
		p.setProperty("isCoordinator", Boolean.toString(false));
		return p;
	}
}