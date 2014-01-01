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

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.jraska.time.R;

public class CountDownTimerView extends AbstractTimerView
{
	//region Constants

	public static final long DEFAULT_COUNT_DOWN_TIME = 60 * 1000;

	//endregion

	//region Fields

	private long mCountDownTime;
	private OnCountDownListener mOnCountDownListener;

	//endregion

	//region Constructors

	public CountDownTimerView(Context context)
	{
		super(context);

		mCountDownTime = DEFAULT_COUNT_DOWN_TIME;
	}

	public CountDownTimerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initCountDownTimer(attrs);
	}

	public CountDownTimerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		initCountDownTimer(attrs);
	}

	private void initCountDownTimer(AttributeSet attrs)
	{
		final TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownTimerView);

		long hours = typedArray.getInt(R.styleable.CountDownTimerView_countDownHours, 0);
		long minutes = typedArray.getInt(R.styleable.CountDownTimerView_countDownMinutes, 0);
		long seconds = typedArray.getInt(R.styleable.CountDownTimerView_countDownSeconds, 0);
		long millis = typedArray.getInt(R.styleable.CountDownTimerView_countDownMillis, 0);

		long totalCountDownMs = toMillis(hours, minutes, seconds, millis);
		mCountDownTime = totalCountDownMs;

		typedArray.recycle();
	}

	//endregion

	//region Properties

	/**
	 * Returns time in milliseconds for which the view will count down.
	 *
	 * @return Total countdown time.
	 */
	public long getCountDownTime()
	{
		return mCountDownTime;
	}

	/**
	 * Sets countdown to new value and start countdown again from begin
	 *
	 * @param countDownTime New countdown time
	 * @throws java.lang.IllegalArgumentException If the countdown value is negative.
	 */
	public void setCountDownTime(long countDownTime)
	{
		if (countDownTime < 0)
		{
			throw new IllegalArgumentException("countDownTime cannot be negative");
		}

		mCountDownTime = countDownTime;

		//this resets countdown to start from beginning
		setElapsedMs(0);
	}

	public long getRemainingMs()
	{
		return mCountDownTime - getElapsedMs();
	}

	public OnCountDownListener getOnCountDownListener()
	{
		return mOnCountDownListener;
	}

	public void setOnCountDownListener(OnCountDownListener onCountDownListener)
	{
		mOnCountDownListener = onCountDownListener;
	}

	//endregion

	//region AbstractTimerView implementation

	@Override
	protected void setTextToInitState()
	{
		setCountDownStartTime();
	}

	@Override
	public void updateTextNow()
	{
		long remaining = mCountDownTime - getElapsedMs();
		updateRemainingTime(remaining);
	}

	@Override
	void onTickInternal()
	{
		long elapsed = getElapsedMs();
		long remaining = mCountDownTime - elapsed;

		//on negative value, remaining is zero and countdown should be stopped
		if (remaining < 1)
		{
			remaining = 0;
		}

		updateRemainingTime(remaining);
		notifyCountDown(remaining);

		//finish is called after update text to have consistent state on finish event
		if (remaining == 0)
		{
			finish();
		}
	}

	private void updateRemainingTime(long remaining)
	{
		updateText(remaining);
	}

	private void finish()
	{
		stop();
		notifyFinished();
	}

	//endregion

	//region Methods

	private void notifyCountDown(long remaining)
	{
		if (mOnCountDownListener != null)
		{
			mOnCountDownListener.onCountDownTick(this, remaining);
		}
	}

	private void notifyFinished()
	{
		if (mOnCountDownListener != null)
		{
			mOnCountDownListener.onFinish(this);
		}
	}

	protected final void setCountDownStartTime()
	{
		updateText(mCountDownTime);
	}

	static long toMillis(long hours, long minutes, long seconds, long millis)
	{
		return (((((hours * 60) + minutes) * 60) + seconds) * 1000) + millis;
	}

	//endregion

	//region Inner interface

	public interface OnCountDownListener
	{
		void onCountDownTick(CountDownTimerView countDownTimerView, long remainingMillis);

		void onFinish(CountDownTimerView countDownTimerView);
	}

	//endregion
}
