//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.AssetPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetLineRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - L A Y O U T   F I E L D S
	public TextView	assetLocation	= null;
	public TextView	itemCount			= null;
	public TextView	itemPrice			= null;
	public TextView	stackValue		= null;

	// - L A Y O U T   L A B E L S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetLineRender(final AssetPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public AssetPart getPart() {
		return (AssetPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		assetLocation = (TextView) _convertView.findViewById(R.id.assetLocation);
		itemCount = (TextView) _convertView.findViewById(R.id.itemCount);
		itemPrice = (TextView) _convertView.findViewById(R.id.itemPrice);
		stackValue = (TextView) _convertView.findViewById(R.id.stackValue);
	}

	public void updateContent() {
		super.updateContent();
		assetLocation.setText(getPart().get_assetLocation());
		itemCount.setText(getPart().get_count());
		itemPrice.setText(getPart().get_itemPrice());
		stackValue.setText(getPart().get_stackValue());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.asset4location, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
