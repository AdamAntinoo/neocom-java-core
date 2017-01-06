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
import org.dimensinfin.eveonline.neocom.part.AssetPart;

import android.app.Activity;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetHolder extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger					= Logger.getLogger("AssetHolder");

	// - F I E L D - S E C T I O N ............................................................................
	protected GestureDetector	_gestureScanner	= null;

	public TextView						assetName				= null;
	public TextView						count						= null;
	public TextView						assetCategory		= null;
	public TextView						itemPrice				= null;
	public TextView						stackValue			= null;

	public TextView						countLabel			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetHolder(final AssetPart target, final Activity context) {
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
		//		_itemIcon = (ImageView) _convertView.findViewById(R.id.assetIcon);
		this.assetName = (TextView) this._convertView.findViewById(R.id.assetName);
		this.count = (TextView) this._convertView.findViewById(R.id.count);
		this.assetCategory = (TextView) this._convertView.findViewById(R.id.assetCategory);
		this.itemPrice = (TextView) this._convertView.findViewById(R.id.itemPrice);
		this.stackValue = (TextView) this._convertView.findViewById(R.id.stackValue);

		this.assetName.setTypeface(getThemeTextFont());
		this.count.setTypeface(getThemeTextFont());
		this.itemPrice.setTypeface(getThemeTextFont());
		this.stackValue.setTypeface(getThemeTextFont());
	}

	@Override
	public void updateContent() {
		super.updateContent();
		this.assetName.setText(getPart().get_assetName());
		this.count.setText(getPart().get_count());
		this.assetCategory.setText(getPart().get_assetCategory());
		this.itemPrice.setText(getPart().get_itemPrice());
		this.stackValue.setText(getPart().get_stackValue());

		// If asset inside a container or ship then set a white margin at the left.
		if (getPart().getCastedModel().hasParent()) {
			final ImageView image = (ImageView) this._convertView.findViewById(R.id.assetIcon);
			final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(image.getLayoutParams());
			lp.setMargins(15, 0, 0, 0);
			image.setLayoutParams(lp);
		}

		loadEveIcon((ImageView) this._convertView.findViewById(R.id.assetIcon), getPart().get_assetTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this._convertView = mInflater.inflate(R.layout.asset_4list, null);
		this._convertView.setTag(this);
	}
}

final class AssetGestureListener extends GestureDetector.SimpleOnGestureListener {
	@Override
	public boolean onDown(final MotionEvent event) {
		return true;
	}
}
// - UNUSED CODE ............................................................................................
