//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
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
import org.dimensinfin.eveonline.neocom.EVEDroidApp;
import org.dimensinfin.eveonline.neocom.constant.AppWideConstants;
import org.dimensinfin.eveonline.neocom.core.EveAbstractPart;
import org.dimensinfin.eveonline.neocom.enums.EIndustryGroup;
import org.dimensinfin.eveonline.neocom.factory.AbstractIndustryDataSource;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.Separator;
import org.dimensinfin.eveonline.neocom.part.AssetGroupPart;
import org.dimensinfin.eveonline.neocom.part.AssetPart;
import org.dimensinfin.eveonline.neocom.part.GroupPart;
import org.dimensinfin.eveonline.neocom.storage.AppModelStore;

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

	public RootNode collaborate2Model() {
		// TODO Auto-generated method stub
		return null;
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
			ArrayList<NeoComAsset> mineralResources = manager.searchAsset4Group("Mineral");
			for (NeoComAsset asset : mineralResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					mineralsGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}

			ArrayList<NeoComAsset> asteroidResources = manager.searchAsset4Category("Asteroid");
			for (NeoComAsset asset : asteroidResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					refinedMaterialGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}

			ArrayList<NeoComAsset> assetsPlanetaryCommodities = manager.searchAsset4Category("Planetary Commodities");
			ArrayList<NeoComAsset> assetsPlanetaryResources = manager.searchAsset4Category("Planetary Resources");
			for (NeoComAsset asset : assetsPlanetaryCommodities) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					planetaryGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}
			for (NeoComAsset asset : assetsPlanetaryResources) {
				// Check if there an entry for this asset name.
				AssetGroupPart hit = names.get(asset.getName());
				if (null == hit) {
					hit = new AssetGroupPart(asset);
					names.put(asset.getName(), hit);
					planetaryGroup.addChild(hit);
				}
				hit.addChild(new AssetPart(asset).setRenderMode(AppWideConstants.rendermodes.RENDER_LOCATIONMODE));
			}

			ArrayList<NeoComAsset> assetsSalvageResources = manager.searchAsset4Group("Salvaged Materials");
			for (NeoComAsset asset : assetsSalvageResources) {
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
			AbstractDataSource.logger
					.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		try {
			AssetsManager manager = AppModelStore.getSingleton().getPilot().getAssetsManager();
			// Depending on the Setting group Locations into Regions
		} catch (RuntimeException sqle) {
			sqle.printStackTrace();
			AbstractDataSource.logger
					.severe("E> There is a problem with the access to the Assets database when getting the Manager.");
		}
		Log.i("DataSource", "<< AssetsMaterialsDataSource.createHierarchy [" + _root.size() + "]");
	}

	@Override
	public ArrayList<AbstractAndroidPart> getBodyParts() {
		AbstractDataSource.logger.info(">> AssetsMaterialsDataSource.getPartHierarchy");
		ArrayList<AbstractAndroidPart> result = new ArrayList<AbstractAndroidPart>();
		try {
			for (AbstractAndroidPart node : _root) {
				if (node instanceof GroupPart) if (node.getChildren().size() == 0) {
					continue;
				}
				result.add(node);
				// Check if the node is expanded. Then add its children.
				if (node.isExpanded()) {
					ArrayList<IPart> grand = node.collaborate2View();
					Collections.sort(grand, EVEDroidApp.createPartComparator(AppWideConstants.comparators.COMPARATOR_NAME));
					for (IPart part : node.collaborate2View()) {
						result.add((AbstractAndroidPart) part);
					}
				}
			}
		} catch (RuntimeException rtex) {
			rtex.printStackTrace();
		}
		_adapterData = result;
		AbstractDataSource.logger.info("<< AssetsMaterialsDataSource.getPartHierarchy");
		return result;
	}

	public ArrayList<AbstractAndroidPart> getHeaderParts() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event) {
		if (event.getPropertyName().equalsIgnoreCase(SystemWideConstants.events.EVENTSTRUCTURE_ACTIONEXPANDCOLLAPSE)) {
			this.fireStructureChange(SystemWideConstants.events.EVENTADAPTER_REQUESTNOTIFYCHANGES, event.getOldValue(),
					event.getNewValue());
		}
	}
}
// - UNUSED CODE ............................................................................................
