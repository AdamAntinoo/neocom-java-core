//	PROJECT:        POC (POC)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Java 1.7.
//	DESCRIPTION:		Projects for Proof Of Concept desings.
package org.dimensinfin.eveonline.poc.planetary;

//- IMPORT SECTION .........................................................................................
import java.awt.Cursor;
import java.util.HashMap;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.industry.Resource;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryProcessor {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger				= Logger.getLogger("org.dimensinfin.eveonline.poc.planetary");
	private static HashMap<Integer, String>	t2ProductList	= new HashMap<Integer, String>();
	// Refined Commodities
	static {
		t2ProductList.put(2329, "Biocells");
		t2ProductList.put(3828, "Construction Blocks");
		t2ProductList.put(9836, "Consumer Electronics");
		t2ProductList.put(9832, "Coolant");
		t2ProductList.put(44, "Enriched Uranium");
		t2ProductList.put(3693, "Fertilizer");
		t2ProductList.put(15317, "Genetically Enhanced Livestock");
		t2ProductList.put(3725, "Livestock");
		t2ProductList.put(3689, "Mechanical Parts");
		t2ProductList.put(2327, "Microfiber Shielding");
		t2ProductList.put(9842, "Miniature Electronics");
		t2ProductList.put(2463, "Nanites");
		t2ProductList.put(2317, "Oxides");
		t2ProductList.put(2321, "Polyaramids");
		t2ProductList.put(3695, "Polytextiles");
		t2ProductList.put(9830, "Rocket Fuel");
		t2ProductList.put(3697, "Silicate Glass");
		t2ProductList.put(9838, "Superconductors");
		t2ProductList.put(2312, "Supertensile Plastics");
		t2ProductList.put(3691, "Synthetic Oil");
		t2ProductList.put(2319, "Test Cultures");
		t2ProductList.put(9840, "Transmitter");
		t2ProductList.put(3775, "Viral Agent");
		t2ProductList.put(2328, "Water-Cooled CPU");
	}
	private static HashMap<Integer, String> t3ProductList = new HashMap<Integer, String>();
	// Specialized Commodities
	static {
		t3ProductList.put(2358, "Biotech Research Reports");
		t3ProductList.put(2345, "Camera Drones");
		t3ProductList.put(2344, "Condensates");
		t3ProductList.put(2367, "Cryoprotectant Solution");
		t3ProductList.put(17392, "Data Chips");
		t3ProductList.put(2348, "Gel-Matrix Biopaste");
		t3ProductList.put(9834, "Guidance Systems");
		t3ProductList.put(2366, "Hazmat Detection Systems");
		t3ProductList.put(2361, "Hermetic Membranes");
		t3ProductList.put(17898, "High-Tech Transmitters");
		t3ProductList.put(2360, "Industrial Explosives");
		t3ProductList.put(2354, "Neocoms");
		t3ProductList.put(2352, "Nuclear Reactors");
		t3ProductList.put(9846, "Planetary Vehicles");
		t3ProductList.put(9848, "Robotics");
		t3ProductList.put(2351, "Smartfab Units");
		t3ProductList.put(2349, "Supercomputers");
		t3ProductList.put(2346, "Synthetic Synapses");
		t3ProductList.put(12836, "Transcranial Microcontrollers");
		t3ProductList.put(17136, "Ukomi Superconductors");
		t3ProductList.put(28974, "Vaccines");
	}
	private static HashMap<Integer, String> t4ProductList = new HashMap<Integer, String>();
	// Advanced Commodities
	static {
		t4ProductList.put(2867, "Broadcast Node");
		t4ProductList.put(2868, "Integrity Response Drones");
		t4ProductList.put(2869, "Nano-Factory");
		t4ProductList.put(2870, "Organic Mortar Applicators");
		t4ProductList.put(2871, "Recursive Computing Module");
		t4ProductList.put(2872, "Self-Harmonizing Power Core");
		t4ProductList.put(2875, "Sterile Conduits");
		t4ProductList.put(2876, "Wetware Mainframe");
	}
	//	private static final String	SELECT_RAW_PRODUCTRESULT			= "SELECT ps.schematicName AS productName, ps.cycleTime AS cycleTime, pstm.quantity AS inputQuantity"
	//			+ " FROM  planetSchematicsTypeMap pstm, planetSchematics ps" + " WHERE pstm.isInput " + " AND   pstm.typeID = ?"
	//			+ " AND   ps.schematicID = pstm.schematicID";
	//
	//	private static final String	SELECT_RAW_PRODUCTRESULT_INV	= "SELECT ps.schematicName AS productName, ps.cycleTime AS cycleTime, pstm.quantity AS inputQuantity"
	//			+ " FROM  planetSchematicsTypeMap pstm" + " WHERE pstm.isInput AND   pstm.typeID = ?"
	//			+ " LEFT OUTER JOIN planetSchematics ps ON ps.schematicID = pstm.schematicID";
	//
	//	private final static String	DATABASE_URL									= "jdbc:h2:mem:account";

	// - M E T H O D - S E C T I O N ..........................................................................
	public static void process(Resource inResource) {
		try {
			final Cursor cursor = this.getCCPDatabase().rawQuery(SELECT_RAW_PRODUCTRESULT,
					new String[] { Integer.valueOf(inResource.getTypeID()).toString() });
			if (null != cursor) {
				while (cursor.moveToNext()) {
					// The the data of the resource. Check for blueprints.
					int resourceID = cursor.getInt(cursor.getColumnIndex("materialTypeID"));
					int qty = cursor.getInt(cursor.getColumnIndex("quantity"));
					// EveItem newItem = searchItembyID(resourceID);
					// Resource resource = ;
					inventionJob.add(new Resource(resourceID, qty));
				}
				cursor.close();
			}
		} catch (final Exception ex) {
			AndroidDatabaseConnector.logger.severe("E> Error searching for material <" + itemID + "> not found.");
		}
	}

	// - F I E L D - S E C T I O N ............................................................................
	private PlanetaryScenery scenery = null;
	//	private SQLiteDatabase				ccpDatabase				= null;
	//
	//	private Dao<Account, Integer>	accountDao;
	//	String												databaseUrl				= "jdbc:h2:mem:account";
	//	// create a connection source to our database
	//	ConnectionSource							connectionSource	= new JdbcConnectionSource(databaseUrl);

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	/**
	 * Create a new processor and connect it to an scenery. The creation does nothing important.
	 * 
	 * @param scenery
	 */
	public PlanetaryProcessor(PlanetaryScenery scenery) {
		this.scenery = scenery;
	}

	public PlanetaryProcessor startProfitSearch(PlanetaryProcessor currentTarget) {
		// If current target is null this is then the first iteration on the search.
		if (null == currentTarget) {
			// Search for Tier2 optimizations
			for (Integer target : t2ProductList.keySet()) {
				// Check if this can be processed with current T1 resources.
				Vector<Integer> inputList = AppConnector.getDBConnector().getInputResources(target);
				// Check the list against the scenery resources.
				Vector<PlanetaryResource> inputs = new Vector<PlanetaryResource>();
				for (Object inputResourceId : inputList) {
					inputs.addElement(scenery.getResource(inputResourceId));
				}
			}
		}
		return null;
	}

}

// - UNUSED CODE ............................................................................................
