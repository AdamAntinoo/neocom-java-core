//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.GroupPart;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ShipSlotRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private ImageView	groupIcon	= null;
	private TextView	title			= null;
	private TextView	count			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ShipSlotRender(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public GroupPart getPart() {
		return (GroupPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		groupIcon = (ImageView) _convertView.findViewById(R.id.groupIcon);
		title = (TextView) _convertView.findViewById(R.id.title);
		count = (TextView) _convertView.findViewById(R.id.count);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		title.setText(this.getPart().getTitle());
		count.setText(EveAbstractHolder.qtyFormatter.format(this.getPart().getChildrenCount()));
		Log.i("REMOVE", "-- ShipSlotRender.updateContent - " + this.getPart().toString() + " iconvalue: "
				+ this.getPart().getIconReference());
		groupIcon.setImageResource(this.getPart().getIconReference());
		_convertView.setBackgroundResource(R.drawable.bluetraslucent60);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) this.getContext()
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.group4manufacture, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
