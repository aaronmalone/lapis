package edu.osu.lapis;

import org.junit.Assert;
import org.junit.Test;

public class FlagsTest {

	@Test
	public void testConstantValues() {
		Assert.assertTrue(Flags.evaluateFlagValue(Flags.getFlag(true)));
		Assert.assertTrue(Flags.evaluateFlagValue(Flags.getFlagTrue()));
		Assert.assertFalse(Flags.evaluateFlagValue(Flags.getFlag(false)));
		Assert.assertFalse(Flags.evaluateFlagValue(Flags.getFlag()));
	}
}
