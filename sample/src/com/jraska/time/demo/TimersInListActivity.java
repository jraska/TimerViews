package com.jraska.time.demo;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		TimerView timerView = (TimerView) v.findViewById(R.id.timerView);

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

			TimerView timerView = (TimerView) convertView.findViewById(R.id.timerView);

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
