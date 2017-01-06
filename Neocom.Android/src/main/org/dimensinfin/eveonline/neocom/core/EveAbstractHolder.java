//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.core;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.HashMap;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.widget.ImageView;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class EveAbstractHolder extends AbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	protected static final long											ONEMINUTE									= 60 * 1000;
	protected static final long											ONEHOUR										= 60 * EveAbstractHolder.ONEMINUTE;
	protected static final long											ONEDAY										= 24 * EveAbstractHolder.ONEHOUR;
	protected static final DecimalFormat						keyFormatter							= new DecimalFormat("0000000");
	protected static final DateTimeFormatter				durationFormatter					= DateTimeFormat.forPattern("d HH:mm");
	protected static final DateTimeFormatter				timePointFormatter				= DateTimeFormat
			.forPattern("YYYY.MM.dd HH:mm");
	protected static final DateTimeFormatter				jobTimeFormatter					= DateTimeFormat.forPattern("D HH:MM");
	protected static final DecimalFormat						priceFormatter						= new DecimalFormat("###,###.00");
	protected static final DecimalFormat						qtyFormatter							= new DecimalFormat("###,##0");
	protected static final DecimalFormat						moduleIndexFormatter			= new DecimalFormat("000");
	protected static final DecimalFormat						queueIndexFormatter				= new DecimalFormat("00");
	protected static final DecimalFormat						moduleMultiplierFormatter	= new DecimalFormat("x##0.0");
	protected static final DecimalFormat						itemCountFormatter				= new DecimalFormat("###,##0");
	protected static final DecimalFormat						volumeFormatter						= new DecimalFormat("###,##0.0");
	protected static final DecimalFormat						securityFormatter					= new DecimalFormat("0.0");
	protected static final HashMap<Integer, String>	securityLevels						= new HashMap<Integer, String>();
	static {
		EveAbstractHolder.securityLevels.put(10, "#2FEFEF");
		EveAbstractHolder.securityLevels.put(9, "#48F0C0");
		EveAbstractHolder.securityLevels.put(8, "#00EF47");
		EveAbstractHolder.securityLevels.put(7, "#00F000");
		EveAbstractHolder.securityLevels.put(6, "#8FEF2F");
		EveAbstractHolder.securityLevels.put(5, "#EFEF00");
		EveAbstractHolder.securityLevels.put(4, "#D77700");
		EveAbstractHolder.securityLevels.put(3, "#F06000");
		EveAbstractHolder.securityLevels.put(2, "#F04800");
		EveAbstractHolder.securityLevels.put(1, "#D73000");
		EveAbstractHolder.securityLevels.put(0, "#F00000");
	}
	public static Typeface daysFace = Typeface
			.createFromAsset(NeoComApp.getSingletonApp().getApplicationContext().getAssets(), "fonts/Days.otf");

	// - F I E L D - S E C T I O N ............................................................................
	// protected ITheme _theme = null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public EveAbstractHolder(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
		// _theme = EVEDroidApp.getAppContext().getTheme();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Typeface getThemeTextFont() {
		return EveAbstractHolder.daysFace;
	}

	protected Spanned colorFormat(final String data, final String color, final String suffix) {
		final StringBuffer htmlFragment = new StringBuffer();
		htmlFragment.append("<font color='").append(color).append("'>");
		htmlFragment.append(data);
		if (null != suffix) {
			htmlFragment.append(suffix);
		}
		htmlFragment.append("</font>");
		return Html.fromHtml(htmlFragment.toString());
	}

	protected Spanned colorFormatLocation(final EveLocation location) {
		final StringBuffer htmlLocation = new StringBuffer();
		final double security = location.getSecurityValue();
		htmlLocation.append(this.generateSecurityColor(security, EveAbstractHolder.securityFormatter.format(security)));
		htmlLocation.append(" ").append(location.getRegion()).append("-").append(location.getName());
		return Html.fromHtml(htmlLocation.toString());
	}

	@Override
	abstract protected void createView();

	/**
	 * The price of the manufacture cost on the output is not tinted with the right color. Use red for cero or
	 * negative costs, orange for less than 10% and white for higher (or maybe green for higher). This code can
	 * be refactored to use ever the same generation code.
	 * 
	 * @param cost
	 * @param price
	 * @param suffix
	 * @param compress
	 * @return
	 */
	protected Spanned displayManufactureCost(final double cost, final double price, final boolean compress,
			final boolean suffix) {
		final StringBuffer htmlFragmentWithColor = new StringBuffer();
		final String priceString = this.generatePriceString(cost, compress, suffix);
		String color = "#FFFFFF";
		if (cost >= (price * 0.9)) {
			color = "#FFA500";
		}
		if (cost >= price) {
			color = "#F00000";
		}
		htmlFragmentWithColor.append("<font color='").append(color).append("'>").append(priceString).append("</font>");
		return Html.fromHtml(htmlFragmentWithColor.toString());
	}

	protected String generateDateString(final long millis) {
		try {
			final DateTimeFormatterBuilder dateFormatter = new DateTimeFormatterBuilder();
			dateFormatter.appendYear(4, 4).appendLiteral(".");
			dateFormatter.appendMonthOfYear(2).appendLiteral(".");
			dateFormatter.appendDayOfMonth(2).appendLiteral(" ");
			dateFormatter.appendHourOfDay(2).appendLiteral(":");
			dateFormatter.appendMinuteOfHour(2).appendLiteral(":").appendSecondOfMinute(2);
			return dateFormatter.toFormatter().print(new Instant(millis));
		} catch (final RuntimeException rtex) {
			return "2014 01/01 00:00:00";
		}
	}

	protected String generateDurationString(final long millis, final boolean withSeconds) {
		long timeToConvert = millis;
		final StringBuffer buffer = new StringBuffer();
		if (timeToConvert > EveAbstractHolder.ONEDAY) {
			final double day = Math.floor(timeToConvert / EveAbstractHolder.ONEDAY);
			buffer.append(Double.valueOf(day).intValue()).append("D ");
			timeToConvert -= day * EveAbstractHolder.ONEDAY;
		}
		if (timeToConvert > EveAbstractHolder.ONEHOUR) {
			final double hour = Math.floor(timeToConvert / EveAbstractHolder.ONEHOUR);
			buffer.append(Double.valueOf(hour).intValue()).append("H ");
			timeToConvert -= hour * EveAbstractHolder.ONEHOUR;
		}
		if (timeToConvert > EveAbstractHolder.ONEMINUTE) {
			final double minute = Math.floor(timeToConvert / EveAbstractHolder.ONEMINUTE);
			buffer.append(Double.valueOf(minute).intValue()).append("M");
			timeToConvert -= minute * EveAbstractHolder.ONEMINUTE;
		}
		if (withSeconds) {
			final double second = Math.floor(timeToConvert / 1000.0);
			buffer.append(" ").append(Double.valueOf(second).intValue()).append("S");
		}
		return buffer.toString();
	}

	protected String generateDurationString2(final long millis) {
		final DateTimeFormatterBuilder timeLeftCountdown = new DateTimeFormatterBuilder();
		if (millis > EveAbstractHolder.ONEDAY) {
			timeLeftCountdown.appendDayOfYear(1).appendLiteral("D ");
		}
		if (millis > EveAbstractHolder.ONEHOUR) {
			timeLeftCountdown.appendHourOfDay(2).appendLiteral("H ");
		}
		if (millis > EveAbstractHolder.ONEMINUTE) {
			timeLeftCountdown.appendMinuteOfHour(2).appendLiteral("M ").appendSecondOfMinute(2).appendLiteral('S');
		}
		return timeLeftCountdown.toFormatter().print(new Instant(millis));
	}

	protected String generatePriceString(final double price, final boolean compress, final boolean addSuffix) {
		// Generate different formats depending on the quantity and the compress
		// flag.
		// Get rid of negative numbers.
		if (compress) {
			if (Math.abs(price) > 1200000000.0) if (addSuffix)
				return EveAbstractHolder.priceFormatter.format(price / 1000.0 / 1000.0 / 1000.0) + " B ISK";
			else
				return EveAbstractHolder.priceFormatter.format(price / 1000.0 / 1000.0 / 1000.0);
			if (Math.abs(price) > 12000000.0) if (addSuffix)
				return EveAbstractHolder.priceFormatter.format(price / 1000.0 / 1000.0) + " M ISK";
			else
				return EveAbstractHolder.priceFormatter.format(price / 1000.0 / 1000.0);
		}
		if (addSuffix)
			return EveAbstractHolder.priceFormatter.format(price) + " ISK";
		else
			return EveAbstractHolder.priceFormatter.format(price);
	}

	protected String generateSecurityColor(double sec, final String data) {
		final StringBuffer htmlFragmentWithColor = new StringBuffer();
		String secColor = "#F00000";
		// Get the color from the table.
		if (sec < 0.0) {
			sec = 0.0;
		}
		if (sec > 1.0) {
			sec = 1.0;
		}
		final long secAdjust = Long.valueOf(Math.round(sec * 10.0)).intValue();
		secColor = EveAbstractHolder.securityLevels.get(Long.valueOf(secAdjust).intValue());
		htmlFragmentWithColor.append("<font color='").append(secColor).append("'>").append(data).append("</font>");
		return htmlFragmentWithColor.toString();
	}

	protected String generateTimeString(final long millis) {
		try {
			final DateTimeFormatterBuilder timeFormatter = new DateTimeFormatterBuilder();
			if (millis > EveAbstractHolder.ONEDAY) {
				timeFormatter.appendDayOfYear(1).appendLiteral("D ");
			}
			if (millis > EveAbstractHolder.ONEHOUR) {
				timeFormatter.appendHourOfDay(2).appendLiteral(":");
			}
			if (millis > EveAbstractHolder.ONEMINUTE) {
				timeFormatter.appendMinuteOfHour(2).appendLiteral(":").appendSecondOfMinute(2);
			}
			return timeFormatter.toFormatter().print(new Instant(millis));
		} catch (final RuntimeException rtex) {
			return "0:00";
		}
	}

	protected void loadEveIcon(final ImageView targetIcon, final int typeID) {
		this.loadEveIcon(targetIcon, typeID, false);
	}

	/**
	 * Downloads and caches the item icon from the CCP server. The new implementation check for special cases
	 * such as locations. Stations on locations have an image that can be downloaded from the same place.
	 * 
	 * @param targetIcon
	 * @param typeID
	 */
	protected void loadEveIcon(final ImageView targetIcon, final int typeID, final boolean station) {
		// Check if the layout has the icon placeholder.
		if (null != targetIcon) {
			// If the flag signals an station change the code.
			String link = NeoComApp.getTheCacheConnector().getURLForItem(typeID);
			if (station) {
				link = NeoComApp.getTheCacheConnector().getURLForStation(typeID);
			}
			final Drawable draw = NeoComApp.getTheCacheConnector().getCacheDrawable(link, targetIcon);
			targetIcon.setImageDrawable(draw);
		}
	}

	protected Spanned regionSystemLocation(final EveLocation loc) {
		final StringBuffer htmlLocation = new StringBuffer();
		// EveLocation loc =
		// AppConnector.getDBConnector().searchLocationbyID(getCastedModel().getBlueprintLocationID());
		final String security = loc.getSecurity();
		String secColor = EveAbstractHolder.securityLevels.get(security);
		if (null == secColor) {
			secColor = "#2FEFEF";
		}
		// Append the Region -> system
		htmlLocation.append(loc.getRegion()).append(AppWideConstants.FLOW_ARROW_RIGHT).append(loc.getConstellation())
				.append(AppWideConstants.FLOW_ARROW_RIGHT);
		htmlLocation.append("<font color='").append(secColor).append("'>")
				.append(EveAbstractHolder.securityFormatter.format(loc.getSecurityValue())).append("</font>");
		htmlLocation.append(" ").append(loc.getStation());
		return Html.fromHtml(htmlLocation.toString());
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@SuppressWarnings("deprecation")
	protected void setBackgroundTransparency(final int resource) {
		// Set the background form the Theme.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			_convertView.setBackgroundDrawable(this.getContext().getResources().getDrawable(resource));
		}
	}
}

// - UNUSED CODE
// ............................................................................................
