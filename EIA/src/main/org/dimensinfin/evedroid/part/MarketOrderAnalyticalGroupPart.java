//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.GregorianCalendar;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.model.MarketOrderAnalyticalGroup;
import org.dimensinfin.evedroid.render.AnalyticalGroupRender;
import org.dimensinfin.evedroid.render.MarketSideRender;

// - CLASS IMPLEMENTATION ...................................................................................
public class MarketOrderAnalyticalGroupPart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 5929631623003721637L;

	// - F I E L D - S E C T I O N ............................................................................
//	private final int					priority					= 10;
//	private final int					iconReference			= R.drawable.defaultitemicon;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public MarketOrderAnalyticalGroupPart(final MarketOrderAnalyticalGroup node) {
		super(node);
		this.expanded = true;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public MarketOrderAnalyticalGroup getCastedModel() {
		return (MarketOrderAnalyticalGroup) getModel();
	}
	public double getGroupBudget() {
		return getCastedModel().getBudget();
	}
	public int getGroupCount() {
		return getCastedModel().getChildren().size();
	}
	public int getGroupQuantity() {
		return getCastedModel().getQuantity();
	}

	public String getGroupTitle() {
		return getCastedModel().getTitle();
	}

	@Override
	public long getModelID() {
		return GregorianCalendar.getInstance().getTimeInMillis();
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer("MarketOrderAnalyticalGroupPart [");
		buffer.append(getGroupTitle()).append(" ");
		buffer.append("weight:").append(getCastedModel().getWeight()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPMARKETANALYTICAL)
			return new AnalyticalGroupRender(this, this._activity);
		// TODO Create a default render to be returned when there is no default.
		return new MarketSideRender(this, this._activity);
	}
}

// - UNUSED CODE ............................................................................................
