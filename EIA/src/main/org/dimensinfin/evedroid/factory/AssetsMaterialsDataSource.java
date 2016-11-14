//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.factory;

// - IMPORT SECTION .........................................................................................
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.dimensinfin.android.mvc.constants.SystemWideConstants;
import org.dimensinfin.android.mvc.core.AbstractAndroidPart;
import org.dimensinfin.evedroid.EVEDroidApp;
import org.dimensinfin.evedroid.constant.AppWideConstants;
import org.dimensinfin.evedroid.core.EIndustryGroup;
import org.dimensinfin.evedroid.core.EveAbstractPart;
import org.dimensinfin.evedroid.manager.AssetsManager;
import org.dimensinfin.evedroid.model.Asset;
import org.dimensinfin.evedroid.model.Separator;
import org.dimensinfin.evedroid.part.AssetGroupPart;
import org.dimensinfin.evedroid.part.AssetPart;
import org.dimensinfin.evedroid.part.GroupPart;
import org.dimensinfin.evedroid.storage.AppModelStore;

import android.util.Log;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * DataSource to retrieve the resources required to perform a T2 Manufacture job from a parameter stack. That
 * stack will contain the pack of T2 BPC to be used on the manufacture process. This is NOT a single blueprint
 * but a pack that can contain a set of equal or similar blueprints. We do not consider differences between
 * them and will use the first hit to search for the ME/TE parameters.<br>
 * The hierarchy are the Actions to complete the job pack. Those actions will contain Tasks that are completed
 * if there are enough resources at the blueprint location or other activities needed to complete the task and
 * then the action.<br>
 * The locations to take on account are the location where is located the blueprint stack.<br>
 * The resources are the character available resources at that location less the resources used on scheduled
 * jobs. Scheduled jobs are only valid at the <code>MANUFACTURE</code> location
 * 
 * @author Adam Antinoo
 */
public class AssetsMaterialsDataSource extends AbstractIndustryDataSource {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long											serialVersionUID	= -1904434849082581300L;

	// - F I E L D - S E C T I O N ............................................................................
	private final HashMap<String, AssetGroupPart>	names							= new HashMap<String, AssetGroupPart>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AssetsMaterialsDataSource(final AppModelStore store) {
		super(store);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * The hierarchy contains two levels of elements. The first level are the actions and the second level are
	 * the tasks to complete and fulfill those actions.
	 */
	@Override
	public void createContentHierarchy() {
		Log.i("DataSource", ">> AssetsMaterialsDataSource.createHierarchy");
		// Clear the current list of elements.
		_root.clear();

		EveAbstractPart mineralsGroup = new GroupPart(new Separator(EIndustryGroup.MINERAL.toString())).setPriority(100);
		EveAbstractPart refinedMaterialGroup = new GroupPart(new Separator(EIndustryGroup.REFINEDMATERIAL.toString()))
				.setPriority(200);
		EveAbstractPart planetaryGroup = new GroupPart(new Separator(EIndustryGroup.PLANETARYMATERIALS.toString()))
				.setPriority(300);
		EveAbstractPart reactionGroup = new GroupPart(new Separator(EIndustryGroup.REACTIONMATERIALS.toString()))
				.setPriority(400);
		EveAbstractPart salvagedGroup = new GroupPart(new Separator(EIndustryGroup.SALVAGEDMATERIAL.toString()))
				.setPriority(500);
		_root.add(mineralsGroup);
		_root.add(refinedMaterialGroup);
		_root.add(planetaryGroup);
		_root.add(reactionGroup);
		_root.add(salvagedGroup);

		// Get the list of Locations for this Pilot.
		try {
			AssetsManager manager = _store.getPilot().getAssetsManager();
			ArrayList<Asset> mineralResources = manager.searchAsset4Group("Mineral");
			for (Asset asset : mineralResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					mineralsGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}

			ArrayList<Asset> asteroidResources = manager.searchAsset4Category("Asteroid");
			for (Asset asset : asteroidResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					refinedMaterialGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}

			ArrayList<Asset> assetsPlanetaryCommodities = manager.searchAsset4Category("Planetary Commodities");
			ArrayList<Asset> assetsPlanetaryResources = manager.searchAsset4Category("Planetary Resources");
			for (Asset asset : assetsPlanetaryCommodities) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					planetaryGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}
			for (Asset asset : assetsPlanetaryResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					planetaryGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}

			ArrayList<Asset> assetsSalvageResources = manager.searchAsset4Group("Salvaged Materials");
			for (Asset asset : assetsSalvageResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					salvagedGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}
		} catch (RuntimeException sqle) {
			sqle.printStackTrace();
			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		try {
			AssetsManager manager = DataSourceFactory.getPilot().getAssetsManager();
			// Depending on the Setting group Locations into Regions
		} catch (RuntimeException sqle) {
			sqle.printStackTrace();
			logger.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		Log.i("DataSource", "<< AssetsMaterialsDataSource.createHierarchy [" + _root.size() + "]");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getPartHierarchy() {
		logger.info(">> AssetsMaterialsDataSource.getPartHierarchy");
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		try {
			for (AbstractAndroidPart node : _root) {
				if (node instanceof GroupPart) if (node.getChildren().size() == 0) {
					continue;
				}
				result.add(node);
				// Check if the node is expanded. Then add its children.
				if (node.isExpanded()) {
					ArrayList<AbstractAndroidPart> grand = node.getPartChildren();
					Collections.sort(grand, EVEDroidApp.createComparator(AppWideConstants.comparators.COMPARATOR_NAME));
					result.addAll(grand);
				}
			}
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}
		_adapterData = result;
		logger.info("<< AssetsMaterialsDataSource.getPartHierarchy");
		return result;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}
}
// - UNUSED CODE ............................................................................................
