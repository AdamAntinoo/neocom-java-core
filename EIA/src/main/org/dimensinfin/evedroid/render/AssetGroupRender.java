//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.AssetGroupPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetGroupRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................

	// - L A Y O U T   F I E L D S
	public TextView	itemName		= null;
	public TextView	childCount	= null;
	public TextView	itemCount		= null;
	public TextView	volume			= null;
	public TextView	sellValue		= null;

	// - L A Y O U T   L A B E L S
	public TextView	iskLabel		= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetGroupRender(final AssetGroupPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public AssetGroupPart getPart() {
		return (AssetGroupPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		childCount = (TextView) _convertView.findViewById(R.id.childCount);
		itemCount = (TextView) _convertView.findViewById(R.id.itemCount);
		volume = (TextView) _convertView.findViewById(R.id.volume);
		sellValue = (TextView) _convertView.findViewById(R.id.sellValue);

		iskLabel = (TextView) _convertView.findViewById(R.id.iskLabel);
	}

	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().get_name());
		childCount.setText(getPart().get_contentCount());
		itemCount.setText(getPart().get_itemCount());
		volume.setText(getPart().get_volume());
		sellValue.setText(getPart().get_sellValue());
		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		// The view is a new view. We have to fill all the items
		_convertView = mInflater.inflate(R.layout.assets4group, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
