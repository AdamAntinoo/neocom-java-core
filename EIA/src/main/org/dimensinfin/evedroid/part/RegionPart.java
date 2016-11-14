//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

// - IMPORT SECTION .........................................................................................
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.core.model.AbstractPropertyChanger;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.INamedPart;
import org.dimensinfin.evedroid.holder.RegionHolder;
import org.dimensinfin.evedroid.model.Separator;

import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class RegionPart extends AbstractAndroidPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long			serialVersionUID		= -7696249781992547826L;
	private static Logger					logger							= Logger.getLogger("RegionPart");
	private static DecimalFormat	itemCountFormatter	= new DecimalFormat("###,##0");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public RegionPart(final Separator region) {
		super(region);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String get_contentCount() {
		return itemCountFormatter.format(getChildren().size());
	}

	public String get_region() {
		return getCastedModel().getTitle();
	}

	public Separator getCastedModel() {
		return (Separator) getModel();
	}

	@Override
	public long getModelID() {
		return getCastedModel().getTitle().hashCode();
	}

	public String getName() {
		return getCastedModel().getTitle();
	}

	@Override
	public ArrayList<AbstractAndroidPart> collaborate2View() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> ch = getChildren();
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		for (AbstractPropertyChanger node : ch) {
			// Convert the node to a part.
			AbstractAndroidPart part = (AbstractAndroidPart) node;
			// Add me to the output list because I am not empty
			result.add(part);
			// Check if the node is expanded. Then add its children.
			if (part.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = part.collaborate2View();
				result.addAll(grand);
			}
		}
		return result;
	}

	/**
	 * Returns the list of parts that are available for this node. If the node it is expanded then the list will
	 * include the children and any other grand children of this one. If the node is collapsed then the only
	 * result will be the node itself.
	 * 
	 * @return list of parts that are accessible for this node.
	 */
	@Override
	public ArrayList<AbstractAndroidPart> getPartChildren() {
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		Vector<AbstractPropertyChanger> ch = getChildren();
		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		for (AbstractPropertyChanger node : ch) {
			// Convert the node to a part.
			AbstractAndroidPart part = (AbstractAndroidPart) node;
			result.add(part);
			// Check if the node is expanded. Then add its children.
			if (part.isExpanded()) {
				ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
				result.addAll(grand);
			}
		}
		return result;
	}

	public void onClick(final View view) {
		// Toggle location to show its contents.
		toggleExpanded();
		fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("RegionPart [");
		buffer.append(this.getCastedModel());
		buffer.append(" ]");
		return buffer.toString();
	}

	@Override
	protected AbstractHolder selectHolder() {
		// Get the proper holder from the render mode.
		return new RegionHolder(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
