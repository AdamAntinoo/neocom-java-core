//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ApiKeyPart;
import org.joda.time.Instant;

import android.app.Activity;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ApiKeyRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView	key						= null;
	private TextView	type					= null;
	private TextView	timeLeft			= null;

	// - L A Y O U T   L A B E L S
	private TextView	timeLeftLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ApiKeyRender(final ApiKeyPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
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
		key.setText(this.getPart().getTransformedKey());
		type.setText(this.getPart().getCastedModel().getType().name());

		final Instant expires = new Instant(this.getPart().getCastedModel().getExpires());
		final Instant now = new Instant();
		if (expires.isBefore(now)) {
			timeLeft.setText("Expired! - " + EveAbstractHolder.timePointFormatter.print(expires));
			// Change the background to a red one
			timeLeftLabel.setText("EXPIRATION DATE");
			_convertView.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.redtraslucent60));
		} else {
			long millis = expires.getMillis() - now.getMillis();
			CountDownTimer timer = new CountDownTimer(millis, AppWideConstants.SECONDS5) {
				@Override
				public void onFinish() {
					timeLeft.setText("Expired! - " + EveAbstractHolder.timePointFormatter.print(expires));
					timeLeftLabel.setText("EXPIRATION DATE");
					_convertView.invalidate();
				}

				@Override
				public void onTick(final long millisUntilFinished) {
					timeLeft.setText(ApiKeyRender.this.generateDurationString(millisUntilFinished, true));
					timeLeft.invalidate();
					_convertView.invalidate();
				}
			}.start();
			_convertView.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.greentraslucent20));
		}
		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.apikey4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
