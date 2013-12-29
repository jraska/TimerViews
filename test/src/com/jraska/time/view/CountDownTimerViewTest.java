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

import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.View;
import com.jraska.time.common.StartStopTestPart;
import com.jraska.time.demo.R;

import java.util.Calendar;
import java.util.TimeZone;

public class CountDownTimerViewTest extends AndroidTestCase
{
	//region Test methods

	public void testStartStopState() throws Exception
	{
		View view = LayoutInflater.from(getContext()).inflate(R.layout.countdowntimer_test, null);
		CountDownTimerView countDownTimerView = (CountDownTimerView) view;

		StartStopTestPart.doAllStateTests(countDownTimerView);
	}

	public void testAttrs() throws Exception
	{
		View view = LayoutInflater.from(getContext()).inflate(R.layout.countdowntimer_test, null);
		CountDownTimerView countDownTimerView = (CountDownTimerView) view;
		long countDownTime = countDownTimerView.getCountDownTime();

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.set(1970, 0, 1); //to eliminate day millis
		calendar.set(Calendar.HOUR_OF_DAY, 1);
		calendar.set(Calendar.MINUTE, 2);
		calendar.set(Calendar.SECOND, 15);
		calendar.set(Calendar.MILLISECOND, 100);

		long calendarTime = calendar.getTime().getTime();
		boolean timesMatch = calendarTime == countDownTime;

		assertTrue(String.format("Times are not same for 1:02:15.100 - expected: %d, but was: %d", calendarTime, countDownTime), timesMatch);
	}

	//endregion
}
