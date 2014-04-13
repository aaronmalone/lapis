package edu.osu.lapis;

import org.junit.Assert;
import org.junit.Test;

public class FlagsTest {

	@Test
	public void testConstantValues() {
		Assert.assertTrue(Flags.evaluateFlagValue(Flags.FLAG_VALUE_TRUE));
		Assert.assertFalse(Flags.evaluateFlagValue(Flags.FLAG_VALUE_FALSE));
	}
}
