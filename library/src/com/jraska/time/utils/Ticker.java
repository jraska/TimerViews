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

import com.jraska.time.common.IStartStop;

/**
 * Base class to implement ticker. Ticker is utility class to tick in some interval.
 * It can be paused and resumed, reset or restarted.
 * <p/>
 * Each ticker has one ticker listener. Ticker listener cannot be null because this class has no purpose without listener.
 */
public abstract class Ticker implements IStartStop
{
	//region Constants

	public static final long DEFAULT_TICK_INTERVAL = 1000; //second

	private static final String TICK_INTERVAL_MUST_BE_POSITIVE = "tickInterval must be positive";

	//endregion

	//region Fields

	private OnTickListener mOnTickListener;
	private long mTickInterval;

	//endregion

	//region Constructors

	protected Ticker()
	{
		this(null);
	}

	protected Ticker(OnTickListener onTickListener)
	{
		this(onTickListener, DEFAULT_TICK_INTERVAL);
	}

	protected Ticker(OnTickListener onTickListener, long tickInterval)
	{
		if (tickInterval < 1)
		{
			throw new IllegalArgumentException(TICK_INTERVAL_MUST_BE_POSITIVE);
		}

		mOnTickListener = onTickListener;
		mTickInterval = tickInterval;
	}

	//endregion

	//region Properties

	public OnTickListener getOnTickListener()
	{
		return mOnTickListener;
	}

	public void setOnTickListener(OnTickListener onTickListener)
	{
		mOnTickListener = onTickListener;
	}

	public long getTickInterval()
	{
		return mTickInterval;
	}

	/**
	 * Returns time to next tick execution due to state of ticker.
	 *
	 * @return Remaining ms to next tick, -1 if not running.
	 */
	public abstract long getRemainingToNextTick();

	/**
	 * Sets tick interval.
	 * <p/>
	 * Interval change will take effect after complete of pending tick or immediately if the ticker is not running.
	 *
	 * @param tickInterval New tick interval
	 */
	public void setTickInterval(long tickInterval)
	{
		if (tickInterval < 1)
		{
			throw new IllegalArgumentException(TICK_INTERVAL_MUST_BE_POSITIVE);
		}

		mTickInterval = tickInterval;
	}

	//endregion

	//region Methods

	protected final void tick()
	{
		if (mOnTickListener != null)
		{
			mOnTickListener.onTick(this);
		}
		onTick();
	}

	protected void onTick()
	{
	}

	/**
	 * Changes current ticker tick duration if is some tick pending.
	 * <p/>
	 * On negative values the next tick will come earlier and on positive it will come sooner.
	 *
	 * @param moveMs Ms of which the next tick will be moved.
	 */
	public abstract void setNextTickMove(long moveMs);

	public abstract long getNextTickMove();

	//endregion

	//region Inner interface

	public interface OnTickListener
	{
		public void onTick(Ticker ticker);
	}

	//endregion
}
