//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ApiKeyPart;
import org.joda.time.Instant;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class APIKeyRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView	key						= null;
	private TextView	type					= null;
	private TextView	timeLeft			= null;

	// - L A Y O U T   L A B E L S
	private TextView	timeLeftLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public APIKeyRender(final ApiKeyPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ApiKeyPart getPart() {
		return (ApiKeyPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		key = (TextView) _convertView.findViewById(R.id.key);
		type = (TextView) _convertView.findViewById(R.id.type);
		timeLeft = (TextView) _convertView.findViewById(R.id.timeLeft);
		timeLeftLabel = (TextView) _convertView.findViewById(R.id.timeLeftLabel);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void updateContent() {
		super.updateContent();
		key.setText(getPart().get_key());
		type.setText(getPart().get_type());

		final Instant expires = getPart().getCastedModel().getTimeLeft();
		Instant now = new Instant();
		if (expires.isBefore(now)) {
			timeLeft.setText("Expired! - " + timePointFormatter.print(expires));
			// Change the background to a red one
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.redtraslucent60));
		} else {
			long millis = expires.getMillis() - now.getMillis();
			CountDownTimer timer = new CountDownTimer(millis, 1000) {
				public void onFinish() {
					timeLeft.setText("Expired! - " + timePointFormatter.print(expires));
				}

				public void onTick(final long millisUntilFinished) {
					timeLeft.setText(generateDurationString(millisUntilFinished, true));
					timeLeft.invalidate();
					_convertView.invalidate();
				}
			}.start();
			_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.greentraslucent20));
		}

		// Set the background form the Theme.
		//		_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.bluetraslucent20));
		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.apikey4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
