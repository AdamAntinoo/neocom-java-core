//  PROJECT:        EveDroid
//  AUTHORS:        Adam Antinoo - haddockgit@gmail.com
//  COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.

package org.dimensinfin.evedroid.industry;

// - IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.evedroid.connector.AppConnector;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * The responsibility is to collect items for refining and return the refined result. Initially it will only
 * work with ore resources but perhaps it should be able to handle reprocessing of modules and items.<br>
 * The API will allow to create a session where the owner will queue stacks of elements. At any time it will
 * retrieve the result of the refining at the selected place. Because there is no idea of how to manage
 * standings and refining perfection this calls will request the user or the external owner about the
 * percentage of refining that has to be used on the calculations.
 * 
 * @author Adam Antinoo
 */
public class Refinery {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger								logger													= Logger.getLogger("Refinery");
	private static final int						RefiningSkillLevel							= 5;
	private static final int						RefiningEfficiencySkillLevel		= 5;
	private static final int						OreSpecificProcessingSkillLevel	= 4;

	// - F I E L D - S E C T I O N ............................................................................
	private HashMap<Integer, Resource>	entryResources									= new HashMap<Integer, Resource>();
	private HashMap<Integer, Resource>	exitResources										= new HashMap<Integer, Resource>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Refinery() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public void addResource(Resource moreResource) {
		int newid = moreResource.getTypeID();
		Resource stack = entryResources.get(newid);
		if (null == stack) entryResources.put(newid, moreResource);
		stack.addition(moreResource);
	}

	/**
	 * This method will perform all the calculations on the current session and return the result of the
	 * refining. The set returned will include the rest of the ore not processed because of packing depending of
	 * a parameter flag. By default not sending the flag will return the ore pending refining.
	 * 
	 * @return
	 */
	public ArrayList<Resource> refine() {
		HashMap<Integer, Resource> result = new HashMap<Integer, Resource>();
		for (Resource resource : entryResources.values()) {
			// Get access to the refining parameters
			ArrayList<Resource> refineParameters = AppConnector.getDBConnector().refineOre(resource.item.getItemID());

			// Calculate the quantity to refine.
			double refineqty = 0.0;
			double mineralObtained = 0.0;
			for (Resource refiner : refineParameters) {
				long portionsize = refiner.getStackSize();
				long resultQuantity = refiner.getBaseQuantity();
				long refineQty = resource.getQuantity();
				double refinePercentage = .85;
				int refinedResult = new Double(Math.floor(Math.floor(refineQty / portionsize) * resultQuantity
						* getRefiningYield())).intValue();
				addProduct(new Resource(refiner.item.getItemID(), refinedResult));

				//			double changeRefineQty = 0;
				//				refineqty = ((mineralRequested * portionsize) / (refineQuantity * 0.8)) + portionsize;
				//				logger.info("-- Quantity to refine: " + refineqty);
				//			oreSelected.setCount(0);
				//				registerAssetChange(-changeRefineQty, oreSelected.getItemID(), oreSelected.getLocationID());
				//				for (Resource rc : refineParameters) {
				//					double mineral = (changeRefineQty / portionsize) * (0.8 * refineQuantity);
				//					if (rc.getTypeID() == mineralCode) mineralObtained = mineral;
				//					registerAssetChange(mineral, rc.item.getItemID(), oreSelected.getLocationID());
				//				}
				//		}
			}
		}

		return new ArrayList<Resource>(result.values());
	}

	private void addProduct(Resource newProduct) {
		int newid = newProduct.getTypeID();
		Resource stack = exitResources.get(newid);
		if (null == stack) exitResources.put(newid, newProduct);
		stack.addition(newProduct);
	}

	private double getRefiningYield() {
		double EffectiveRefiningYield = .5 + (0.375 * (1 + (RefiningSkillLevel * 0.02))
				* (1 + (RefiningEfficiencySkillLevel * 0.04)) * (1 + (OreSpecificProcessingSkillLevel * 0.05)));
		return EffectiveRefiningYield;
	}
}

// - UNUSED CODE ............................................................................................
