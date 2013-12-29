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

package com.jraska.time.demo;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.TimePicker;
import com.jraska.time.view.CountDownTimerView;
import com.jraska.time.view.TimerView;

public class TimersActivity extends Activity
{
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_timers);

		final TimerView timerView = (TimerView) findViewById(R.id.timerView);
		final CountDownTimerView countDownTimerView = (CountDownTimerView) findViewById(R.id.countDownTimerView);

		findViewById(R.id.btnStart).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				timerView.start();
			}
		});

		findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				timerView.stop();
			}
		});

		findViewById(R.id.btnReset).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				timerView.reset();
			}
		});

		findViewById(R.id.btnStartCountDown).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				countDownTimerView.start();
			}
		});

		findViewById(R.id.btnStopCountDown).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				countDownTimerView.stop();
			}
		});

		findViewById(R.id.btnResetCountDown).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				countDownTimerView.reset();
			}
		});

		findViewById(R.id.btnStartTrace).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Debug.startMethodTracing("TimersTrace", 1024 * 1024 * 24);
			}
		});

		findViewById(R.id.btnStopTrace).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Debug.stopMethodTracing();
			}
		});

		findViewById(R.id.btnSetCountDown).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				setCountDownTime(countDownTimerView);
			}
		});
	}

	private void setCountDownTime(final CountDownTimerView countDownTimerView)
	{
		TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
		{
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute)
			{
				long countDownMillis = (hourOfDay * 60 + minute) * 60 * 1000;
				countDownTimerView.setCountDownTime(countDownMillis);
			}
		}, 0, 0, true);

		timePickerDialog.show();
	}
}
