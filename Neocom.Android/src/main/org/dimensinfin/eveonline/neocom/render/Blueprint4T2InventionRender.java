//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.render;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.core.EveAbstractHolder;
import org.dimensinfin.eveonline.neocom.part.BlueprintPart;
import org.dimensinfin.eveonline.neocom.part.MarketDataPart;

import android.app.Activity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

// - CLASS IMPLEMENTATION ...................................................................................
public class Blueprint4T2InventionRender extends EveAbstractHolder {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	public TextView	itemName			= null;
	public TextView	bpcCount			= null;
	public TextView	inventionCost	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Blueprint4T2InventionRender(final MarketDataPart target, final Activity context) {
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
		inventionCost = (TextView) _convertView.findViewById(R.id.inventionCost);
	}

	@Override
	public void updateContent() {
		super.updateContent();
		itemName.setText(getPart().getName());
		if (AppWideConstants.DEVELOPMENT) itemName.setText(getPart().getName() + " [#" + getPart().getTypeID() + "]");
		int inventionCount = getPart().getPossibleRuns();
		int runCount = getPart().getRuns() * getPart().getStackSize();
		// Get the number of total copies manufacturable to set the color.
		bpcCount.setText(displayDoableCount(runCount, inventionCount));
		//		String pctcolor = "#FFFFFF";
		//		if (inventionCount < runCount) pctcolor = "#FFA500";
		//		if (inventionCount == 0) pctcolor = "#F00000";
		//		bpcCount.setText(colorFormat(runCount + " BPCs [" + inventionCount + " copies]", pctcolor, null));
		inventionCost.setText(generatePriceString(getPart().getInventionCost(), true, true));

		loadEveIcon((ImageView) _convertView.findViewById(R.id.itemIcon), getPart().getCastedModel().getTypeID());
	}

	private CharSequence displayDoableCount(int availableCount, int doableCount) {
		String pctcolor = "#FFFFFF";
		if (doableCount < availableCount) pctcolor = "#FFA500";
		if (doableCount == 0) pctcolor = "#F00000";
		return colorFormat(availableCount + " BPCs [" + doableCount + " copies]", pctcolor, null);
	}

	@Override
	protected void createView() {
		final LayoutInflater mInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		_convertView = mInflater.inflate(R.layout.blueprint4invention, null);
		_convertView.setTag(this);
	}
}
// - UNUSED CODE ............................................................................................
