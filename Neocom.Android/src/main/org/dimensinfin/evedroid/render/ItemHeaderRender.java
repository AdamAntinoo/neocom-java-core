//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractHolder;
import org.dimensinfin.evedroid.part.ItemHeader4IndustryPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class ItemHeaderRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	public TextView		itemName						= null;
	public TextView		itemGroupCategory		= null;
	public TextView		bestSellPrice				= null;
	public TextView		bestSellLocation		= null;
	public TextView		bestBuyPrice				= null;
	public TextView		bestBuyLocation			= null;
	public TextView		manufactureCost			= null;
	public TextView		moduleIndex					= null;
	public TextView		moduleMultiplier		= null;
	public ViewGroup	moduleQualityBlock	= null;
	public ViewGroup	qualityLabelsBlock	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ItemHeaderRender(final AbstractAndroidPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ItemHeader4IndustryPart getPart() {
		return (ItemHeader4IndustryPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		this.itemName = (TextView) this._convertView.findViewById(R.id.itemName);
		this.itemGroupCategory = (TextView) this._convertView.findViewById(R.id.itemGroupCategory);
		this.bestSellPrice = (TextView) this._convertView.findViewById(R.id.bestSellPrice);
		this.bestSellLocation = (TextView) this._convertView.findViewById(R.id.bestSellLocation);
		this.bestBuyPrice = (TextView) this._convertView.findViewById(R.id.bestBuyPrice);
		this.bestBuyLocation = (TextView) this._convertView.findViewById(R.id.bestBuyLocation);
		this.manufactureCost = (TextView) this._convertView.findViewById(R.id.manufactureCost);
		this.moduleIndex = (TextView) this._convertView.findViewById(R.id.moduleIndex);
		this.moduleMultiplier = (TextView) this._convertView.findViewById(R.id.moduleMultiplier);

		this.moduleQualityBlock = (ViewGroup) this._convertView.findViewById(R.id.moduleQualityBlock);
		this.qualityLabelsBlock = (ViewGroup) this._convertView.findViewById(R.id.qualityLabelsBlock);

		this.itemName.setTypeface(getThemeTextFont());
		this.bestSellPrice.setTypeface(getThemeTextFont());
		this.bestBuyPrice.setTypeface(getThemeTextFont());
		this.manufactureCost.setTypeface(getThemeTextFont());
		this.moduleIndex.setTypeface(getThemeTextFont());
		this.moduleMultiplier.setTypeface(getThemeTextFont());
	}

	@Override
	public void updateContent() {
		super.updateContent();
		this.itemName.setText(getPart().getName());
		if (AppWideConstants.DEVELOPMENT)
			this.itemName.setText(getPart().getName() + " #[" + getPart().getModelID() + "]");
		this.itemGroupCategory.setText(getPart().getGroup() + "/" + getPart().getCategory());
		this.bestSellPrice.setText(generatePriceString(getPart().getBuyerPrice(), false, false));
		this.bestSellLocation.setText(getPart().display_BuyerLocation());
		this.bestBuyPrice.setText(generatePriceString(getPart().getSellerPrice(), false, false));
		this.bestBuyLocation.setText(getPart().display_SellerLocation());

		if (getPart().isManufacturable()) {
			this.manufactureCost.setText(displayManufactureCost(getPart().getManufactureCost(), getPart().getBuyerPrice(),
					true, true));
			//			manufactureCost.setText(Html.fromHtml(getPart().get_manufactureCost()));
			this.moduleIndex.setText(moduleIndexFormatter.format(getPart().getProfitIndex()));
			this.moduleMultiplier.setText(moduleMultiplierFormatter.format(getPart().getMultiplier()));

			this.manufactureCost.setVisibility(View.VISIBLE);
			this.moduleQualityBlock.setVisibility(View.VISIBLE);
			this.qualityLabelsBlock.setVisibility(View.VISIBLE);
		} else {
			this.manufactureCost.setVisibility(View.GONE);
			this.moduleQualityBlock.setVisibility(View.GONE);
			this.qualityLabelsBlock.setVisibility(View.GONE);
		}

		loadEveIcon((ImageView) this._convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getItemID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this._convertView = mInflater.inflate(R.layout.item_4header, null);
		this._convertView.setTag(this);
	}
}

// - UNUSED CODE ............................................................................................
