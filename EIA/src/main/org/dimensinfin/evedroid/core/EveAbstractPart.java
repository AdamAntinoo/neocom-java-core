//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.core;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.HashMap;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.render.DefaultRender;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.text.Html;
import android.text.Spanned;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class EveAbstractPart extends NeoComAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID					= -2988276062110424930L;
	private static final long									ONEMINUTE									= 60 * 1000;
	private static final long									ONEHOUR										= 60 * EveAbstractPart.ONEMINUTE;
	private static final long									ONEDAY										= 24 * EveAbstractPart.ONEHOUR;
	protected static DecimalFormat						keyFormatter							= new DecimalFormat("0000000");
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
		EveAbstractPart.securityLevels.put(10, "#2FEFEF");
		EveAbstractPart.securityLevels.put(9, "#48F0C0");
		EveAbstractPart.securityLevels.put(8, "#00EF47");
		EveAbstractPart.securityLevels.put(7, "#00F000");
		EveAbstractPart.securityLevels.put(6, "#8FEF2F");
		EveAbstractPart.securityLevels.put(5, "#EFEF00");
		EveAbstractPart.securityLevels.put(4, "#D77700");
		EveAbstractPart.securityLevels.put(3, "#F06000");
		EveAbstractPart.securityLevels.put(2, "#F04800");
		EveAbstractPart.securityLevels.put(1, "#D73000");
		EveAbstractPart.securityLevels.put(0, "#F00000");
	}

	public static String generateTimeString(final long millis) {
		try {
			DateTimeFormatterBuilder timeFormatter = new DateTimeFormatterBuilder();
			if (millis > EveAbstractPart.ONEDAY) timeFormatter.appendDayOfYear(1).appendLiteral("D ");
			if (millis > EveAbstractPart.ONEHOUR) timeFormatter.appendHourOfDay(2).appendLiteral(":");
			if (millis > EveAbstractPart.ONEMINUTE)
				timeFormatter.appendMinuteOfHour(2).appendLiteral(":").appendSecondOfMinute(2);
			return timeFormatter.toFormatter().print(new Instant(millis));
		} catch (RuntimeException rtex) {
			return "0:00";
		}
	}

	protected final DateTimeFormatter timePointFormatter = DateTimeFormat.forPattern("yyyy/MMM/dd HH:mm");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveAbstractPart(final AbstractComplexNode model) {
		super(model);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String generatePriceString(final double price, final boolean compress, final boolean addSuffix) {
		// Generate different formats depending on the quantity and the compress flag.
		// Get rid of negative numbers.
		if (compress) {
			if (Math.abs(price) > 1200000000.0) if (addSuffix)
				return EveAbstractPart.priceFormatter.format(price / 1000.0 / 1000.0 / 1000.0) + " B ISK";
			else
				return EveAbstractPart.priceFormatter.format(price / 1000.0 / 1000.0 / 1000.0);
			if (Math.abs(price) > 12000000.0) if (addSuffix)
				return EveAbstractPart.priceFormatter.format(price / 1000.0 / 1000.0) + " M ISK";
			else
				return EveAbstractPart.priceFormatter.format(price / 1000.0 / 1000.0);
		}
		if (addSuffix)
			return EveAbstractPart.priceFormatter.format(price) + " ISK";
		else
			return EveAbstractPart.priceFormatter.format(price);
	}

	protected Spanned colorFormatLocation(final EveLocation location) {
		StringBuffer htmlLocation = new StringBuffer();
		double security = location.getSecurityValue();
		htmlLocation.append(this.generateSecurityColor(security, EveAbstractPart.securityFormatter.format(security)));
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
		return this.generateDurationString(timer.getMillis());
	}

	protected String generateDurationString(final long millis) {
		DateTimeFormatterBuilder timeLeftCountdown = new DateTimeFormatterBuilder();
		if (millis > EveAbstractPart.ONEDAY) timeLeftCountdown.appendDayOfYear(1).appendLiteral("D ");
		if (millis > EveAbstractPart.ONEHOUR) timeLeftCountdown.appendHourOfDay(2).appendLiteral("H ");
		if (millis > EveAbstractPart.ONEMINUTE)
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
		secColor = EveAbstractPart.securityLevels.get(Long.valueOf(secAdjust).intValue());
		htmlFragmentWithColor.append("<font color='").append(secColor).append("'>").append(data).append("</font>");
		return htmlFragmentWithColor.toString();
	}

	protected NeoComCharacter getPilot() {
		return EVEDroidApp.getAppStore().getPilot();
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new DefaultRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
