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

import android.os.SystemClock;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class RoundingMillisDecoratorFormatterTest extends TestCase
{
	//region Test methods

	public void testRoundingIncrement10() throws Exception
	{
		//some hard coded test values
		long[][] testData = {{10, 10}, {126896371, 126896370}, {3562562, 3562560}, {363, 360}, {774, 770}, {1455, 1460}, {6, 10}, {92727, 92730}, {28, 30}, {367678299, 367678300}};
		final List<RoundTestEntry> testEntries = toEntryList(testData);

		RoundingMillisDecoratorFormatter decoratorFormatter = newDecoratorFormatter(10);

		testDecoratorRounding(testEntries, decoratorFormatter);
	}

	public void testRoundingIncrement20() throws Exception
	{
		testRoundingIncrement(20);
	}

	public void testRoundingIncrement50() throws Exception
	{
		testRoundingIncrement(50);
	}

	public void testRoundingIncrement100() throws Exception
	{
		testRoundingIncrement(100);
	}

	public void testRoundingIncrement1000() throws Exception
	{
		testRoundingIncrement(1000);
	}

	//endregion

	//region Static methods

	private static void testRoundingIncrement(long increment)
	{
		final List<RoundTestEntry> entries = generateTestData(increment, (SystemClock.elapsedRealtime() / increment) * increment);

		testDecoratorRounding(entries, newDecoratorFormatter(increment));
	}

	private static void testDecoratorRounding(List<RoundTestEntry> testEntries, RoundingMillisDecoratorFormatter decoratorFormatter)
	{
		for (RoundTestEntry testEntry : testEntries)
		{
			long rounded = decoratorFormatter.countNewMillis(testEntry.mValue);

			String errorMessage = String.format("Rounded and expected values do not match: Rounded: %d != Expected: %d", rounded, testEntry.mExpectedValue);
			assertEquals(errorMessage, testEntry.mExpectedValue, rounded);
		}
	}

	private static List<RoundTestEntry> generateTestData(long increment, long baseNumber)
	{
		if (baseNumber % increment != 0)
		{
			throw new IllegalArgumentException("Base number must be divided with increment with no remainder.");
		}

		List<RoundTestEntry> generatedEntries = new ArrayList<RoundTestEntry>((int) increment);
		for (long number = baseNumber - (increment / 2), finalNumber = baseNumber + (increment / 2); number < finalNumber; number++)
		{
			generatedEntries.add(new RoundTestEntry(number, baseNumber));
		}

		return generatedEntries;
	}

	private static List<RoundTestEntry> toEntryList(long[][] data)
	{
		List<RoundTestEntry> entries = new ArrayList<RoundTestEntry>(data.length);

		for (long[] rawData : data)
		{
			entries.add(new RoundTestEntry(rawData[0], rawData[1]));
		}

		return entries;
	}

	private static RoundingMillisDecoratorFormatter newDecoratorFormatter(long increment)
	{
		return new RoundingMillisDecoratorFormatter(new ReferenceFormatter(1), increment);
	}

	//endregion

	//region Inner classes

	private static class RoundTestEntry
	{
		private final long mValue;
		private final long mExpectedValue;

		private RoundTestEntry(long value, long expectedValue)
		{
			mValue = value;
			mExpectedValue = expectedValue;
		}

		@Override
		public String toString()
		{
			return "Value: " + mValue + " Expected after rounding: " + mExpectedValue;
		}
	}

	//endregion
}
