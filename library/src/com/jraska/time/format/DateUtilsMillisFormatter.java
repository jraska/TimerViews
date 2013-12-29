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

package com.jraska.time.format;

import android.text.format.DateUtils;

/**
 * Formatter to format only to full seconds to format mm:ss or h:mm:ss if necessary.
 */
public final class DateUtilsMillisFormatter implements IMillisFormatter
{
	//region Fields

	private final StringBuilder mRecycleBuilder = new StringBuilder(8);

	//endregion

	//region IMillisFormatter implementation

	@Override
	public String formatElapsedTime(long totalMs)
	{
		long seconds = totalMs / 1000;

		return DateUtils.formatElapsedTime(mRecycleBuilder, seconds);
	}

	//endregion
}
