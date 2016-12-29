//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.datasource;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.android.mvc.core.AbstractDataSource;
import org.dimensinfin.android.mvc.interfaces.IPart;
import org.dimensinfin.core.model.RootNode;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.part.BlueprintPart;
import org.dimensinfin.evedroid.part.LocationIndustryPart;
import org.dimensinfin.evedroid.part.TerminatorPart;
import org.dimensinfin.evedroid.storage.AppModelStore;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.Separator;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This DataSource will get access to the special IndustryManager to have track of the assets resulting from
 * some other actions. The list of resources is mandatory to be able to check how many blueprints can be
 * manufactured. Manufacture resources are accounted from the location where the blueprint is located with
 * exception to the MANUFACTURE location that will have the resources reduced or augmented depending on
 * scheduled manufacture jobs or by scheduled market buys.<br>
 * Each blueprint is located on a hierarchical tree with locations at the top, then Hangars or Containers and
 * then the blueprints themselves. Blueprints will be stacked and the resulting stack checked against
 * available resources to set the number of blueprints that are manufacturable. This data will be stored on
 * the Part and will use the new IndustryManager.
 * 
 * @author Adam Antinoo
 */
public class IndustryT1BlueprintsDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long													serialVersionUID	= 3277207858005629861L;

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore															_store						= null;
	private final HashMap<Long, LocationIndustryPart>	locations					= new HashMap<Long, LocationIndustryPart>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public IndustryT1BlueprintsDataSource(final AppModelStore store) {
		if (null != store) _store = store;
	}

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The source data for this data source is the EveChar - AssetsManager - Blueprint CCP API. The first
	 * element to access if the EveChar that should have access to an AssetsManager. This manager will store and
	 * cache all Character model data and on some cases the access to the CCP servers to download new and fresh
	 * versions of the API data. Blueprints are not to be stored at the database and their load on the EveChar
	 * structures is expected to be performed on background tasks once the Character is active on memory. The
	 * new Activity data model dependencies may request that same loading multiple times during the life cycle
	 * of the activities and the local data models that will be created on that cycles. Because of this is quite
	 * important to perform effective caches of the original source CCP API data that should be stored on disk
	 * for off link access and app operation.<br>
	 * The hierarchy contains two levels of elements. The first level are the locations and can be of two types,
	 * locations where the blueprints are on the floor (hangar) and locations where the blueprint is inside some
	 * container. They will not have the default name but the name of the container and will behave more like
	 * them.<br>
	 * On the second level there are the blueprints themselves.<br>
	 * <br>
	 * The process to get this hierarchy is somehow different from other loops. It will get each of the
	 * blueprints in order and then locate the proper place where to connect it. If the elements do not exist
	 * they will be created. If the element exists the blueprint will be aggregated to a stack of the same type.
	 */
	@Override
	public void createContentHierarchy() {
		Log.i("IndustryT1Blueprints", ">> IndustryT1Blueprints.createContentHierarchy");
		// Clear the current list of elements.
		_root.clear();

		// Get the AssetsManager through the Store.
		final ArrayList<NeoComBlueprint> bps = _store.getPilot().getAssetsManager().searchT1Blueprints();
		for (final NeoComBlueprint currentbpc : bps) {
			final long locid = currentbpc.getLocationID();
			final NeoComAsset parent = currentbpc.getParentContainer();
			final BlueprintPart bppart = new BlueprintPart(currentbpc);
			bppart.setActivity(ModelWideConstants.activities.MANUFACTURING);
			bppart.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY);
			if (null == parent)
				this.add2Location(locid, bppart);
			else
				this.add2Container(parent, bppart);
		}
		Log.i("IndustryT2Blueprints", "<< IndustryT2Blueprints.createContentHierarchy [" + _root.size() + "]");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		AbstractDataSource.logger.info(">> IndustryT1Blueprints.getPartHierarchy");
		Collections.sort(_root, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		final ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (final AbstractAndroidPart node : _root) {
			result.add(node);
			// Check if the node is expanded. Then add its children.
			// Check if the node is expanded. Then add its children.
			if (node.isExpanded()) {
				ArrayList<IPart> grand = node.collaborate2View();
				Collections.sort(grand, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NAME));
				for (IPart part : grand)
					result.add((AbstractAndroidPart) part);
				result.add(new TerminatorPart(new Separator("")));
			}
		}
		_adapterData = result;
		AbstractDataSource.logger.info("<< IndustryT1Blueprints.getPartHierarchy");
		return result;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE))
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
	}

	/**
	 * Adding to a container is like adding to a location. The location is the container's location but the name
	 * changes to the containers name. So create a <code>LocationBlueprintPart</code> but set the container
	 * field. We also change the aggregation algorithm to locate the corresponding asset container on another
	 * list.
	 * 
	 * @param container
	 * @param part
	 */
	private void add2Container(final NeoComAsset container, final BlueprintPart part) {
		final long cid = container.getDAOID();
		LocationIndustryPart lochit = locations.get(cid);
		if (null == lochit) {
			lochit = (LocationIndustryPart) new LocationIndustryPart(part.getCastedModel().getLocation())
					.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY);
			lochit.setContainerLocation(true);
			final String containername = container.getUserLabel();
			if (null == containername)
				lochit.setContainerName("#" + container.getAssetID());
			else
				lochit.setContainerName(containername);
			locations.put(cid, lochit);
			_root.add(lochit);
		}
		lochit.addChild(part);
	}

	/**
	 * Checks of this locations already exists on the table and if not found then creates a new LocationPart
	 * branch and adds to it the parameter Part.
	 * 
	 * @param locationid
	 * 
	 * @param part
	 *          part to be added to the locations. May be an asset or a container.
	 */
	private void add2Location(final long locationid, final BlueprintPart part) {
		// Check if the location is already on the array.
		LocationIndustryPart hit = locations.get(locationid);
		if (null == hit) {
			hit = (LocationIndustryPart) new LocationIndustryPart(part.getCastedModel().getLocation())
					.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY);
			locations.put(locationid, hit);
			_root.add(hit);
		}
		hit.addChild(part);
	}
}

// - UNUSED CODE ............................................................................................
