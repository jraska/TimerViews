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

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import com.jraska.time.common.IStartStop;
import com.jraska.time.view.CountDownTimerView;

public class CountDownTimersActivity extends BaseTimersExampleActivity
{
	//region Activity overrides

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.count_down_timers);

		setupTickTackCountDownTimerView();
		setupStartStopControls(R.id.countDownTimerView_2, R.id.countDownTimerView_2_Controls);
		setupStartStopControls(R.id.countDownTimerView_3, R.id.countDownTimerView_3_Controls);
		setupStartStopControls(R.id.countDownTimerView_4, R.id.countDownTimerView_4_Controls);
	}

	//endregion

	//region BaseTimersExampleActivity overrides

	@Override
	protected void setupStartStop(IStartStop startStop, View controlsView)
	{
		super.setupStartStop(startStop, controlsView);

		final CountDownTimerView countDownTimerView = (CountDownTimerView) startStop;
		controlsView.findViewById(R.id.btnSetCountDown).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setCountDownTime(countDownTimerView);
			}
		});
	}

	//endregion

	//region Methods

	private void setupTickTackCountDownTimerView()
	{
		CountDownTimerView countDownTimerView = (CountDownTimerView) findViewById(R.id.countDownTimerView_1);
		setupStartStopControls(countDownTimerView, R.id.countDownTimerView_1_Controls);

		final TextView tickTackText = (TextView) findViewById(R.id.tickTackText);
		countDownTimerView.setOnCountDownListener(new CountDownTimerView.OnCountDownListener()
		{
			private boolean mTick = true;

			@Override
			public void onCountDownTick(CountDownTimerView countDownTimerView, long remainingMillis)
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

			@Override
			public void onFinish(CountDownTimerView countDownTimerView)
			{
				tickTackText.setText(getString(R.string.finished));
			}
		});
	}

	private void setCountDownTime(final CountDownTimerView countDownTimerView)
	{
		TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
		{
			//hours are taken as minutes and minutes as seconds
			@Override
			public void onTimeSet(TimePicker view, int minute, int second)
			{
				long countDownMillis = (minute * 60 + second) * 1000;
				countDownTimerView.setCountDownTime(countDownMillis);
			}
		}, 0, 0, true);
		timePickerDialog.setTitle(getString(R.string.minutes_and_seconds));

		timePickerDialog.show();
	}

	//endregion
}
