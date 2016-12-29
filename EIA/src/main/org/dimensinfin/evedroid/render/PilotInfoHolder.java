//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.render;

//- IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.PilotInfoPart;
import org.dimensinfin.eveonline.neocom.model.Pilot;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class PilotInfoHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	//	private static Logger	logger									= Logger.getLogger("PilotInfoHolder");

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private ImageView	pilotAvatar							= null;
	private TextView	pilotName								= null;
	private TextView	lastKnownLocation				= null;
	private TextView	accountBalance					= null;
	private TextView	assetsCount							= null;

	// - L A Y O U T   L A B E L S
	@SuppressWarnings("unused")
	private TextView	lastKnownLocationLabel	= null;
	@SuppressWarnings("unused")
	private TextView	accountBalanceLabel			= null;
	@SuppressWarnings("unused")
	private TextView	assetsCountLabel				= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PilotInfoHolder(final AbstractAndroidPart newPart, final Activity context) {
		super(newPart, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public PilotInfoPart getPart() {
		return (PilotInfoPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		pilotAvatar = (ImageView) _convertView.findViewById(R.id.pilotAvatar);
		pilotName = (TextView) _convertView.findViewById(R.id.pilotName);
		lastKnownLocation = (TextView) _convertView.findViewById(R.id.lastKnownLocation);
		accountBalance = (TextView) _convertView.findViewById(R.id.accountBalance);
		assetsCount = (TextView) _convertView.findViewById(R.id.assetsCount);

		lastKnownLocationLabel = (TextView) _convertView.findViewById(R.id.lastKnownLocationLabel);
		accountBalanceLabel = (TextView) _convertView.findViewById(R.id.accountBalanceLabel);
		assetsCountLabel = (TextView) _convertView.findViewById(R.id.assetsCountLabel);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		Pilot pilot = this.getPart().getCastedModel();
		pilotName.setText(pilot.getName());
		lastKnownLocation.setText(">> " + pilot.getLastKnownLocation());
		accountBalance.setText(this.getPart().getTransformedBalance());
		assetsCount.setText(this.getPart().getTransformedAssetsCount());

		if (null != pilotAvatar) {
			String link = pilot.getURLForAvatar();
			final Drawable draw = EVEDroidApp.getTheCacheConnector().getCacheDrawable(link, pilotAvatar);
			pilotAvatar.setImageDrawable(draw);
		}
		//		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.pilotinfo4list, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
