//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.BlueprintPart;
import org.dimensinfin.eveonline.neocom.part.MarketDataPart;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Blueprint4IndustryRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	public TextView	itemName					= null;
	public TextView	bpcCount					= null;
	public TextView	bestSellPrice			= null;
	public TextView	manufactureCost		= null;
	public TextView	manufactureIndex	= null;
	public TextView	blueprintMETE			= null;
	public TextView	blueprintRuns			= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Blueprint4IndustryRender(final MarketDataPart target, final Activity context) {
		super(target, context);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public BlueprintPart getPart() {
		return (BlueprintPart) super.getPart();
	}

	@Override
	public void initializeViews() {
		super.initializeViews();
		itemName = (TextView) _convertView.findViewById(R.id.itemName);
		bpcCount = (TextView) _convertView.findViewById(R.id.bpcCount);
		bestSellPrice = (TextView) _convertView.findViewById(R.id.bestSellPrice);
		manufactureCost = (TextView) _convertView.findViewById(R.id.manufactureCost);
		manufactureIndex = (TextView) _convertView.findViewById(R.id.manufactureIndex);
		blueprintMETE = (TextView) _convertView.findViewById(R.id.blueprintMETE);
		blueprintRuns = (TextView) _convertView.findViewById(R.id.blueprintRuns);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getName());
		// Compose the BPC count information
		// Get the number of total copies manufacturable to set the color.
		int availableRuns = getPart().getMaxRuns();
		int runCount = getPart().getRunCount();
		String pctcolor = "#FFFFFF";
		if (availableRuns < runCount) pctcolor = "#FFA500";
		if (availableRuns == 0) pctcolor = "#F00000";
		StringBuffer htmlCountString = new StringBuffer();
		htmlCountString.append("<font color='").append(pctcolor).append("'>");
		htmlCountString.append(getPart().getBlueprintCount()).append(" BPCs [").append(availableRuns).append(" copies]");
		htmlCountString.append("</font>");
		bpcCount.setText(Html.fromHtml(htmlCountString.toString()));

		manufactureCost.setText(displayManufactureCost(getPart().getManufactureCost(), getPart().getBuyerPrice(), true,
				true));
		bestSellPrice.setText(generatePriceString(getPart().getBuyerPrice(), true, true));
		manufactureIndex.setText(getPart().get_manufactureIndex());
		blueprintMETE.setText(getPart().get_blueprintMETE());
		blueprintRuns.setText("[" + qtyFormatter.format(getPart().getRuns()) + "]");

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.blueprint4industry, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................
