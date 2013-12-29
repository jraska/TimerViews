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
 * Reference formatter which will act as reference to test other formatters to others.
 * <p/>
 * Uses slow but easy String.format method.
 */
class ReferenceFormatter implements IMillisFormatter
{
	//region Fields

	private final int mFractionDecimalPlaces;
	private final String mMillisFormat;

	//endregion

	//region Constructors

	ReferenceFormatter(final int fractionDecimalPlaces)
	{
		if (fractionDecimalPlaces > 3 || fractionDecimalPlaces < 1)
		{
			throw new IllegalArgumentException("Fraction decimal places must be from 1 to 3");
		}

		mFractionDecimalPlaces = fractionDecimalPlaces;
		mMillisFormat = "%1$s.%2$0" + fractionDecimalPlaces + "d";
	}

	//endregion

	//region IMillisFormatter impl

	@Override
	public String formatElapsedTime(long totalMs)
	{
		String dateUtilsValue = DateUtils.formatElapsedTime(totalMs / 1000);

		long fraction = totalMs % 1000;

		for (int i = 0; i < 3 - mFractionDecimalPlaces; i++)
		{
			fraction /= 10;
		}

		return String.format(mMillisFormat, dateUtilsValue, fraction);
	}

	//endregion
}
