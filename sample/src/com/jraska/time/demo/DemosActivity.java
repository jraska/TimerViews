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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DemosActivity extends ListActivity
{
	//region Static fields

	//integer is title resource
	private final Map<String, Class> mExampleActivities = new HashMap<String, Class>();

	//endregion

	//region Fields

	private ArrayAdapter<String> mAdapter;

	//endregion

	//region Activity overrides

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mExampleActivities.put(getString(R.string.timers_list), TimersInListActivity.class);
		mExampleActivities.put(getString(R.string.simple_timers), SimpleTimersActivity.class);
		mExampleActivities.put(getString(R.string.count_down_timers), CountDownTimersActivity.class);

		mAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, new ArrayList<String>(mExampleActivities.keySet()));

		setListAdapter(mAdapter);
	}

	//endregion

	//region ListActivity overrides

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		final String text = mAdapter.getItem(position);
		Class activityClass = mExampleActivities.get(text);

		Intent activityIntent = new Intent(this, activityClass);
		startActivity(activityIntent);
	}

	//endregion
}
