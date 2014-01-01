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

package com.jraska.time.view;

import android.view.LayoutInflater;
import android.view.View;
import com.jraska.time.common.StartStopTestPart;
import com.jraska.time.demo.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Common test for {@link com.jraska.time.view.TimerView}
 * and methods contained in {@link com.jraska.time.view.AbstractTimerView}
 * using TimerView as test Object
 */
public class TimerViewTest extends TimerViewTestBase
{
	//region Fields

	private TimerView mTestTimerView;

	private final Object mSyncLock = new Object();

	//endregion

	//region TestCase impl

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		mTestTimerView = (TimerView) getTestView();
	}

	//endregion

	//region TimerViewTestBase impl

	@Override
	protected int getLayoutResId()
	{
		return R.layout.timer_test_main;
	}

	//endregion

	//region Test methods

	public void testStartStopState() throws Exception
	{
		View view = LayoutInflater.from(getContext()).inflate(R.layout.timer_test, null);
		TimerView timerView = (TimerView) view;

		StartStopTestPart.doAllStateTests(timerView);
	}

	public void testAttrs() throws Exception
	{
		View view = LayoutInflater.from(getContext()).inflate(R.layout.timer_test, null);
		TimerView timerView = (TimerView) view;

		assertTrue(timerView.isAutoStart());
		assertTrue(timerView.getTickInterval() == 123);
		assertFalse(timerView.isPauseTickingOnWindowDisappear());
	}

	public void testPauseSyncOnDetaching() throws Exception
	{
		prepareTimerView();

		int tickCount = 3;
		final CountDownLatch countDownLatch = new CountDownLatch(tickCount);

		final long tickInterval = mTestTimerView.getTickInterval();
		final long detachedTime = 30 + tickInterval / 2; //to ensure detaching will break sync

		long lastTickOffset = tickInterval - (detachedTime % tickInterval);

		final long totalTime = tickCount * tickInterval + detachedTime + lastTickOffset;

		mTestTimerView.setPauseTickingOnWindowDisappear(true);
		mTestTimerView.setOnTimerTickListener(new TimerView.OnTimerTickListener()
		{
			@Override
			public void onTick(TimerView timerView, long elapsedMillis)
			{
				countDownLatch.countDown();

				boolean wasLastTick = countDownLatch.getCount() == 0;
				if (wasLastTick)
				{
					mTestTimerView.stop();
				}
			}
		});

		mTestTimerView.start();

		long initWaitTime = tickInterval * (tickCount - 1) + tickInterval / 2; // will pause ticking on detach
		synchronized (mSyncLock)
		{
			mSyncLock.wait(initWaitTime);
		}

		mTestTimerView.onDetachedFromWindow();

		synchronized (mSyncLock)
		{
			mSyncLock.wait(detachedTime);
		}

		assertTrue("TimerView did not paused on detach", countDownLatch.getCount() == 1);

		mTestTimerView.onAttachedToWindow();

		long remainingTime = totalTime - initWaitTime - detachedTime;

		final boolean countedDown = countDownLatch.await(remainingTime + TOLERANCE_MS, TimeUnit.MILLISECONDS);
		String notCountedMessage = "TimerView did not counted " + tickCount + " times in " + totalTime + " with " + detachedTime + "ms detached.";
		assertTrue(notCountedMessage, countedDown);

		//timer was stopped on last tick - millis should match the period of ticking
		long elapsed = mTestTimerView.getElapsedMs();
		boolean syncedElapsed = (elapsed + 1) % tickInterval <= TOLERANCE_MS;
		String notSyncedMessage = String.format("Elapsed %d is not synced with interval %d.", elapsed, tickInterval);
		assertTrue(notSyncedMessage, syncedElapsed);

		boolean runTooFast = elapsed + TOLERANCE_MS < totalTime;
		String tooFastMessage = String.format("Timer view ticked too fast expected: %d but was: %d", totalTime, elapsed);
		assertFalse(tooFastMessage, runTooFast);
	}

	private String mErrorMessage;

	public void testListener() throws Exception
	{
		long tickInterval = 20;
		mTestTimerView.setTickInterval(tickInterval);
		int tickCount = 3;

		final CountDownLatch countDownLatch = new CountDownLatch(tickCount);
		mTestTimerView.setOnTimerTickListener(new TimerView.OnTimerTickListener()
		{
			@Override
			public void onTick(TimerView timerView, long elapsedMillis)
			{
				if (countDownLatch.getCount() == 0)
				{
					mErrorMessage = "TimerView ticked too fast.";
				}

				countDownLatch.countDown();
			}
		});

		mTestTimerView.restart();

		long waitTime = (tickCount * tickInterval);
		final boolean tickedRight = countDownLatch.await(waitTime + TOLERANCE_MS, TimeUnit.MILLISECONDS);

		mTestTimerView.stop();

		assertTrue(String.format("TimerView did not ticked %d times in %d ms.", tickCount, waitTime), tickedRight);

		if (mErrorMessage != null)
		{
			fail(mErrorMessage);
		}
	}

	//endregion

	//region Methods

	private void prepareTimerView()
	{
		mTestTimerView.reset();
	}

	//endregion
}
