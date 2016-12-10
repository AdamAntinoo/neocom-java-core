//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.evedroid.model;

import java.util.ArrayList;
// - IMPORT SECTION .........................................................................................
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.manager.AssetsManager;

// - CLASS IMPLEMENTATION ...................................................................................
public class NeoComCorporation extends NeoComCharacter {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = Logger.getLogger("NeoComCorporation.java");

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public NeoComCorporation() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Return the elements collaborated by this object. For a Character it depends on the implementation being a Pilot or
	 * a Corporation. For a Pilot the result depends on the variant received as the parameter
	 */
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		return results;
	}
	protected  void getAllAssets() {
		try {
			// Initialize the model
			_dataModelRoot = new RootNode();
			regions = new LongSparseArray<Region>();
			locations = new LongSparseArray<EveLocation>();
			containers = new LongSparseArray<Asset>();
			// Read all the assets for this character is not done already.
			AppModelStore store = EVEDroidApp.getAppStore();
			// Get the full list of assets for this pilot.
			final AssetsManager manager = store.getPilot().getAssetsManager();
			ArrayList<Asset> assets = manager.getAllAssets();
			// Move the list to a processing map.
			assetMap = new LongSparseArray<Asset>(assets.size());
			for (Asset asset : assets) {
				assetMap.put(asset.getAssetID(), asset);
			}
			// Process the map until all elements are removed.
			try {
				Long key = assetMap.keyAt(0);
				Asset point = assetMap.get(key);
				while (null != point) {
					processElement(point);
					key = assetMap.keyAt(0);
					point = assetMap.get(key);
				}
			} catch (NoSuchElementException nsee) {
				nsee.printStackTrace();
			}
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
			logger.severe(
					"RTEX> AssetsByLocationDataSource.collaborate2Model-There is a problem with the access to the Assets database when getting the Manager.");
		}
	}
}

// - UNUSED CODE ............................................................................................
