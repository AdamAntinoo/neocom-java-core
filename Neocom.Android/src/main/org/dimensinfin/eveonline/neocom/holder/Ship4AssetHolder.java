//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.ShipPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Ship4AssetHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
//	private static Logger	logger				= Logger.getLogger("Ship4AssetHolder");

	// - F I E L D - S E C T I O N ............................................................................
	public TextView				shipType			= null;
	public TextView				shipName			= null;
	public TextView				shipLocation	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Ship4AssetHolder(final ShipPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ShipPart getPart() {
		return (ShipPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		shipType = (TextView) _convertView.findViewById(R.id.shipType);
		shipName = (TextView) _convertView.findViewById(R.id.shipName);
		shipLocation = (TextView) _convertView.findViewById(R.id.shipLocation);

	}

	@Override
	public void updateContent() {
		super.updateContent();
		shipType.setText(getPart().get_shipClassGroup());
		String name = getPart().getCastedModel().getUserLabel();
		if (null == name)
			shipName.setVisibility(View.GONE);
		else {
			shipName.setVisibility(View.VISIBLE);
			shipName.setText(name);
		}
		shipLocation.setText(getPart().get_assetLocation());

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().get_assetTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.ship4asset, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
