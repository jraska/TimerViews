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

package com.jraska.time.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import com.jraska.time.common.StartStopTestPart;
import junit.framework.TestCase;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HandlerTickerTest extends TestCase
{
	//region Constants

	private static final long DefaultTestTickIntervalMs = 20;
	private static final long TestTickIntervalToleranceMs = 4; //to count with time delays of executing code

	//endregion

	//region Fields

	private HandlerTicker mTicker;
	private Looper mTickerLooper;

	//endregion

	//region TestCase implementation

	@Override
	protected void setUp() throws Exception
	{
		//latch for waiting to not start tests before thread setup is finished
		final CountDownLatch workerThreadSetupLatch = new CountDownLatch(1);

		Runnable workerRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				Looper.prepare();

				mTicker = new HandlerTicker(null, DefaultTestTickIntervalMs);
				mTickerLooper = Looper.myLooper();

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
		mTicker.stop();
		mTickerLooper.quit();
	}

	//endregion

	//region Test methods

	public void testStartStopState() throws Exception
	{
		StartStopTestPart.doAllStateTests(mTicker);
	}

	public void testSingleTick() throws Exception
	{
		testTicks(1, DefaultTestTickIntervalMs);
	}

	public void testTenTicks() throws Exception
	{
		testTicks(10, DefaultTestTickIntervalMs);
	}

	public void testIntervalChanged() throws Exception
	{
		testTicks(2, 35);
	}

	public void testRuntimeIntervalChange() throws Exception
	{
		final int changeIntervalWhat = 14348;
		Handler intervalChangeHandler = new Handler(mTickerLooper) //ticker looper is used to simulate ui thread serial message executing
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case changeIntervalWhat:
						long newInterval = (Long) msg.obj;
						mTicker.setTickInterval(newInterval);
						break;
				}
			}
		};

		final Message changeIntervalTo30Message = Message.obtain(intervalChangeHandler, changeIntervalWhat, 30L);
		intervalChangeHandler.sendMessageDelayed(changeIntervalTo30Message, 5); //after five millis it will change delay to 30

		final Message changeIntervalTo10Message = Message.obtain(intervalChangeHandler, changeIntervalWhat, 10L);
		intervalChangeHandler.sendMessageDelayed(changeIntervalTo10Message, 75); //will set 5 millis before next tick

		//expected behaviour is one tick with start value of 20 ms, two ticks with 30 and than two ticks with 10 ms interval
		long totalExpectedDuration = 20 + 30 + 30 + 10 + 10;
		testTicks(5, 20, totalExpectedDuration);
	}

	public void testTickTimeChangeOnPause() throws Exception
	{
		final int changeIntervalWhat = 2334;
		Handler intervalChangeHandler = new Handler(mTickerLooper) //ticker looper is used to simulate ui thread serial message executing
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case changeIntervalWhat:
						mTicker.stop();
						long newInterval = (Long) msg.obj;
						mTicker.setTickInterval(newInterval);
						mTicker.start();
						break;
				}
			}
		};

		final Message changeIntervalTo30Message = Message.obtain(intervalChangeHandler, changeIntervalWhat, 30L);
		intervalChangeHandler.sendMessageDelayed(changeIntervalTo30Message, 5); //after five millis it will change delay to 30,

		final Message changeIntervalTo10Message = Message.obtain(intervalChangeHandler, changeIntervalWhat, 10L);
		intervalChangeHandler.sendMessageDelayed(changeIntervalTo10Message, 75); //will set 5 millis before next tick

		//expected behaviour is 2 30 ms ticks than replaced with 10 ms ticks
		long totalExpectedDuration = 30 + 30 + 10 + 10 + 10;
		testTicks(5, 20, totalExpectedDuration);
	}

	public void testNextTickMove() throws Exception
	{
		final int tickMoveId = 7890;
		Handler moveNextTickHandler = new Handler(mTickerLooper) //ticker looper is used to simulate ui thread serial message executing
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case tickMoveId:
						long moveMs = (Long) msg.obj;
						mTicker.setNextTickMove(moveMs);
						break;
				}
			}
		};

		final Message changeIntervalTo30Message = Message.obtain(moveNextTickHandler, tickMoveId, 10L);
		moveNextTickHandler.sendMessageDelayed(changeIntervalTo30Message, 25); //will delay next tick by 10 ms,

		final Message changeIntervalTo10Message = Message.obtain(moveNextTickHandler, tickMoveId, -10L);
		moveNextTickHandler.sendMessageDelayed(changeIntervalTo10Message, 45); //will speed up next tick by 10 ms

		//expected behaviour is one 20 ms tick, one 30 ms tick one 10 ms and one 20 ms tick again.
		long totalExpectedDuration = 20 + 30 + 10 + 20;
		testTicks(4, 20, totalExpectedDuration);
	}

	private String mRemainingTimeFailMessage;

	public void testRemainingTimeCounting() throws Exception
	{
		final int checkRemainingToNextTickId = 2627727;
		final int tolerance = 1;
		Handler testHandler = new Handler(mTickerLooper)
		{
			@Override
			public void handleMessage(Message msg)
			{
				switch (msg.what)
				{
					case checkRemainingToNextTickId:
						long remainingMs = mTicker.getRemainingToNextTick();

						long expectedValue = (Long) msg.obj;
						long lowerBorder = expectedValue - tolerance;
						long upperBorder = expectedValue + tolerance;

						if (lowerBorder > remainingMs || upperBorder < remainingMs)
						{
							mRemainingTimeFailMessage = String.format("Error checking remaining ms. Expected: %d but was: %d", expectedValue, remainingMs);
						}

						break;
				}
			}
		};

		final Message checkRemainingTime1 = Message.obtain(testHandler, checkRemainingToNextTickId, DefaultTestTickIntervalMs - 8);
		testHandler.sendMessageDelayed(checkRemainingTime1, 8);

		final Message checkRemainingTime2 = Message.obtain(testHandler, checkRemainingToNextTickId, DefaultTestTickIntervalMs - 15);
		testHandler.sendMessageDelayed(checkRemainingTime2, 15);
//
		final Message checkRemainingTime3 = Message.obtain(testHandler, checkRemainingToNextTickId, 5L);
		testHandler.sendMessageDelayed(checkRemainingTime3, 2 * DefaultTestTickIntervalMs - 5);

		testTicks(2, DefaultTestTickIntervalMs);

		if (mRemainingTimeFailMessage != null)
		{
			fail(mRemainingTimeFailMessage);
		}
	}

	public void testConsistencyOnTickEvent() throws Exception
	{
		//TODO
	}

	//endregion

	//region Methods

	private void testTicks(int tickCount, long tickInterval) throws Exception
	{
		testTicks(tickCount, tickInterval, tickCount * tickInterval);
	}

	private void testTicks(int tickCount, long tickInterval, long expectedDuration) throws InterruptedException
	{
		prepareTicker();

		mTicker.setTickInterval(tickInterval);
		final CountDownLatch countDownLatch = new CountDownLatch(tickCount);
		mTicker.setOnTickListener(new Ticker.OnTickListener()
		{
			@Override
			public void onTick(Ticker ticker)
			{
				countDownLatch.countDown();
			}
		});

		long toleranceDuration = expectedDuration + TestTickIntervalToleranceMs;
		mTicker.start();

		long startWait = SystemClock.elapsedRealtime();
		final boolean counted = countDownLatch.await(toleranceDuration, TimeUnit.MILLISECONDS);
		long realDuration = SystemClock.elapsedRealtime() - startWait;

		//check time exceeding
		assertTrue("Ticker did not ticked " + tickCount + " times in " + expectedDuration + " ms", counted);

		//check running too fast
		boolean wasFasterThanExpected = realDuration + TestTickIntervalToleranceMs < expectedDuration;
		assertFalse("Ticker ticked too fast. Expected: " + expectedDuration + " but was: " + realDuration, wasFasterThanExpected);

		mTicker.stop();
	}

	private void prepareTicker()
	{
		mTicker.reset();
	}

	//endregion
}
