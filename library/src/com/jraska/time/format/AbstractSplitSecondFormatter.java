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

public abstract class AbstractSplitSecondFormatter implements IMillisFormatter
{
	//region Constants

	private static final long MILLIS_IN_SECOND = 1000;
	private static final long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;
	private static final long MILLIS_IN_HOUR = 60 * MILLIS_IN_MINUTE;

	//endregion

	//region IMillisFormatter implementation

	@Override
	public final String formatElapsedTime(long totalMs)
	{
		final long hours = totalMs / MILLIS_IN_HOUR;

		long remainingMillis = totalMs;
		remainingMillis -= hours * MILLIS_IN_HOUR;

		final long minutes = remainingMillis / MILLIS_IN_MINUTE;
		remainingMillis -= minutes * MILLIS_IN_MINUTE;

		final long seconds = remainingMillis / MILLIS_IN_SECOND;
		final long millis = (remainingMillis % MILLIS_IN_SECOND);

		return format(hours, minutes, seconds, millis);
	}

	//endregion

	//region Abstract methods

	protected abstract String format(long hours, long minutes, long seconds, long millis);

	//endregion
}
