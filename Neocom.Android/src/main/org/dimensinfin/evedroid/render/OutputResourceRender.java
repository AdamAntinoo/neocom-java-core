//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ResourcePart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class OutputResourceRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	private TextView	itemName					= null;
	private TextView	totalItems				= null;
	private TextView	bestBuyerPrice		= null;
	private TextView	bestBuyerLocation	= null;
	private TextView	manufactureCost		= null;
	private TextView	profit						= null;

	// - L A Y O U T   L A B E L S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public OutputResourceRender(final ResourcePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ResourcePart getPart() {
		return (ResourcePart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		totalItems = (TextView) _convertView.findViewById(R.id.totalItems);
		bestBuyerPrice = (TextView) _convertView.findViewById(R.id.bestBuyerPrice);
		bestBuyerLocation = (TextView) _convertView.findViewById(R.id.bestBuyerLocation);
		manufactureCost = (TextView) _convertView.findViewById(R.id.manufactureCost);
		profit = (TextView) _convertView.findViewById(R.id.profit);
	}

	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getCastedModel().getName());
		if (AppWideConstants.DEVELOPMENT)
			itemName.setText(getPart().getCastedModel().getName() + " [#" + getPart().getCastedModel().getTypeID() + "]");
		totalItems.setText(qtyFormatter.format(getPart().getQuantity()));
		// The sell is the total income. If cannot make not even one set to to 1.
		int totalQty = Math.max(1, getPart().getQuantity());
		// If this represents a blueprint the display is different.
		if (getPart().getItem().isBlueprint()) {
			bestBuyerPrice.setVisibility(View.GONE);
			profit.setVisibility(View.GONE);
		} else {
			double income = getPart().getHighestBuyerPrice() * totalQty;
			bestBuyerPrice.setText(generatePriceString(income, true, true));
			bestBuyerLocation.setText(getPart().display_BuyerLocation());
			// The manufacture cost is the cost of all copies.
			double cost = getPart().getManufactureCost() * totalQty;
			double profitValue = income - cost;
			profit.setText(generatePriceString(profitValue, true, true));
			if (profitValue < 0.0) profit.setTextColor(getContext().getResources().getColor(R.color.redPrice));
			if (profitValue > 0.0) profit.setTextColor(getContext().getResources().getColor(R.color.greenPrice));
			manufactureCost.setText(displayManufactureCost(cost, getPart().getBuyerPrice() * totalQty, true, true));
		}

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
		_convertView.invalidate();
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.resource4industryoutput, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
