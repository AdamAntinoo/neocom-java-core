//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ResourcePart;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;

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
	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(this.getPart().getName());
		if (AppWideConstants.DEVELOPMENT)
			itemName.setText(this.getPart().getName() + " [#" + this.getPart().getTypeID() + "]");
		String category = this.getPart().getCategory();
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
			itemPrice.setVisibility(View.GONE);
			// Check for blueprint level 2
			if (this.getPart().getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII)) {
				balance.setText(this.getPart().get_inventionCost());
				balance.setVisibility(View.VISIBLE);
			} else {
				balance.setText("N/A");
				balance.setVisibility(View.GONE);
			}
			totalItems.setText("x1");
		} else {
			if (this.getPart().getQuantity() == 1) itemPrice.setVisibility(View.GONE);
			itemPrice.setText(this.generatePriceString(this.getPart().getSellerPrice(), true, true));
			balance.setText(this.generatePriceString(this.getPart().getBalance(), true, true));
			totalItems.setText("x" + EveAbstractHolder.qtyFormatter.format(this.getPart().getQuantity()));
		}

		this.loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), this.getPart().getCastedModel().getTypeID());
		_convertView.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.topwhiteline));
	}
}

// - UNUSED CODE ............................................................................................
