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

public class FastSplitSecondFormatter extends AbstractSplitSecondFormatter implements IMillisFormatter
{
	//region Constants

	protected static final char TIME_PADDING = '0';
	protected static final char TIME_SEPARATOR = ':';
	protected static final char FRACTION_SEPARATOR = '.';

	//endregion

	//region Fields

	private final StringBuilder mRecycleBuilder = new StringBuilder(10);
	private final int mDecimalPlaces;
	private final int mDivisor;
	private final char[] mRecycleSecondFraction;

	//endregion

	//region Constructors

	public FastSplitSecondFormatter(SecondFractionDisplay secondFractionDisplay)
	{
		if (secondFractionDisplay == null)
		{
			throw new IllegalArgumentException("secondFractionDisplay cannot be null");
		}

		mDecimalPlaces = secondFractionDisplay.getDecimalPlaces();
		mDivisor = secondFractionDisplay.getDivisor();

		mRecycleSecondFraction = new char[mDecimalPlaces];
	}

	//endregion

	//region AbstractSplitSecondFormatter implementation

	@Override
	protected String format(long hours, long minutes, long seconds, long millis)
	{
		StringBuilder sb = mRecycleBuilder;
		sb.setLength(0);

		if (hours > 0)
		{
			sb.append(hours);
			sb.append(TIME_SEPARATOR);
		}

		if (minutes < 10)
		{
			sb.append(TIME_PADDING);
		}
		else
		{
			sb.append(toDigitChar(minutes / 10));
		}
		sb.append(toDigitChar(minutes % 10));
		sb.append(TIME_SEPARATOR);
		if (seconds < 10)
		{
			sb.append(TIME_PADDING);
		}
		else
		{
			sb.append(toDigitChar(seconds / 10));
		}
		sb.append(toDigitChar(seconds % 10));

		sb.append(FRACTION_SEPARATOR);

		addSplitSecond(sb, (int) millis);

		return sb.toString();
	}

	private void addSplitSecond(StringBuilder sb, int millis)
	{
		int workingMillis = millis / mDivisor;

		for (int i = mDecimalPlaces - 1; i > -1; i--)
		{
			mRecycleSecondFraction[i] = toDigitChar(workingMillis % 10);
			workingMillis /= 10;
		}

		sb.append(mRecycleSecondFraction);
	}

	//endregion

	//region Static methods

	private static char toDigitChar(long digit)
	{
		return (char) (digit + '0');
	}

	//endregion
}
