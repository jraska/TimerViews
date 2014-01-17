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

package com.jraska.time.demo;

import android.app.Activity;
import android.view.View;
import com.jraska.time.common.IStartStop;

public class BaseTimersExampleActivity extends Activity
{
	//region Methods

	protected void setupStartStopControls(int startStopRes, int controlsRes)
	{
		final IStartStop startStop = (IStartStop) findViewById(startStopRes);
		setupStartStopControls(startStop, controlsRes);
	}

	protected void setupStartStopControls(IStartStop startStop, int controlsRes)
	{
		View controlsOne = findViewById(controlsRes);
		setupStartStop(startStop, controlsOne);
	}

	protected void setupStartStop(final IStartStop startStop, View controlsView)
	{
		controlsView.findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startStop.start();
			}
		});

		controlsView.findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startStop.stop();
			}
		});

		controlsView.findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startStop.reset();
			}
		});
	}

	//endregion
}
