//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.eveonline.neocom.datasource;

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
import org.dimensinfin.eveonline.neocom.NeoComApp;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.part.BlueprintPart;
import org.dimensinfin.eveonline.neocom.part.LocationIndustryPart;
import org.dimensinfin.eveonline.neocom.part.LocationPart;
import org.dimensinfin.eveonline.neocom.part.TerminatorPart;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This data source will generate the list of blueprints that are suitable for invention. It starts with the
 * list of blueprints of tech 1. But not all will be used on invention so it will then be limted to places
 * where there are Datacores.
 * 
 * @author Adam Antinoo
 */
public class IndustryT2InventionDataSource extends AbstractDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long													serialVersionUID	= 9094727714659835456L;

	// - F I E L D - S E C T I O N ............................................................................
	private AppModelStore															_store						= null;
	private final HashMap<Long, LocationIndustryPart>	locations					= new HashMap<Long, LocationIndustryPart>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public IndustryT2InventionDataSource(final AppModelStore store) {
		if (null != store) _store = store;
	}

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * .
	 */
	@Override
	public void createContentHierarchy() {
		Log.i("EVEI", ">> IndustryT2InventionDataSource.createContentHierarchy");
		// Clear the current list of elements.
		_root.clear();

		// Get the blueprints through the Store. And also the datacores.
		AssetsManager manager = _store.getPilot().getAssetsManager();
		ArrayList<NeoComAsset> datacores = manager.searchAsset4Group(ModelWideConstants.eveglobal.Datacores);
		ArrayList<NeoComBlueprint> bps = manager.searchT1Blueprints();
		for (NeoComBlueprint currentbpc : bps)
			// Check if the bp has the invention feature.
			if (currentbpc.getItem().hasInvention()) {
				long locid = currentbpc.getLocationID();
				NeoComAsset parent = currentbpc.getParentContainer();
				BlueprintPart bppart = new BlueprintPart(currentbpc);
				bppart.setActivity(ModelWideConstants.activities.MANUFACTURING);
				bppart.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTT2INVENTION);
				if (null == parent)
					this.add2Location(locid, bppart);
				else
					this.add2Container(parent, bppart);
			}

		// Filter our all the locations that do not contain datacores.
		for (LocationPart locationPart : locations.values()) {
			long stationID = locationPart.getCastedModel().getStationID();
			for (NeoComAsset datacore : datacores)
				if (datacore.getLocation().getStationID() == stationID) {
					_root.add(locationPart);
					break;
				}
		}
		Log.i("EVEI", "<< IndustryT2InventionDataSource.createContentHierarchy [" + _root.size() + "]");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		AbstractDataSource.logger.info(">> IndustryT1Blueprints.getPartHierarchy");
		Collections.sort(_root, NeoComApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		for (AbstractAndroidPart node : _root) {
			result.add(node);
			// Check if the node is expanded. Then add its children.
			if (node.isExpanded()) {
				for (IPart part : node.collaborate2View())
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
		long cid = container.getDAOID();
		LocationIndustryPart lochit = locations.get(cid);
		if (null == lochit) {
			lochit = (LocationIndustryPart) new LocationIndustryPart(part.getCastedModel().getLocation())
					.setRenderMode(AppWideConstants.rendermodes.RENDER_BLUEPRINTINDUSTRY);
			lochit.setContainerLocation(true);
			String containername = container.getUserLabel();
			if (null == containername)
				lochit.setContainerName("#" + container.getAssetID());
			else
				lochit.setContainerName(containername);
			locations.put(cid, lochit);
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
		}
		hit.addChild(part);
	}
}

// - UNUSED CODE ............................................................................................
