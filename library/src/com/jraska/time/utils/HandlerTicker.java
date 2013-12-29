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
import android.os.Message;
import android.os.SystemClock;
import com.jraska.time.common.IStartStop;

/**
 * Ticker using android handler to ticks synchronization
 *
 * @see com.jraska.time.utils.Ticker
 * @see android.os.Handler
 */
public final class HandlerTicker extends Ticker implements IStartStop
{
	//region Constants

	private static final int TICK_WHAT = 2;

	//endregion

	//region Fields

	private boolean mRunning;
	private long mFutureTickTime;
	private long mExecutedLastTickTime = 0;
	private long mNextTickMove = 0;

	private final Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message m)
		{
			if (isRunning())
			{
				doTick();
			}
		}
	};

	//endregion

	//region Constructors

	public HandlerTicker(OnTickListener onTickListener)
	{
		super(onTickListener);
	}

	public HandlerTicker(OnTickListener onTickListener, long tickInterval)
	{
		super(onTickListener, tickInterval);
	}

	//endregion

	//region Properties

	public boolean isRunning()
	{
		return mRunning;
	}

	private void setRunning(boolean running)
	{
		mRunning = running;
	}

	@Override
	public long getRemainingToNextTick()
	{
		if (!isRunning())
		{
			return getTickInterval() - mExecutedLastTickTime + mNextTickMove;
		}

		long remaining = mFutureTickTime - SystemClock.elapsedRealtime();
		if (remaining < 0)
		{
			return 0;
		}

		return remaining;
	}

	@Override
	public long getNextTickMove()
	{
		return mNextTickMove;
	}

	@Override
	public void setNextTickMove(long moveMs)
	{
		mNextTickMove = moveMs;
	}

	//endregion

	//region IStartStop impl

	public void start()
	{
		if (isRunning())
		{
			return;
		}

		long elapsed = SystemClock.elapsedRealtime();
		mFutureTickTime = elapsed + getTickInterval() - mExecutedLastTickTime + mNextTickMove;
		mNextTickMove = 0;
		sendNextTick(elapsed);

		setRunning(true);
	}

	public void stop()
	{
		if (!isRunning())
		{
			return;
		}

		mExecutedLastTickTime = countExecutedLastTick();

		clearNextTicks();

		setRunning(false);
	}

	public void reset()
	{
		stop();

		mExecutedLastTickTime = 0;
	}

	public void restart()
	{
		stop();
		reset();
		start();
	}

	//endregion

	//region Methods

	private void doTick()
	{
		tick();
		updateNextTick();
		sendNextTick(SystemClock.elapsedRealtime());
	}

	private long countExecutedLastTick()
	{
		long remaining = mFutureTickTime - SystemClock.elapsedRealtime();
		return getTickInterval() - remaining;
	}

	private void sendNextTick(long currentMillis)
	{
		//count delay
		long delay = mFutureTickTime - currentMillis;

		mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), delay);
	}

	private void clearNextTicks()
	{
		mHandler.removeMessages(TICK_WHAT);
	}

	private void updateNextTick()
	{
		mFutureTickTime = mFutureTickTime + getTickInterval() + mNextTickMove;
		mNextTickMove = 0;
	}

	//endregion
}
