//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.eveonline.neocom.holder;

// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.part.AssetPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Asset4CategoryHolder extends AssetHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger	logger				= Logger.getLogger("Asset4CategoryHolder");

	// - F I E L D - S E C T I O N ............................................................................
	public TextView				locationHtml	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Asset4CategoryHolder(final AssetPart target, final Activity context) {
		super(target, context);
	}

	public AssetPart getPart() {
		return super.getPart();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public void initializeViews() {
		super.initializeViews();
		locationHtml = (TextView) _convertView.findViewById(R.id.assetLocation);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		locationHtml.setText(getPart().get_assetLocation());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.assetcategory_4list, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................
