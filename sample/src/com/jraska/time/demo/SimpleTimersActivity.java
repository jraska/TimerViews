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

import android.os.Bundle;
import android.widget.TextView;
import com.jraska.time.view.TimerView;

public class SimpleTimersActivity extends BaseTimersExampleActivity
{
	//region Activity overrides

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.simple_timers);

		setupTickTackTimerView();
		setupStartStopControls(R.id.timerView_2, R.id.timerView_2_Controls);
		setupStartStopControls(R.id.timerView_3, R.id.timerView_3_Controls);
		setupStartStopControls(R.id.timerView_4, R.id.timerView_4_Controls);
	}

	//endregion

	//region Methods

	private void setupTickTackTimerView()
	{
		final TimerView timerView = (TimerView) findViewById(R.id.timerView_1);
		setupStartStopControls(timerView, R.id.timerView_1_Controls);

		final TextView tickTackText = (TextView) findViewById(R.id.tickTackText);
		timerView.setOnTimerTickListener(new TimerView.OnTimerTickListener()
		{
			private boolean mTick = true;

			@Override
			public void onTick(TimerView timerView, long elapsedMillis)
			{
				if (mTick)
				{
					tickTackText.setText(getString(R.string.tick));
				}
				else
				{
					tickTackText.setText(getString(R.string.tack));
				}

				mTick = !mTick;
			}
		});
	}

	//endregion
}