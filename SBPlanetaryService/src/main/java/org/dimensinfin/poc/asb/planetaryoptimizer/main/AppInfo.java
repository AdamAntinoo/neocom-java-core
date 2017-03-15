package org.dimensinfin.poc.asb.planetaryoptimizer.main;

import java.io.Serializable;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

class AppInfo implements Serializable {
	private static final long	serialVersionUID	= -1339601625236133567L;
	private final String			name							= "BaseBookServiceApp";
	private final String			version						= "0.0.1";
	private final String			description				= "Simple and sample microservice that ever return a list of strings that are matchet to a list of books.";

	public String getCompilationDate() {
		return NeoComApplication.compilationDate.toString();
	}

	public String getName() {
		return name;
	}

	public String getRequests() {
		NeoComApplication.requests++;
		return Integer.toString(NeoComApplication.requests);
	}

	public String getRequestsTime() {
		NeoComApplication.requestsTime += new Long(new Double(Math.random() * 1000).intValue());
		return Long.toString(NeoComApplication.requestsTime);
	}

	public String getRunningTime() {
		DateTime startTime = new DateTime(NeoComApplication.startTimeinMillis);
		DateTime now = new DateTime();
		Period period = new Period(startTime, now);

		PeriodFormatter formatter = new PeriodFormatterBuilder().appendYears().appendSuffix("y ").appendMonths()
				.appendSuffix("m ").appendDays().appendSuffix("d ").appendHours().appendSuffix("h ").appendMinutes()
				.appendSuffix("m ").appendSeconds().appendSuffix("s").printZeroNever().toFormatter();
		String p2 = period.toString(formatter);
		return p2;
	}

	public String getStartupTime() {
		return NeoComApplication.startupTime.toString();
	}

}
// - UNUSED CODE ............................................................................................
