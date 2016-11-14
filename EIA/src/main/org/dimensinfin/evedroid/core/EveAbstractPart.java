//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.HashMap;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.core.model.AbstractGEFNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.model.EveChar;
import org.dimensinfin.evedroid.model.EveLocation;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.text.Html;
import android.text.Spanned;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class EveAbstractPart extends AbstractAndroidPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID					= -2988276062110424930L;
	private static final long									ONEMINUTE									= 60 * 1000;
	private static final long									ONEHOUR										= 60 * ONEMINUTE;
	private static final long									ONEDAY										= 24 * ONEHOUR;
	protected static DecimalFormat						keyFormatter							= new DecimalFormat("0000000");
	protected final DateTimeFormatter					timePointFormatter				= DateTimeFormat.forPattern("yyyy/MMM/dd HH:mm");
	protected static DecimalFormat						priceFormatter						= new DecimalFormat("###,###.00");
	protected static DecimalFormat						qtyFormatter							= new DecimalFormat("###,##0");
	protected static DecimalFormat						moduleIndexFormatter			= new DecimalFormat("000");
	protected static DecimalFormat						moduleMultiplierFormatter	= new DecimalFormat("x##0.0");
	protected static DecimalFormat						itemCountFormatter				= new DecimalFormat("###,##0");
	protected static DecimalFormat						iskFormatter							= new DecimalFormat("###,##0.0# MISK");
	protected static DecimalFormat						volumeFormatter						= new DecimalFormat("###,##0.0");
	protected static DecimalFormat						securityFormatter					= new DecimalFormat("0.0");
	protected static HashMap<Integer, String>	securityLevels						= new HashMap<Integer, String>();
	static {
		securityLevels.put(10, "#2FEFEF");
		securityLevels.put(9, "#48F0C0");
		securityLevels.put(8, "#00EF47");
		securityLevels.put(7, "#00F000");
		securityLevels.put(6, "#8FEF2F");
		securityLevels.put(5, "#EFEF00");
		securityLevels.put(4, "#D77700");
		securityLevels.put(3, "#F06000");
		securityLevels.put(2, "#F04800");
		securityLevels.put(1, "#D73000");
		securityLevels.put(0, "#F00000");
	}

	public static String generateTimeString(final long millis) {
		try {
			DateTimeFormatterBuilder timeFormatter = new DateTimeFormatterBuilder();
			if (millis > ONEDAY) timeFormatter.appendDayOfYear(1).appendLiteral("D ");
			if (millis > ONEHOUR) timeFormatter.appendHourOfDay(2).appendLiteral(":");
			if (millis > ONEMINUTE) timeFormatter.appendMinuteOfHour(2).appendLiteral(":").appendSecondOfMinute(2);
			return timeFormatter.toFormatter().print(new Instant(millis));
		} catch (RuntimeException rtex) {
			return "0:00";
		}
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveAbstractPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String generatePriceString(final double price, final boolean compress, final boolean addSuffix) {
		// Generate different formats depending on the quantity and the compress flag.
		// Get rid of negative numbers.
		if (compress) {
			if (Math.abs(price) > 1200000000.0) if (addSuffix)
				return priceFormatter.format(price / 1000.0 / 1000.0 / 1000.0) + " B ISK";
			else
				return priceFormatter.format(price / 1000.0 / 1000.0 / 1000.0);
			if (Math.abs(price) > 12000000.0) if (addSuffix)
				return priceFormatter.format(price / 1000.0 / 1000.0) + " M ISK";
			else
				return priceFormatter.format(price / 1000.0 / 1000.0);
		}
		if (addSuffix)
			return priceFormatter.format(price) + " ISK";
		else
			return priceFormatter.format(price);
	}

	protected Spanned colorFormatLocation(final EveLocation location) {
		StringBuffer htmlLocation = new StringBuffer();
		double security = location.getSecurityValue();
			htmlLocation.append(generateSecurityColor(security, securityFormatter.format(security)));
		htmlLocation.append(" ").append(location.getRegion()).append("-").append(location.getStation());
		return Html.fromHtml(htmlLocation.toString());
	}

	protected String generateDateString(final long millis) {
		try {
			DateTimeFormatterBuilder dateFormatter = new DateTimeFormatterBuilder();
			dateFormatter.appendYear(4, 4).appendLiteral(".");
			dateFormatter.appendMonthOfYear(2).appendLiteral(".");
			dateFormatter.appendDayOfMonth(2).appendLiteral(" ");
			dateFormatter.appendHourOfDay(2).appendLiteral(":");
			dateFormatter.appendMinuteOfHour(2).appendLiteral(":").appendSecondOfMinute(2);
			return dateFormatter.toFormatter().print(new Instant(millis));
		} catch (RuntimeException rtex) {
			return "2014 01/01 00:00:00";
		}
	}

	/**
	 * Accepts a <code>Duration</code> as a parameter and returns an string with the formatted duration in the
	 * pattern <b>9D 99H 99M 99S</b> depending on the real duration of the object.
	 * 
	 * @return
	 */
	protected String generateDurationString(final Instant timer) {
		return generateDurationString(timer.getMillis());
	}

	protected String generateDurationString(final long millis) {
		DateTimeFormatterBuilder timeLeftCountdown = new DateTimeFormatterBuilder();
		if (millis > ONEDAY) timeLeftCountdown.appendDayOfYear(1).appendLiteral("D ");
		if (millis > ONEHOUR) timeLeftCountdown.appendHourOfDay(2).appendLiteral("H ");
		if (millis > ONEMINUTE)
			timeLeftCountdown.appendMinuteOfHour(2).appendLiteral("M ").appendSecondOfMinute(2).appendLiteral('S');
		return timeLeftCountdown.toFormatter().print(new Instant(millis));
	}

	protected String generateSecurityColor(double sec, final String data) {
		StringBuffer htmlFragmentWithColor = new StringBuffer();
		String secColor = "#F00000";
		// Get the color from the table.
		if (sec < 0.0) sec = 0.0;
		if (sec > 1.0) sec = 1.0;
		long secAdjust = Long.valueOf(Math.round(sec * 10.0)).intValue();
		secColor = securityLevels.get(Long.valueOf(secAdjust).intValue());
		htmlFragmentWithColor.append("<font color='").append(secColor).append("'>").append(data).append("</font>");
		return htmlFragmentWithColor.toString();
	}

	protected EveChar getPilot() {
		return EVEDroidApp.getAppStore().getPilot();
	}
}

// - UNUSED CODE ............................................................................................
