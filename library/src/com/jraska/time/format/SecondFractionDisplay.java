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

public enum SecondFractionDisplay
{
	//region Enum members

	Tenth(100, 1),
	Hundredth(10, 2),
	Millis(1, 3);

	//endregion

	//region Fields

	private final int mDivisor; //value which will divide milliseconds to get requested precision
	private final int mDecimalPlaces;

	//endregion

	//region Constructors

	private SecondFractionDisplay(int divisor, int decimalPlaces)
	{
		mDivisor = divisor;
		mDecimalPlaces = decimalPlaces;
	}

	//endregion

	//region Properties

	public int getDecimalPlaces()
	{
		return mDecimalPlaces;
	}

	public int getDivisor()
	{
		return mDivisor;
	}

	//endregion
}
