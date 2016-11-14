//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.constant.ModelWideConstants;
import org.dimensinfin.evedroid.part.ResourcePart;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;

// - CLASS IMPLEMENTATION ...................................................................................
public class BlueprintResourceRender extends ResourceRender {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	// - L A Y O U T   L A B E L S

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public BlueprintResourceRender(final ResourcePart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getName());
		if (AppWideConstants.DEVELOPMENT) itemName.setText(getPart().getName() + " [#" + getPart().getTypeID() + "]");
		String category = getPart().getCategory();
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			itemPrice.setVisibility(View.GONE);
			// Check for blueprint level 2
			if (getPart().getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				balance.setText(getPart().get_inventionCost());
				balance.setVisibility(View.VISIBLE);
			} else {
				balance.setText("N/A");
				balance.setVisibility(View.GONE);
			}
			totalItems.setText("x1");
		} else {
			if (getPart().getQuantity() == 1) itemPrice.setVisibility(View.GONE);
			itemPrice.setText(generatePriceString(getPart().getSellerPrice(), true, true));
			balance.setText(generatePriceString(getPart().getBalance(), true, true));
			totalItems.setText("x" + qtyFormatter.format(getPart().getQuantity()));
		}

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
		_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.topwhiteline));
	}
}

// - UNUSED CODE ............................................................................................
