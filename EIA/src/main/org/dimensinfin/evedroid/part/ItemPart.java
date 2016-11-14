//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class ItemPart extends MarketDataPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 968880399396071464L;

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ItemPart(final AbstractGEFNode node) {
		super(node);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getCategory() {
		return getItem().getCategory();
	}

	public String getGroup() {
		return getItem().getGroupName();
	}

	@Override
	public long getModelID() {
		return getItem().getTypeID();
	}

	public String getName() {
		return getItem().getName();
	}

	public String getTech() {
		return getItem().getTech();
	}

	public int getTypeID() {
		return getItem().getTypeID();
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer("ItemPart [");
		buffer.append("#").append(getItem().getTypeID()).append(" ");
		buffer.append(getName()).append(" ");
		buffer.append("Category:").append(getItem().getCategory()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	protected AbstractHolder selectHolder() {
		throw new RuntimeException("E> ItemPart. Call to unimplemented call on abstract class.");
	}
}

// - UNUSED CODE ............................................................................................
