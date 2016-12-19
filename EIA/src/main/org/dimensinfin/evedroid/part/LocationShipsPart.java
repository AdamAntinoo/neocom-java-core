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
import java.util.Vector;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractHolder;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.interfaces.INamedPart;
import org.dimensinfin.evedroid.model.ShipLocation;
import org.dimensinfin.evedroid.render.Location4ShipsRender;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

// - CLASS IMPLEMENTATION ...................................................................................
public class LocationShipsPart extends LocationPart implements INamedPart, OnClickListener {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 5847693617588479818L;

	// - F I E L D - S E C T I O N ............................................................................
	private boolean						isContainer				= false;
	private String						containerName			= "Hangar";

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public LocationShipsPart(final ShipLocation location) {
		super(location);
	}

	/**
	 * Returns the number of model part children that are the blueprints at that location. If the case on a same
	 * station there are more than one container they will be represented as different locations.
	 * 
	 * @return
	 */
	@Override
	public String get_locationContentCount() {
		int locationAssets = this.getChildren().size();
		String countString = null;
		if (locationAssets > 1)
			countString = EveAbstractPart.qtyFormatter.format(locationAssets) + " Stacks";
		else
			countString = EveAbstractPart.qtyFormatter.format(locationAssets) + " Stack";
		return countString;
	}

	@Override
	public ShipLocation getCastedModel() {
		return (ShipLocation) super.getCastedModel();
	}

	public String getContainerName() {
		return containerName;
	}

	public boolean hasContainer() {
		return isContainer;
	}

	//	@Override
	//	public ArrayList<AbstractAndroidPart> getPartChildren() {
	//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		Vector<AbstractPropertyChanger> ch = this.getChildren();
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

	// - M E T H O D - S E C T I O N ..........................................................................
	//	@Override
	//	public ArrayList<AbstractAndroidPart> collaborate2View() {
	//		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
	//		// If the node is expanded then give the children the opportunity to also be added.
	//		if (this.isExpanded()) {
	//			Vector<AbstractPropertyChanger> ch = this.getChildren();
	//			// ---This is the section that is different for any Part. This should be done calling the list of policies.
	//			Collections.sort(ch, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
	//			// --- End of policies
	//			for (AbstractPropertyChanger nodePart : ch)
	//				if (nodePart instanceof AbstractAndroidPart) if (((AbstractAndroidPart) nodePart).renderWhenEmpty()) {
	//					result.add((AbstractAndroidPart) nodePart);
	//					result.addAll(((AbstractAndroidPart) nodePart).collaborate2View());
	//				}
	//		}
	//		return result;
	//	}
	/**
	 * The policies for locations are also special. The fist order is by ship group name then by name.
	 */
	@Override
	public Vector<IPart> runPolicies(final Vector<IPart> targets) {
		Collections.sort(targets, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_GROUPNAME));
		Collections.sort(targets, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NAME));
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
		return new Location4ShipsRender(this, _activity);
	}
}

// - UNUSED CODE ............................................................................................
