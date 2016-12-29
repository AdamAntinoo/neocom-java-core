//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.part;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.eveonline.neocom.R;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.render.IndustryGroupRender;
import org.dimensinfin.evedroid.render.JobStateRender;
import org.dimensinfin.evedroid.render.MarketSideRender;
import org.dimensinfin.evedroid.render.ShipSlotRender;
import org.dimensinfin.eveonline.neocom.model.Separator;

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
		this.getCastedModel().setExpanded(true);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_counter() {
		return EveAbstractPart.qtyFormatter.format(this.getChildren().size());
	}

	public Separator getCastedModel() {
		return (Separator) this.getModel();
	}

	public int getChildrenCount() {
		return this.getChildren().size();
	}

	public int getIconReference() {
		return iconReference;
	}

	@Override
	public long getModelID() {
		return GregorianCalendar.getInstance().getTimeInMillis();
	}

	public String getTitle() {
		return this.getCastedModel().getTitle();
	}

	/**
	 * The default actions inside this method usually are the sorting of the children nodes. Sort the container
	 * contents by name.
	 */
	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		// Order the contents by alphabetical name.
		Collections.sort(targets, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		return targets;
	}

	public GroupPart setIconReference(final int ref) {
		Log.i("REMOVE", "-- GroupPart.setIconReference - " + this.toString() + " change value to: " + ref);
		iconReference = ref;
		return this;
	}

	public EveAbstractPart setPriority(final int pri) {
		priority = pri;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("GroupPart [");
		buffer.append(this.getTitle()).append(" ");
		buffer.append(priority).append(" ");
		buffer.append("chCount: ").append(this.getChildren().size()).append(" ");
		buffer.append("]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPMARKETSIDE)
			return new MarketSideRender(this, _activity);
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPJOBSTATE)
			return new JobStateRender(this, _activity);
		if (this.getRenderMode() == AppWideConstants.rendermodes.RENDER_GROUPSHIPFITTING)
			return new ShipSlotRender(this, _activity);
		return new IndustryGroupRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
