//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ResourcePart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Resource4MarketOrderRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView	itemName			= null;
	private TextView		orderLocation	= null;
	private TextView		itemPrice			= null;
	private TextView		volumes				= null;
	private TextView		orderDuration	= null;

	// - L A Y O U T   L A B E L S
	private TextView		expiresLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Resource4MarketOrderRender(final ResourcePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public ResourcePart getPart() {
		return (ResourcePart) super.getPart();
	}

	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		orderLocation = (TextView) _convertView.findViewById(R.id.orderLocation);
		itemPrice = (TextView) _convertView.findViewById(R.id.itemPrice);
		volumes = (TextView) _convertView.findViewById(R.id.volumes);
		orderDuration = (TextView) _convertView.findViewById(R.id.orderDuration);

		expiresLabel = (TextView) _convertView.findViewById(R.id.expiresLabel);
	}

	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getName());
		itemPrice.setText(generatePriceString(getPart().getHighestBuyerPrice(), true, true));
		itemPrice.setTextAppearance(getContext(), R.style.PriceStyle_Seller);
		orderLocation.setText(regionSystemLocation(getPart().getHighestBuyerLocation()));
		orderDuration.setText(generatePriceString(getPart().getBalance(), true, true));
		volumes.setText(qtyFormatter.format(getPart().getQuantity()));
		expiresLabel.setText("BUDGET");

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getTypeID());
//		_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.topwhiteline));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.resource4marketorders, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................
