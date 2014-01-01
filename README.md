Timer Views
===

Library to easily display running time in your apps.

Features
---
* Displaying time with precision to seconds, tenths, hundredths of second or milliseconds.
* Updating displayed time on specified interval.
* Receiving callbacks on every update of time.
* CountDown view to show count down.
* Changing time values and update intervals at runtime.

Setup
---
* In Intellij or Eclipse, just import the library as an Android library project.
Then, just add TimerViews as a dependency to your existing project and you're ready to go!

Example
---
*coming soon*

API Overview
---

* **Views**
	* *Custom views to display time values - core part of library*
	* **`TimerView`** - View to display and update running time.
	* **`CountDownTimerView`** - View to display and update countdown time with stopping on zero.
	* `AbstractTimerView` - Base class for time showing views
	* *Both views supply custom time formatting and are parametrizable to update frequency, time format etc.*

* MillisFormatters
	* *All running time in views is handled with milliseconds and millis formatters format those milliseconds to human readable form.*
	* `DateUtilsMillisFormatter` - format millis to full seconds with second precision via Android DateUtils class.
	* `FastSplitSecondFormatter` - format millis and split seconds - tenths, hundredths or milliseconds itself.
	* `RoundingMillisDecoratorFormatter` - decorates some other formatter to work with rounded values to avoid values like 999ms  1001 ms etc.

* Utilities
	* *Few handy class to perform some time operations.*
	* `Ticker` - Abstract for classes performing some callback on time interval, allowing changes at runtime, moving time and more.
	* `HandlerTicker` - Ticker using `android.os.Handler` messages for its internal synchronization.
	* `StopWatch` - Measuring elapsed time.
