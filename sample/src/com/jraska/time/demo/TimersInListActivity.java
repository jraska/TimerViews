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

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.jraska.time.utils.StopWatch;
import com.jraska.time.view.TimerView;

import java.util.ArrayList;
import java.util.List;

public class TimersInListActivity extends ListActivity
{
	//region Fields

	private TestAdapter mTestAdapter;

	//endregion

	//region Activity overrides

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.simple_list);

		final List<Task> tasks = generateTestTasks();
		mTestAdapter = new TestAdapter(this, tasks);

		setListAdapter(mTestAdapter);

		final Toast toast = Toast.makeText(this, R.string.tap_task_to_toggle_run, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 100);
		toast.show();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		TimerView timerView = (TimerView) v.findViewById(R.id.listTimerView);

		Task task = mTestAdapter.getItem(position);
		if (task.isRunning())
		{
			task.stop();
			timerView.stop();
		}
		else
		{
			task.start();
			timerView.start();
		}
	}

	//endregion

	//region Methods

	static List<Task> generateTestTasks()
	{
		return generateTasks(15);
	}

	static List<Task> generateTasks(int count)
	{
		List<Task> tasks = new ArrayList<Task>(count);
		for (int taskIndex = 0; taskIndex < count; taskIndex++)
		{
			tasks.add(new Task("Task " + (taskIndex + 1)));
		}

		return tasks;
	}

	//endregion

	//region Nested classes

	static class TestAdapter extends ArrayAdapter<Task>
	{
		TestAdapter(Context context, List<Task> objects)
		{
			super(context, 0, objects);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
			{
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.timer_list_row, null);
			}

			Task task = getItem(position);

			final TextView titleTextView = (TextView) convertView.findViewById(R.id.title);
			titleTextView.setText(task.mTitle);

			TimerView timerView = (TimerView) convertView.findViewById(R.id.listTimerView);

			if (task.isRunning())
			{
				timerView.start();
			}
			else
			{
				timerView.stop();
			}

			timerView.setElapsedMs(task.getElapsedMs());
			timerView.updateTextNow();

			return convertView;
		}
	}

	static class Task
	{
		private final String mTitle;
		private final StopWatch mStopWatch = new StopWatch();

		Task(String title)
		{
			mTitle = title;
		}

		public boolean isRunning()
		{
			return mStopWatch.isRunning();
		}

		public long getElapsedMs()
		{
			return mStopWatch.getElapsedMs();
		}

		public void start()
		{
			mStopWatch.start();
		}

		public void stop()
		{
			mStopWatch.stop();
		}
	}

	//endregion
}
