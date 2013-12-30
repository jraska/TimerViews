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
import android.util.AttributeSet;
import com.jraska.time.common.IStartStop;

public class TimerView extends AbstractTimerView implements IStartStop
{
	//region Constants

//	private static final String TAG = TimerView.class.getName();

	//endregion

	//region Fields

	private OnTimerTickListener mOnTimerTickListener;

	//endregion

	//region Constructors

	public TimerView(Context context)
	{
		super(context);

		init();
	}

	public TimerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		init();
	}

	public TimerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		init();
	}

	//endregion

	//region Properties

	public OnTimerTickListener getOnTimerTickListener()
	{
		return mOnTimerTickListener;
	}

	public void setOnTimerTickListener(OnTimerTickListener onTimerTickListener)
	{
		mOnTimerTickListener = onTimerTickListener;
	}

	//setting elapsed is public now
	@Override
	public void setElapsedMs(long elapsedMs)
	{
		super.setElapsedMs(elapsedMs);
	}

	@Override
	public void setElapsedMs(long elapsedMs, boolean syncTicks)
	{
		super.setElapsedMs(elapsedMs, syncTicks);
	}

	//endregion

	//region AbstractTimerView implementation

	@Override
	protected void setTextToInitState()
	{
		updateText(0);
	}

	@Override
	public void updateTextNow()
	{
		updateElapsedTime(getElapsedMs());
	}

	@Override
	final void onTickInternal()
	{
		long elapsedMs = getElapsedMs();

		updateElapsedTime(elapsedMs);
		notifyListener(elapsedMs);
	}

	//endregion

	//region Methods

	private void init()
	{
	}

	protected void updateElapsedTime(long elapsedMs)
	{
		updateText(elapsedMs);
	}

	private void notifyListener(long elapsedMs)
	{
		if (mOnTimerTickListener != null)
		{
			mOnTimerTickListener.onTick(this, elapsedMs);
		}
	}

	//endregion

	//region Inner interface

	public interface OnTimerTickListener
	{
		void onTick(TimerView timerView, long elapsedMillis);
	}

	//endregion
}
