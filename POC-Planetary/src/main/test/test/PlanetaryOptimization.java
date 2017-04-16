//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package test;

import java.util.Vector;
// - IMPORT SECTION .........................................................................................
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.poc.planetary.PlanetaryProcessor;
import org.dimensinfin.eveonline.poc.planetary.PlanetaryScenery;
import org.dimensinfin.eveonline.poc.planetary.ProcessingAction;
import org.junit.Before;
import org.junit.Test;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryOptimization {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger			logger					= Logger.getLogger("PlanetaryOptimization");

	// - F I E L D - S E C T I O N ............................................................................
	private Vector<Resource>	planetaryAssets	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryOptimization() {
	}

	@Before
	public void setUp() throws Exception {
		// Create the list of resources to use for testing.
		planetaryAssets = new Vector<Resource>();
		// RAW Resources
		planetaryAssets.add(new Resource(2268, 120000)); // Aqueous Liquids
		planetaryAssets.add(new Resource(2267, 240000)); // Base Metals
		planetaryAssets.add(new Resource(2272, 240000)); // Heavy Metals
		planetaryAssets.add(new Resource(2306, 240000)); // Non-CS Crystals
		planetaryAssets.add(new Resource(2307, 240000)); // Felsic Magma
		planetaryAssets.add(new Resource(2287, 240000)); // Complex Organisms
		planetaryAssets.add(new Resource(2288, 240000)); // Carbon Compounds
		planetaryAssets.add(new Resource(2073, 240000)); // Micro Organisms
		// Tier 1 Resources
		planetaryAssets.add(new Resource(2395, 1600)); // Proteins
		planetaryAssets.add(new Resource(2398, 1000)); // Reactive Metals
		planetaryAssets.add(new Resource(2400, 1000)); // Toxic Metals
		planetaryAssets.add(new Resource(3779, 600)); // Biomass
		planetaryAssets.add(new Resource(3545, 200)); // Water
		// Tier 2 Resources
		planetaryAssets.add(new Resource(3725, 100)); // Livestock
	}

	@Test
	public void testStartProfitSearch() {
		// The Planetary Advisor requires a list of Planetary Resources to be stocked to start the profit calculations.
		PlanetaryScenery scenery = new PlanetaryScenery();
		scenery.stock(planetaryAssets);

		// Create the initial processing point and start the optimization recursively.
		PlanetaryProcessor proc = new PlanetaryProcessor(scenery);
		// Start running the best profit search.
		Vector<ProcessingAction> bestScenario = proc.startProfitSearch(null);
		// Print the output
		for (ProcessingAction action : bestScenario) {
			System.out.println(action.toString());
		}

	}

	// - M E T H O D - S E C T I O N ..........................................................................
}

// - UNUSED CODE ............................................................................................
