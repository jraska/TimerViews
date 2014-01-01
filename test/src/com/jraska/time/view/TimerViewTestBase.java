package com.jraska.time.view;

import android.os.Looper;
import android.test.AndroidTestCase;
import android.view.LayoutInflater;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public abstract class TimerViewTestBase extends AndroidTestCase
{
	//region Constants

	public static final long TOLERANCE_MS = 2;

	//endregion

	//region Fields

	private AbstractTimerView mTestView;
	private Looper mViewLooper;

	//endregion

	//region Properties

	public AbstractTimerView getTestView()
	{
		return mTestView;
	}

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

				mTestView = (AbstractTimerView) LayoutInflater.from(getContext()).inflate(getLayoutResId(), null);
				mViewLooper = Looper.myLooper();

				workerThreadSetupLatch.countDown();

				Looper.loop();
			}
		};

		Thread workerThread = new Thread(workerRunnable);
		workerThread.start();

		//wait for thread to setup
		final boolean threadInitializedOk = workerThreadSetupLatch.await(500, TimeUnit.MILLISECONDS);
		assertTrue("Worker thread for TimerView test was not initialized in time.", threadInitializedOk);
	}

	@Override
	protected void tearDown() throws Exception
	{
		mTestView.stop();
		mViewLooper.quit();
	}

	//endregion

	//region Methods

	protected abstract int getLayoutResId();

	//endregion
}
