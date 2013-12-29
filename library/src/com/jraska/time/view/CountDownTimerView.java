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

	public long getCountDownTime()
	{
		return mCountDownTime;
	}

	public void setCountDownTime(long countDownMs)
	{
		setCountDownTime(countDownMs, true);
	}

	public void setCountDownTime(long countDownTime, boolean updateImmediate)
	{
		if (countDownTime < 0)
		{
			throw new IllegalArgumentException("countDownTime cannot be negative");
		}

		mCountDownTime = countDownTime;

		if (updateImmediate)
		{
			updateRemainingTime(getRemainingMs());
		}
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

		updateRemainingTime(remaining);
		notifyCountDown(remaining);
	}

	protected void updateRemainingTime(long remaining)
	{
		//on negative value, remaining is zero and countdown should be stopped
		if (remaining < 1)
		{
			remaining = 0;
		}

		updateText(remaining);

		//finish is called after update text to have consistent state on finish event
		if (remaining == 0)
		{
			finish();
		}
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
