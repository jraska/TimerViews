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
