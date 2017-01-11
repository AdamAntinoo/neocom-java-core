//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.model.EveItem;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;

// - CLASS IMPLEMENTATION ...................................................................................
public class POCPlanetaryApplication {
	public enum ECategory {
		PlanetaryResources

	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger		= Logger.getLogger("POCPlanetaryApplication");
	private static POCPlanetaryApplication	singleton	= null;

	public static void main(String[] args) {
		// Create the application instance and make it run
		singleton = new POCPlanetaryApplication(args);
		singleton.run();
	}

	// - F I E L D - S E C T I O N ............................................................................
	private long	itemIdSequence	= 1000000000000L;
	private long	locationID			= 20000547L;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public POCPlanetaryApplication(String[] args) {
	}

	private double calculateAssetValue(final NeoComAsset asset) {
		// Skip blueprints from the value calculations
		double assetValueISK = 0.0;
		if (null != asset) {
			EveItem item = asset.getItem();
			if (null != item) {
				String category = item.getCategory();
				String group = item.getGroupName();
				if (null != category) if (!category.equalsIgnoreCase(ModelWideConstants.eveglobal.Blueprint)) {
					// Add the value and volume of the stack to the global result.
					long quantity = asset.getQuantity();
					double price = asset.getItem().getHighestBuyerPrice().getPrice();
					assetValueISK = price * quantity;
				}
			}
		}
		return assetValueISK;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Creates an extended app asset from the asset created by the eveapi on the download of CCP information.
	 * <br>
	 * This method checks the location to detect if under the new flat model the location is an asset and then
	 * we should convert it into a parent or the location is a real location. Initially this is done checking
	 * the location id value if under 1000000000000.
	 * 
	 * @param eveAsset
	 *          the original assest as downloaded from CCP api
	 * @return
	 */
	private NeoComAsset createAsset(int typeID, int quantity) {
		// Create the asset from the API asset.
		final NeoComAsset newAsset = new NeoComAsset();
		newAsset.setAssetID(itemIdSequence++);
		newAsset.setTypeID(typeID);
		newAsset.setLocationID(locationID);
		newAsset.setParentId(-1);
		newAsset.setQuantity(quantity);
		newAsset.setSingleton(false);

		// Get access to the Item and update the copied fields.
		final EveItem item = AppConnector.getDBConnector().searchItembyID(newAsset.getTypeID());
		if (null != item) {
			try {
				newAsset.setName(item.getName());
				newAsset.setCategory(item.getCategory());
				newAsset.setGroupName(item.getGroupName());
				newAsset.setTech(item.getTech());
			} catch (RuntimeException rtex) {
			}
		}
		// Add the asset value to the database.
		newAsset.setIskvalue(this.calculateAssetValue(newAsset));
		return newAsset;
	}

	private NeoComAsset processRaw(NeoComAsset neoComAsset) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This pretends to be the POC for a new Planetary Advisor that will take a character's set of Planetary
	 * Resources (on a single and probably predefined location) and try to get the most profitable processing
	 * and selling of the set. <br>
	 * The input is a list of resources and a tax value to be used for taxes and the result is the list of
	 * actions to do and the processing to setup to get that output, probably the time it get to get everything
	 * processed and the resulting income in ISK.
	 */
	private void run() {
		// Get some list of planetary resources of all kinds.
		Vector<NeoComAsset> planetaryAssets = new Vector<NeoComAsset>();
		NeoComAsset pa = createAsset(2268, 1000000);
		planetaryAssets.add(pa);
		pa = createAsset(2393, 8640);
		planetaryAssets.add(pa);
		pa = createAsset(2267, 1000000);
		planetaryAssets.add(pa);
		pa = createAsset(2329, 6520);
		planetaryAssets.add(pa);
		pa = createAsset(2869, 19);
		planetaryAssets.add(pa);
		pa = createAsset(17136, 253);
		planetaryAssets.add(pa);
		pa = createAsset(9838, 7484);
		planetaryAssets.add(pa);
		pa = createAsset(3891, 4890);
		planetaryAssets.add(pa);
		pa = createAsset(3693, 1445);
		planetaryAssets.add(pa);
		pa = createAsset(3695, 7573);
		planetaryAssets.add(pa);
		pa = createAsset(2287, 2000000);
		planetaryAssets.add(pa);
		pa = createAsset(2288, 2000000);
		planetaryAssets.add(pa);
		pa = createAsset(2308, 2000000);
		planetaryAssets.add(pa);
		pa = createAsset(3645, 69886);
		planetaryAssets.add(pa);
		pa = createAsset(2390, 13320);
		planetaryAssets.add(pa);
		pa = createAsset(2310, 2245474);
		planetaryAssets.add(pa);

		// Process Raw resources to Tier 1 and then  start to process the set.
		for (NeoComAsset neoComAsset : planetaryAssets) {
			if (neoComAsset.getCategory().equalsIgnoreCase("Planetary Resources")) {
				NeoComAsset transform = processRaw(neoComAsset);
			}
		}
		// Create the initial processing point and start the optimization recursively.
		// Print the output
	}
}

// - UNUSED CODE ............................................................................................
