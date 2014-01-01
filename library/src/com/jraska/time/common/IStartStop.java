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

package com.jraska.time.common;

/**
 * Implementations can stop their execution, resume it and start again from beginning
 */
public interface IStartStop
{
	//region Properties

	/**
	 * Returns true if start() or restart() was called and no reset() or stop() afterwards
	 *
	 * @return Boolean indicating if the IStartStop is executing something now.
	 */
	boolean isRunning();

	//endregion

	//region Methods

	/**
	 * Starts execution of IStartStop or resumes its execution after stop.
	 * Does nothing if already running.
	 */
	void start();

	/**
	 * Stops execution of IStartStop. Does nothing if not running.
	 */
	void stop();

	/**
	 * Stops execution if running and moves IStartStop to initial state.
	 */
	void reset();

	/**
	 * Stops execution if running, moves to initial state and start execution from beginning.
	 */
	void restart();

	//endregion
}
