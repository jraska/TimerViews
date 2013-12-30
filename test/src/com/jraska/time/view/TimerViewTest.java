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

import android.os.Looper;
import android.test.AndroidTestCase;
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
public class TimerViewTest extends AndroidTestCase
{
	//region Constants

	public static final long TOLERANCE_MS = 2;

	//endregion

	//region Fields

	private TimerView mTestTimerView;
	private Looper mTimerViewLooper;

	private final Object mSyncLock = new Object();

	//endregion

	//region TestCase impl

	@Override
	protected void setUp() throws Exception
	{
		super.setUp();

		//latch for waiting to not start tests before thread setup is finished
		final CountDownLatch workerThreadSetupLatch = new CountDownLatch(1);

		Runnable workerRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				Looper.prepare();

				mTestTimerView = (TimerView) LayoutInflater.from(getContext()).inflate(R.layout.timer_test_main, null);
				mTimerViewLooper = Looper.myLooper();

				workerThreadSetupLatch.countDown();

				Looper.loop();
			}
		};

		Thread workerThread = new Thread(workerRunnable);
		workerThread.start();

		//wait for thread to setup
		final boolean threadInitializedOk = workerThreadSetupLatch.await(500, TimeUnit.MILLISECONDS);
		assertTrue("Worker thread for HandlerTicker test was not initialized in time.", threadInitializedOk);
	}

	@Override
	protected void tearDown() throws Exception
	{
		mTestTimerView.stop();
		mTimerViewLooper.quit();
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

	//endregion

	//region Methods

	private void prepareTimerView()
	{
		mTestTimerView.reset();
	}

	//endregion
}
