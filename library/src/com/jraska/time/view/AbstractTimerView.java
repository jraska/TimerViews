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

package com.jraska.time.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.jraska.time.R;
import com.jraska.time.common.IStartStop;
import com.jraska.time.format.*;
import com.jraska.time.utils.HandlerTicker;
import com.jraska.time.utils.StopWatch;
import com.jraska.time.utils.Ticker;

/**
 * Base class for Views displaying time.
 */
public abstract class AbstractTimerView extends TextView implements IStartStop
{
	//region Constants

	public static final int DEFAULT_TICK_INTERVAL = 1000; //second

	//styleable enum attributes value
	private static final int DISPLAY_PRECISION_FIT_TICK_INTERVAL = 0;
	private static final int DISPLAY_PRECISION_SECONDS = 1;
	private static final int DISPLAY_PRECISION_TENTHS = 2;
	private static final int DISPLAY_PRECISION_HUNDREDTHS = 3;
	private static final int DISPLAY_PRECISION_MILLIS = 4;

	//endregion

	//region Static factories

	private static IMillisFormatterFactory sMillisFormatterFactory = new DefaultMillisFormatterFactory();
	private static ITickerFactory sTickerFactory = new DefaultTickerFactory();

	public static IMillisFormatterFactory getMillisFormatterFactory()
	{
		return sMillisFormatterFactory;
	}

	public static void setMillisFormatterFactory(IMillisFormatterFactory factory)
	{
		if (factory == null)
		{
			throw new IllegalArgumentException("Factory cannot be null.");
		}

		sMillisFormatterFactory = factory;
	}

	public static ITickerFactory getTickerFactory()
	{
		return sTickerFactory;
	}

	public static void setTickerFactory(ITickerFactory tickerFactory)
	{
		if (tickerFactory == null)
		{
			throw new IllegalArgumentException("tickerFactory cannot be null.");
		}

		sTickerFactory = tickerFactory;
	}

	//endregion

	//region Fields

	private final StopWatch mStopWatch = new StopWatch();
	private Ticker mTicker;

	private final Ticker.OnTickListener mOnTickListener = new Ticker.OnTickListener()
	{
		@Override
		public void onTick(Ticker ticker)
		{
			doTick();
		}
	};

	private long mLastDisplayedMs;

	private IMillisFormatter mMillisFormatter;
	private boolean mAutoStart;
	private boolean mPauseTickingOnWindowDisappear;
	private boolean mTickingPaused;

	//endregion

	//region Constructors

	public AbstractTimerView(Context context)
	{
		super(context);

		initAbstractTimerView();
		commonAbstractTimerViewInit();
	}

	public AbstractTimerView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		initAbstractTimerView(attrs);
		commonAbstractTimerViewInit();
	}

	public AbstractTimerView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);

		initAbstractTimerView(attrs);
		commonAbstractTimerViewInit();
	}

	private void initAbstractTimerView()
	{
		mTicker = sTickerFactory.createTicker(new TickerFactoryParameters(mOnTickListener, DEFAULT_TICK_INTERVAL));
		mMillisFormatter = sMillisFormatterFactory.createFormatter(new FormatterFactoryParameters(DISPLAY_PRECISION_FIT_TICK_INTERVAL, DEFAULT_TICK_INTERVAL));
		mPauseTickingOnWindowDisappear = true;
	}

	private void initAbstractTimerView(AttributeSet attrs)
	{
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AbstractTimerView);

		mAutoStart = a.getBoolean(R.styleable.AbstractTimerView_autoStart, false);
		mPauseTickingOnWindowDisappear = a.getBoolean(R.styleable.AbstractTimerView_pauseTickingOnWindowDisappear, true);

		int tickInterval = a.getInt(R.styleable.AbstractTimerView_tickInterval, DEFAULT_TICK_INTERVAL);
		mTicker = sTickerFactory.createTicker(new TickerFactoryParameters(mOnTickListener, tickInterval));

		final int displayPrecision = a.getInt(R.styleable.AbstractTimerView_displayPrecision, DISPLAY_PRECISION_FIT_TICK_INTERVAL);
		mMillisFormatter = sMillisFormatterFactory.createFormatter(new FormatterFactoryParameters(displayPrecision, tickInterval));

		a.recycle();
	}

	private void commonAbstractTimerViewInit()
	{
		//init state can be changed in child classes and cannot be run directly in constructors
		post(new Runnable()
		{
			@Override
			public void run()
			{
				setTextToInitState();
			}
		});
	}

	//endregion

	//region Properties

	public long getLastDisplayedMs()
	{
		return mLastDisplayedMs;
	}

	public long getTickInterval()
	{
		return mTicker.getTickInterval();
	}

	public void setTickInterval(long tickInterval)
	{
		mTicker.setTickInterval(tickInterval);
	}

	public boolean isAutoStart()
	{
		return mAutoStart;
	}

	@Override
	public final boolean isRunning()
	{
		return mStopWatch.isRunning();
	}

	/**
	 * Gets how much milliseconds elapsed since view started counting.
	 *
	 * @return Time in millis for which the view was in running state.
	 */
	public long getElapsedMs()
	{
		return mStopWatch.getElapsedMs();
	}

	/**
	 * Sets view time to specified value. This allows move view state to provided value.
	 *
	 * @param elapsedMs Not negative to which will be view set.
	 * @throws java.lang.IllegalArgumentException If the value is negative.
	 */
	protected void setElapsedMs(long elapsedMs)
	{
		mStopWatch.setElapsedMs(elapsedMs);
		mTicker.stop();
		syncTickerToElapsed();
		mTicker.start();
	}

	/**
	 * Gets current formatter which is used to format millisecond to display.
	 *
	 * @return Current formatter.
	 */
	public IMillisFormatter getMillisFormatter()
	{
		return mMillisFormatter;
	}

	/**
	 * Sets new millis formatter to format milliseconds to display.
	 *
	 * @param millisFormatter New millis formatter to format millis.
	 * @throws java.lang.IllegalArgumentException If the formatter is null.
	 */
	public void setMillisFormatter(IMillisFormatter millisFormatter)
	{
		if (millisFormatter == null)
		{
			throw new IllegalArgumentException("millisFormatter cannot be null");
		}

		mMillisFormatter = millisFormatter;
	}

	public boolean isPauseTickingOnWindowDisappear()
	{
		return mPauseTickingOnWindowDisappear;
	}

	public void setPauseTickingOnWindowDisappear(boolean pauseTickingOnWindowDisappear)
	{
		mPauseTickingOnWindowDisappear = pauseTickingOnWindowDisappear;
	}

	//endregion

	//region Window attaching handling methods

	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();

		if (mAutoStart)
		{
			start();
		}

		if (wasTickingPaused())
		{
			resumePausedTicker();
		}
	}

	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();

		if (isRunning() && mPauseTickingOnWindowDisappear)
		{
			pauseTicker();
		}
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility)
	{
		super.onVisibilityChanged(changedView, visibility);

		boolean visible = VISIBLE == visibility;
		if (visible)
		{
			if (wasTickingPaused())
			{
				resumePausedTicker();
			}
		}
		else
		{
			if (isRunning() && mPauseTickingOnWindowDisappear)
			{
				pauseTicker();
			}
		}
	}

	private boolean wasTickingPaused()
	{
		return mTickingPaused;
	}

	private void pauseTicker()
	{
		mTicker.stop();
		mTickingPaused = true;
	}

	private void resumePausedTicker()
	{
		updateTextNow();
		syncTickerToElapsed();
		mTicker.start();
		mTickingPaused = false;
	}

	//endregion

	//region Methods

	public final void start()
	{
		if (isRunning())
		{
			return;
		}

		onBeforeStart();

		syncTickerToElapsed();
		mTickingPaused = false;

		mTicker.start();
		mStopWatch.start();

		onStart();
	}

	public final void stop()
	{
		if (!isRunning())
		{
			return;
		}

		onBeforeStop();

		mTickingPaused = false;
		mTicker.stop();

		mStopWatch.stop();

		onStop();
	}

	public final void reset()
	{
		stop();

		onBeforeReset();

		mStopWatch.reset();
		mTicker.reset();

		setTextToInitState();

		onReset();
	}

	public final void restart()
	{
		stop();
		reset();

		onRestart();

		start();
	}

	protected synchronized final void updateText(long millis)
	{
		final String timeString = mMillisFormatter.formatElapsedTime(millis);

		mLastDisplayedMs = millis;

		setText(timeString);
	}

	private void doTick()
	{
		onTickInternal();
		onTick();
	}

	void onTickInternal()
	{
	}

	//region State callback methods to override

	protected void onTick()
	{
	}

	protected void onStart()
	{
	}

	protected void onStop()
	{
	}

	protected void onRestart()
	{
	}

	protected void onReset()
	{
	}

	protected void onBeforeStart()
	{
	}

	protected void onBeforeStop()
	{
	}

	protected void onBeforeReset()
	{
	}

	//endregion

	private long countSyncTickerMove()
	{
		long interval = mTicker.getTickInterval();
		long remainingToNextTick = mTicker.getRemainingToNextTick();
		long elapsed = mStopWatch.getElapsedMs();
		long toNextIntervalPeriod = interval - (elapsed % interval);

		return toNextIntervalPeriod - remainingToNextTick;
	}

	private void syncTickerToElapsed()
	{
		mTicker.setNextTickMove(countSyncTickerMove());
	}

	//endregion

	//region Abstract methods

	protected abstract void setTextToInitState();

	/**
	 * Updates immediately timer text from current time of timer.
	 */
	public abstract void updateTextNow();

	//endregion

	//region Nested classes

	/**
	 * Factory used on creating of timer view to create tickers, which will synchronize ticks of timer views
	 */
	public interface ITickerFactory
	{
		Ticker createTicker(TickerFactoryParameters parameters);
	}

	public interface IMillisFormatterFactory
	{
		IMillisFormatter createFormatter(FormatterFactoryParameters parameters);
	}

	public static class TickerFactoryParameters
	{
		private final long mTickInterval;
		private final Ticker.OnTickListener mTickListener;

		public TickerFactoryParameters(Ticker.OnTickListener tickListener, long tickInterval)
		{
			mTickListener = tickListener;
			mTickInterval = tickInterval;
		}

		public long getTickInterval()
		{
			return mTickInterval;
		}

		public Ticker.OnTickListener getTickListener()
		{
			return mTickListener;
		}
	}

	public static class FormatterFactoryParameters
	{
		private final int mDisplayPrecisionValue;
		private final long mTickInterval;

		public FormatterFactoryParameters(int displayPrecisionValue, long mTickInterval)
		{
			mDisplayPrecisionValue = displayPrecisionValue;
			this.mTickInterval = mTickInterval;
		}

		public int getDisplayPrecisionValue()
		{
			return mDisplayPrecisionValue;
		}

		public long getTickInterval()
		{
			return mTickInterval;
		}
	}

	public static class DefaultTickerFactory implements ITickerFactory
	{
		@Override
		public Ticker createTicker(TickerFactoryParameters parameters)
		{
			return new HandlerTicker(parameters.mTickListener, parameters.mTickInterval);
		}
	}

	public static class DefaultMillisFormatterFactory implements IMillisFormatterFactory
	{
		@Override
		public IMillisFormatter createFormatter(FormatterFactoryParameters parameters)
		{
			IMillisFormatter coreFormatter = resolvePrecisionFormatter(parameters);
			return RoundingMillisDecoratorFormatter.decorateWithRounding(coreFormatter, parameters.getTickInterval());
		}

		public static IMillisFormatter resolvePrecisionFormatter(FormatterFactoryParameters parameters)
		{
			switch (parameters.mDisplayPrecisionValue)
			{
				case DISPLAY_PRECISION_FIT_TICK_INTERVAL:
					return resolveTickIntervalFitFormatter(parameters.mTickInterval);
				case DISPLAY_PRECISION_SECONDS:
					return new DateUtilsMillisFormatter();
				case DISPLAY_PRECISION_TENTHS:
					return new FastSplitSecondFormatter(SecondFractionDisplay.Tenth);
				case DISPLAY_PRECISION_HUNDREDTHS:
					return new FastSplitSecondFormatter(SecondFractionDisplay.Hundredth);
				case DISPLAY_PRECISION_MILLIS:
					return new FastSplitSecondFormatter(SecondFractionDisplay.Millis);

				default:
					throw new IllegalArgumentException();
			}
		}

		public static IMillisFormatter resolveTickIntervalFitFormatter(long tickInterval)
		{
			if (tickInterval < 10)
			{
				return new FastSplitSecondFormatter(SecondFractionDisplay.Millis);
			}

			if (tickInterval < 100)
			{
				return new FastSplitSecondFormatter(SecondFractionDisplay.Hundredth);
			}

			if (tickInterval < 1000)
			{
				return new FastSplitSecondFormatter(SecondFractionDisplay.Tenth);
			}

			return new DateUtilsMillisFormatter();
		}
	}

	//endregion
}
