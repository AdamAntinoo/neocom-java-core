//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

//- IMPORT SECTION .........................................................................................
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.connector.IConnector;
import org.dimensinfin.eveonline.neocom.connector.IDatabaseConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;
import org.dimensinfin.eveonline.poc.connector.AbstractAppConnector;
import org.dimensinfin.eveonline.poc.connector.SpringDatabaseConnector;

// - CLASS IMPLEMENTATION ...................................................................................
public class POCPlanetaryApplication extends AbstractAppConnector {
	public enum ECategory {
		PlanetaryResources

	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger		= Logger.getLogger("POCPlanetaryApplication");
	private static POCPlanetaryApplication	singleton	= null;

	public static void main(String[] args) {
		// Create the application instance and make it run
		singleton = new POCPlanetaryApplication(args);
		AppConnector.setConnector(singleton);
		singleton.run();
	}

	// - F I E L D - S E C T I O N ............................................................................
	private SpringDatabaseConnector dbConnector = null;
	//	private long										itemIdSequence	= 1000000000000L;
	//	private long										locationID			= 20000547L;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public POCPlanetaryApplication(String[] args) {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public IDatabaseConnector getDBConnector() {
		if (null == dbConnector) dbConnector = new SpringDatabaseConnector();
		return dbConnector;
	}

	@Override
	public IConnector getSingleton() {
		return singleton;
	}

	/**
	 * This pretends to be the POC for a new Planetary Advisor that will take a character's set of Planetary
	 * Resources (on a single and probably predefined location) and try to get the most profitable processing
	 * and selling of the set. <br>
	 * The input is a list of resources and a tax value to be used for taxes and the result is the list of
	 * actions to do and the processing to setup to get that output, probably the time it get to get everything
	 * processed and the resulting income in ISK.
	 */
	public void run() {
		// Get some list of planetary resources of all kinds for testing.
		Vector<Resource> planetaryAssets = new Vector<Resource>();
		planetaryAssets.add(new Resource(2268, 1000000)); // Aqueous Liquids
		planetaryAssets.add(new Resource(2309, 800000)); // Ionic Solutions
		planetaryAssets.add(new Resource(2288, 600000)); // Carbon Compounds
		planetaryAssets.add(new Resource(2073, 800000)); // Micro Organisms

		planetaryAssets.add(new Resource(2397, 14600)); // Industrial Fibers
		planetaryAssets.add(new Resource(2395, 11800)); // Proteins

		planetaryAssets.add(new Resource(3691, 4278)); // Synthetic Oil
		planetaryAssets.add(new Resource(9838, 2814)); // Superconductors

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
		// Get the list of outputs.
		System.out.println(proc.getResources());
	}
}

// - UNUSED CODE ............................................................................................
