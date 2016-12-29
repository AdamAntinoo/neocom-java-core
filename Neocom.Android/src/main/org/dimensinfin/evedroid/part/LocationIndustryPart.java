//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.part;

import java.util.Collections;
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.render.Location4IndustryRender;
import org.dimensinfin.eveonline.neocom.model.EveLocation;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class LocationIndustryPart extends LocationPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 5847693617588479818L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean						isContainer				= false;
	private String						containerName			= "Hangar";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public LocationIndustryPart(final EveLocation location) {
		super(location);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getContainerName() {
		return containerName;
	}

	public boolean hasContainer() {
		return isContainer;
	}

	//	@Override
	//	public ArrayList<AbstractAndroidPart> getPartChildren() {
	//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		Vector<AbstractPropertyChanger> ch = getChildren();
	//		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
	//		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_ITEM_TYPE));
	//
	//		for (AbstractPropertyChanger node : ch) {
	//			// Convert the node to a part.
	//			AbstractAndroidPart part = (AbstractAndroidPart) node;
	//			result.add(part);
	//			// Check if the node is expanded. Then add its children.
	//			if (part.isExpanded()) {
	//				ArrayList<AbstractAndroidPart> grand = part.getPartChildren();
	//				result.addAll(grand);
	//				// Add a separator.
	//				//	result.add(new TerminatorPart(new Separator("")));
	//			}
	//		}
	//		//		result.add(new TerminatorPart(new Separator("")));
	//		return result;
	//	}

	/**
	 * A click on a location block on the Industry Blueprint section will only toggle the expand/collapse state.
	 */
	public void onClick(final View view) {
		// Toggle location to show its contents.
		this.toggleExpanded();
		Log.i("EVEI", "-- " + this.getCastedModel().isExpanded() + " expansion to Location " + this.getName() + " ["
				+ this.get_locationID() + "]");
		this.fireStructureChange(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE, this, this);
	}

	//	@Override
	//	public ArrayList<AbstractAndroidPart> collaborate2View() {
	//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		Vector<AbstractPropertyChanger> ch = getChildren();
	//		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
	//		Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_ITEM_TYPE));
	//
	//		for (AbstractPropertyChanger node : ch) {
	//			// Convert the node to a part.
	//			AbstractAndroidPart part = (AbstractAndroidPart) node;
	//			result.add(part);
	//			// Check if the node is expanded. Then add its children.
	//			if (part.isExpanded()) {
	//				ArrayList<AbstractAndroidPart> grand = part.collaborate2View();
	//				result.addAll(part.collaborate2View());
	//				// Add a separator.
	//				//	result.add(new TerminatorPart(new Separator("")));
	//			}
	//		}
	//		//		result.add(new TerminatorPart(new Separator("")));
	//		return result;
	//	}
	/**
	 * The policies for locations are two sorts. The first one by name the second by item type.
	 */
	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		Collections.sort(targets, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		Collections.sort(targets, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_ITEM_TYPE));
		return targets;
	}

	public void setContainerLocation(final boolean flag) {
		isContainer = flag;
	}

	public void setContainerName(final String name) {
		containerName = name;
	}

	@Override
	protected AbstractHolder selectHolder() {
		return new Location4IndustryRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
