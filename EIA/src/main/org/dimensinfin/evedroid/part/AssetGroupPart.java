//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.render.AssetGroupRender;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class AssetGroupPart extends EveAbstractPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = -7696249781992547826L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetGroupPart(final Asset node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_contentCount() {
		return itemCountFormatter.format(getChildren().size());
	}

	public String get_itemCount() {
		int qty = 0;
		for (AbstractPropertyChanger node : getChildren()) {
			if (node instanceof AssetPart) {
				qty += ((AssetPart) node).getCastedModel().getQuantity();
			}
		}
		return itemCountFormatter.format(qty);
	}

	public String get_name() {
		return getCastedModel().getName();
	}

	public String get_sellValue() {
		double value = 0.0;
		for (AbstractPropertyChanger node : getChildren()) {
			if (node instanceof AssetPart) {
				Asset ass = ((AssetPart) node).getCastedModel();
				long count = ass.getQuantity();
				double price = ass.getItem().getHighestBuyerPrice().getPrice();
				value += count * price;
			}
		}
		return generatePriceString(value, true, false);
	}

	public String get_volume() {
		double volume = 0.0;
		for (AbstractPropertyChanger node : getChildren()) {
			if (node instanceof AssetPart) {
				Asset ass = ((AssetPart) node).getCastedModel();
				long count = ass.getQuantity();
				double vol = ass.getItem().getVolume();
				volume += count * vol;
			}
		}
		return itemCountFormatter.format(volume);
	}

	public Asset getCastedModel() {
		return (Asset) getModel();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getAssetID();
	}

	public String getName() {
		return getCastedModel().getName();
	}

	public void onClick(final View view) {
		// Toggle location to show its contents.
		toggleExpanded();
		fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("AsteroidPart [");
		buffer.append(this.getCastedModel());
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new AssetGroupRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
