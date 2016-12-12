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
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ResourcePart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * Class responsible to render the resource information for a Manufacturing or Invention job component. There
 * different flavours for the Resource rendering and this is the most extensive and complex of all the
 * structures.
 * 
 * @author Adam Antinoo
 */
public class ResourceRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	// - L A Y O U T   F I E L D S
	protected TextView	itemName				= null;
	protected TextView	itemPrice				= null;
	protected TextView	balance					= null;
	protected TextView	totalItems			= null;

	// - L A Y O U T   L A B E L S
	private TextView		totalItemsLabel	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ResourceRender(final ResourcePart target, final Activity context) {
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
		itemPrice = (TextView) _convertView.findViewById(R.id.itemPrice);
		totalItems = (TextView) _convertView.findViewById(R.id.totalItems);
		balance = (TextView) _convertView.findViewById(R.id.balance);

		totalItemsLabel = (TextView) _convertView.findViewById(R.id.balance);
		totalItemsLabel.setVisibility(View.GONE);
		balance.setVisibility(View.VISIBLE);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getName());
		if (AppWideConstants.DEVELOPMENT) itemName.setText(getPart().getName() + " [#" + getPart().getTypeID() + "]");
		String category = getPart().getCategory();
		if (category.equalsIgnoreCase(ModelWideConstants.eveglobal.NeoComBlueprint)) {
			itemPrice.setVisibility(View.GONE);
			balance.setVisibility(View.GONE);
			totalItems.setText("x1");
		} else {
			// For minerals use other price
			if (getPart().getGroup().equalsIgnoreCase(ModelWideConstants.eveglobal.Mineral)) {
				itemPrice.setText(generatePriceString(getPart().getBuyerPrice(), true, true));
				balance.setText(generatePriceString(getPart().getQuantity() * getPart().getBuyerPrice(), true, true));
			} else {
				itemPrice.setText(generatePriceString(getPart().getSellerPrice(), true, true));
				balance.setText(generatePriceString(getPart().getQuantity() * getPart().getSellerPrice(), true, true));
			}
			totalItems.setText("x" + qtyFormatter.format(getPart().getQuantity()));
		}

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
		_convertView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.topwhiteline));
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.resource4industryresource, null);
		_convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
