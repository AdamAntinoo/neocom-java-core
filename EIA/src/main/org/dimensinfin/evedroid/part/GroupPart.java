//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.render.IndustryGroupRender;
import org.dimensinfin.evedroid.render.JobStateRender;
import org.dimensinfin.evedroid.render.MarketSideRender;
import org.dimensinfin.evedroid.render.ShipSlotRender;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
public class GroupPart extends EveAbstractPart {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= -7108273035430243825L;

	// - F I E L D - S E C T I O N ............................................................................
	private int								priority					= 10;
	private int								iconReference			= R.drawable.defaultitemicon;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public GroupPart(final Separator node) {
		super(node);
		getCastedModel().setExpanded(true);
	}

	/**
	 * This is the new view generation method that replaces the <code>getPartChildren</code>.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> collaborate2View() {
		Vector<AbstractPropertyChanger> ch = getChildren();
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		return super.getPartChildren();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_counter() {
		return qtyFormatter.format(getChildren().size());
	}

	public Separator getCastedModel() {
		return (Separator) getModel();
	}

	public int getChildrenCount() {
		return getChildren().size();
	}

	public int getIconReference() {
		return iconReference;
	}

	@Override
	public long getModelID() {
		return GregorianCalendar.getInstance().getTimeInMillis();
	}

	@Deprecated
	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		Vector<AbstractPropertyChanger> ch = getChildren();
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		return super.getPartChildren();
	}

	public String getTitle() {
		return getCastedModel().getTitle();
	}

	public void setIconReference(final int ref) {
		Log.i("REMOVE", "-- GroupPart.setIconReference - " + this.toString() + " change value to: " + ref);
		iconReference = ref;
	}

	public EveAbstractPart setPriority(final int pri) {
		priority = pri;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("GroupPart [");
		buffer.append(getTitle()).append(" ");
		buffer.append(priority).append(" ");
		buffer.append("chCount: ").append(getChildren().size()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPMARKETSIDE)
			return new MarketSideRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPJOBSTATE)
			return new JobStateRender(this, _activity);
		if (getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING)
			return new ShipSlotRender(this, _activity);
		return new IndustryGroupRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
