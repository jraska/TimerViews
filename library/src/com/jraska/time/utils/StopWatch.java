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

import android.os.SystemClock;
import com.jraska.time.common.IStartStop;

public final class StopWatch implements IStartStop
{
	//region Fields

	private long m_elapsedMs = 0;
	private long m_lastMs = 0;
	private boolean m_running = false;

	//endregion

	//region Properties

	public boolean isRunning()
	{
		return m_running;
	}

	public long getElapsedMs()
	{
		if (m_running)
		{
			long systemMs = SystemClock.elapsedRealtime();
			return m_elapsedMs + (systemMs - m_lastMs);
		}
		return m_elapsedMs;
	}

	//endregion

	//region Methods

	public void start()
	{
		//do nothing if the stopwatch is already running
		if (m_running)
		{
			return;
		}
		m_running = true;

		//get the time information as last part for better precision
		m_lastMs = SystemClock.elapsedRealtime();
	}

	public void stop()
	{
		//get the time information as first part for better precision
		long systemMs = SystemClock.elapsedRealtime();

		//if it was already stopped or did not even run, do nothing
		if (!m_running)
		{
			return;
		}

		m_elapsedMs += (systemMs - m_lastMs);

		m_running = false;
	}

	public void reset()
	{
		m_running = false;
		m_elapsedMs = 0;
		m_lastMs = 0;
	}

	public void restart()
	{
		stop();
		reset();
		start();
	}

	//endregion

	//region Object implementation

	@Override
	public String toString()
	{
		return "Stopwatch: " + "Elapsed millis: " + getElapsedMs() + (m_running ? " Running" : " Not running");
	}

	//endregion
}
