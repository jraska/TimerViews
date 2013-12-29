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

import junit.framework.TestCase;

import java.util.Random;

public class FastSplitSecondFormatterTest extends TestCase
{
	//region Constants

	protected static final int TEST_ITERATIONS = 1000;

	//endregion

	//region Tests

	public void testTenths() throws Exception
	{
		FastSplitSecondFormatter fastSplitSecondFormatter = new FastSplitSecondFormatter(SecondFractionDisplay.Tenth);
		ReferenceFormatter referenceFormatter = new ReferenceFormatter(1);

		runTestFormatter(fastSplitSecondFormatter, referenceFormatter);
	}

	public void testHundredths() throws Exception
	{
		FastSplitSecondFormatter fastSplitSecondFormatter = new FastSplitSecondFormatter(SecondFractionDisplay.Hundredth);
		ReferenceFormatter referenceFormatter = new ReferenceFormatter(2);

		runTestFormatter(fastSplitSecondFormatter, referenceFormatter);
	}

	public void testMillis() throws Exception
	{
		FastSplitSecondFormatter fastSplitSecondFormatter = new FastSplitSecondFormatter(SecondFractionDisplay.Millis);
		ReferenceFormatter referenceFormatter = new ReferenceFormatter(3);

		runTestFormatter(fastSplitSecondFormatter, referenceFormatter);
	}

	public static void runTestFormatter(FastSplitSecondFormatter formatter, ReferenceFormatter referenceFormatter)
	{
		Random random = new Random();
		for (int i = 0, count = TEST_ITERATIONS; i < count; i++)
		{
			long ms = Math.abs(random.nextInt());

			final String formatted = formatter.formatElapsedTime(ms);
			final String reference = referenceFormatter.formatElapsedTime(ms);

			assertEquals(String.format("Times are not same! %s != %s", formatted, reference), formatted, reference);
		}
	}

	//endregion
}
