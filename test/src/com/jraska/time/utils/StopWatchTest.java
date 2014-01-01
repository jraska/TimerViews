/*
 * Copyright (c) 2014, Josef Raška
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

package com.jraska.time.utils;

import com.jraska.time.common.StartStopTestPart;
import junit.framework.TestCase;

public class StopWatchTest extends TestCase
{
	//region Constants

	private static final long TOLERANCE_MS = 2;

	//endregion

	//region Fields

	private final Object mLock = new Object();

	//endregion

	//region Test Methods

	public void testStartStopState()
	{
		StartStopTestPart.doAllStateTests(new StopWatch());
	}

	public void testSimpleElapsed() throws Exception
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		final long waitInterval = 10;

		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}

		stopWatch.stop();

		boolean elapsedRightInterval = stopWatch.getElapsedMs() < waitInterval + TOLERANCE_MS && stopWatch.getElapsedMs() > waitInterval - TOLERANCE_MS;
		assertTrue("StopWatch did not measured right.", elapsedRightInterval);
	}

	public void testPauseContinue() throws Exception
	{
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		final long waitInterval = 10;

		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}

		stopWatch.stop();

		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}

		stopWatch.start();

		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}

		stopWatch.stop();

		long elapsed = stopWatch.getElapsedMs();
		boolean elapsedRightInterval = elapsed < waitInterval * 2 + TOLERANCE_MS && elapsed > waitInterval * 2 - TOLERANCE_MS;
		assertTrue(String.format("StopWatch did not paused expected: %d but was: %d", 2 * waitInterval, elapsed), elapsedRightInterval);
	}

	public void testSettingTime() throws Exception
	{
		StopWatch stopWatch = new StopWatch();

		stopWatch.start();
		final long waitInterval = 10;
		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}
		stopWatch.stop();

		long startTime = 21627;
		stopWatch.setElapsedMs(startTime);
		long elapsed = stopWatch.getElapsedMs();

		boolean matchStartSet = elapsed == startTime;
		assertTrue(String.format("Start time set on start does not match elapsed. Expected: %d but was: %d", startTime, elapsed), matchStartSet);

		stopWatch.start();

		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}

		stopWatch.stop();

		elapsed = stopWatch.getElapsedMs();
		long expectedElapsed = startTime + waitInterval;

		boolean elapsedMatch = elapsed < expectedElapsed + TOLERANCE_MS && elapsed > expectedElapsed - TOLERANCE_MS;
		assertTrue(String.format("StopWatch elapsed does not match elapsed. Expected: %d but was: %d", expectedElapsed, elapsed), elapsedMatch);

		stopWatch.start();

		long newSetTime = 75617;
		stopWatch.setElapsedMs(newSetTime);

		synchronized (mLock)
		{
			mLock.wait(waitInterval);
		}

		stopWatch.stop();

		elapsed = stopWatch.getElapsedMs();
		expectedElapsed = newSetTime + waitInterval;
		boolean timeMatch = elapsed < expectedElapsed + TOLERANCE_MS && elapsed > expectedElapsed - TOLERANCE_MS;

		assertTrue(String.format("StopWatch elapsed does not match elapsed. Expected: %d but was: %d", expectedElapsed, elapsed), timeMatch);
	}

	//endregion
}
