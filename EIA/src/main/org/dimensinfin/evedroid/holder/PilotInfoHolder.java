//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.holder;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.model.NeoComCharacter;
import org.dimensinfin.evedroid.part.PilotInfoPart;

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
	private TextView	lastKnownLocationLabel	= null;
	private TextView	accountBalanceLabel			= null;
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
		NeoComCharacter pilot = this.getPart().getCastedModel();
		pilotName.setText(pilot.getName());
		lastKnownLocation.setText(">> " + pilot.getLastKnownLocation() + " <<");
		accountBalance.setText(this.getPart().get_balance());
		assetsCount.setText(this.getPart().get_assetsCount());

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
