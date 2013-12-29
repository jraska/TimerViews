ChangeLog
===

v1.0 *(30.12.2013)*
---

*First release published.*
---

Start API contains
---

* **Views**
	* *Custom views to display time values - core part of library*
	* **`TimerView`** - View to display and update running time.
	* **`CountDownTimerView`** - View to display and update countdown time with stopping on zero.
	* `AbstractTimerView` - Base class for time showing views
	* *Both views supply custom time formatting and are parametrizable to update frequency, time format etc.*

* **MillisFormatters**
	* *All running time in views is handled with millisecond and millis formatters format those millisecond to human readable form.*
	* `DateUtilsMillisFormatter` - format millis to full seconds with second precision via Android DateUtils class.
	* `FastSplitSecondFormatter` - format millis and split seconds - tenths, hundredths or milliseconds itself.
	* `RoundingMillisDecoratorFormatter` - decorate some other formatter to work with rounded values to avoid values like 999ms  1001 ms etc.

* **Utilities**
	* *Few handy class to perform some time operations.*
	* `Ticker` - Abstract for classes performing some callback on time interval, allowing changes at runtime, moving time and more.
	* `HandlerTicker` - Ticker using `android.os.Handler` messages for its internal synchronization.
	* `StopWatch` - Measuring elapsed time.
