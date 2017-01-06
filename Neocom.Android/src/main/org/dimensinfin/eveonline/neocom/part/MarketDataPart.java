//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.part;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.model.EveItem;

import android.text.Spanned;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class MarketDataPart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -5642783343846527150L;

	// - F I E L D - S E C T I O N ............................................................................
	protected EveItem					item							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketDataPart(final AbstractComplexNode node) {
		super(node);
		this.initialize();
	}

	//	public MarketDataPart(final AbstractGEFNode node) {
	//		super(node);
	//		initialize();
	//	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public Spanned display_BuyerLocation() {
		return this.colorFormatLocation(this.getItem().getHighestBuyerPrice().getLocation());
	}

	public Spanned display_SellerLocation() {
		return this.colorFormatLocation(this.getItem().getLowestSellerPrice().getLocation());
	}

	public String get_highestBuyerPrice() {
		return this.generatePriceString(this.getItem().getHighestBuyerPrice().getPrice(), false, false);
	}

	public String get_lowestSellerPrice() {
		return this.generatePriceString(this.getItem().getLowestSellerPrice().getPrice(), false, false);
	}

	public double getBuyerPrice() {
		return this.getItem().getHighestBuyerPrice().getPrice();
	}

	public EveItem getItem() {
		if (null == item) throw new RuntimeException("RT> Use of a MarketDataPart without setting the the item.");
		return item;
	}

	public double getSellerPrice() {
		return this.getItem().getLowestSellerPrice().getPrice();
	}

	protected abstract void initialize();
}
// - UNUSED CODE ............................................................................................
