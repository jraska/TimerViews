/*
 * Copyright (c) 2013, Josef Raška
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jraska.time.common;

import junit.framework.Assert;

import java.util.Random;

/**
 * Code part for tests of {@link com.jraska.time.common.IStartStop} implementations checking state handling, stopping etc.
 */
public class StartStopTestPart
{
	//region Constants

	public static final int RANDOM_TEST_ITERATIONS = 100;

	//endregion

	//region TestMethods

	public static void doAllStateTests(IStartStop startStop)
	{
		testRunningAfterStart(startStop);
		testNotRunningAfterStop(startStop);
		testRunningAfterRestart(startStop);
		testNotRunningAfterReset(startStop);
		testStartStopChanging(startStop);
	}

	public static void testRunningAfterStart(IStartStop startStop)
	{
		startStop.start();

		Assert.assertTrue(String.format("%s error - not running after start", startStop.getClass().getName()), startStop.isRunning());
	}

	public static void testNotRunningAfterStop(IStartStop startStop)
	{
		startStop.stop();

		Assert.assertFalse(String.format("%s error - running after stop", startStop.getClass().getName()), startStop.isRunning());
	}

	public static void testRunningAfterRestart(IStartStop startStop)
	{
		startStop.restart();

		Assert.assertTrue(String.format("%s error - not running after start", startStop.getClass().getName()), startStop.isRunning());
	}

	public static void testNotRunningAfterReset(IStartStop startStop)
	{
		startStop.reset();

		Assert.assertFalse(String.format("%s error - running after reset", startStop.getClass().getName()), startStop.isRunning());
	}

	public static void testStartStopChanging(IStartStop startStop)
	{
		Random random = new Random();
		for (int i = 0; i < RANDOM_TEST_ITERATIONS; i++)
		{
			switch (random.nextInt(4))
			{
				case 0:
					testNotRunningAfterReset(startStop);
					break;
				case 1:
					testRunningAfterStart(startStop);
					break;
				case 2:
					testNotRunningAfterStop(startStop);
					break;
				case 3:
					testRunningAfterRestart(startStop);
					break;
				default:
					throw new IllegalArgumentException("Error in generation.");
			}
		}
	}

	//endregion
}
