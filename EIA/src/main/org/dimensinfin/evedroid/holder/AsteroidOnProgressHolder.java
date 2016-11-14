//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EThemeTransparency;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.AsteroidOnProgressPart;
import org.joda.time.Instant;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class AsteroidOnProgressHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger					= Logger.getLogger("AsteroidOnProgressHolder");

	// - F I E L D - S E C T I O N ............................................................................

	// - L A Y O U T   F I E L D S
	private TextView			asteroidName		= null;
	private TextView			asteroidQty			= null;
	private TextView			asteroidVolume	= null;
	private TextView			timeLeft				= null;

	// - L A Y O U T   L A B E L S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AsteroidOnProgressHolder(final AsteroidOnProgressPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public AsteroidOnProgressPart getPart() {
		return (AsteroidOnProgressPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		asteroidName = (TextView) _convertView.findViewById(R.id.asteroidName);
		asteroidQty = (TextView) _convertView.findViewById(R.id.asteroidQty);
		asteroidVolume = (TextView) _convertView.findViewById(R.id.asteroidVolume);
		timeLeft = (TextView) _convertView.findViewById(R.id.timeLeft);

		asteroidName.setTypeface(_theme.getThemeTextFont());
		asteroidQty.setTypeface(_theme.getThemeTextFont());
		timeLeft.setTypeface(_theme.getThemeTextFont());
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	@Override
	public void updateContent() {
		super.updateContent();
		asteroidName.setText(getPart().get_asteroidName());
		asteroidQty.setText(getPart().get_asteroidQty());
		asteroidVolume.setText(getPart().get_asteroidVolume());
		final int secondsLeft = getPart().get_secondsLeft();
		CountDownTimer timer = new CountDownTimer(secondsLeft * 1000, 1000) {
			public void onFinish() {
				timeLeft.setText("COMPLETED");
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
					_convertView.setBackgroundDrawable(EVEDroidApp.getSingletonApp().getResources()
							.getDrawable(R.drawable.greenroundedbox));
				else
					_convertView.setBackground(EVEDroidApp.getSingletonApp().getResources()
							.getDrawable(R.drawable.greenroundedbox));
			}

			public void onTick(final long millisUntilFinished) {
				DateTimeFormatter monthAndYear = new DateTimeFormatterBuilder().appendMinuteOfHour(2).appendLiteral('m')
						.appendSecondOfMinute(2).appendLiteral('s').toFormatter();
				//	DateTimeFormatter fmt = monthAndYear.dateTime();
				String str = monthAndYear.print(new Instant(millisUntilFinished));
				timeLeft.setText(str);
				timeLeft.invalidate();
				_convertView.invalidate();
			}
		}.start();

		loadEveIcon((ImageView) _convertView.findViewById(R.id.assetIcon), getPart().getCastedModel().getTypeID());
		// Set the background form the Theme.
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN)
			_convertView.setBackgroundDrawable(_theme.getThemeTransparent(EThemeTransparency.LOW));
		else
			_convertView.setBackground(_theme.getThemeTransparent(EThemeTransparency.LOW));
		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.asteroid_4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
