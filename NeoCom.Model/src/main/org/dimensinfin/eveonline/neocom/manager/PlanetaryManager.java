//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.interfaces.INamed;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Region;

// - CLASS IMPLEMENTATION ...................................................................................
public class PlanetaryManager extends AbstractManager implements INamed {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long									serialVersionUID				= 3794750126425122302L;
	private static Logger											logger									= Logger.getLogger("PlanetaryManager");

	// - F I E L D - S E C T I O N ............................................................................
	private final long												totalAssets							= -1;
	private final long												verificationAssetCount	= 0;
	private final double											totalAssetsValue				= 0.0;
	private final HashMap<Long, Region>				regions									= new HashMap<Long, Region>();
	private final HashMap<Long, EveLocation>	locations								= new HashMap<Long, EveLocation>();
	private final HashMap<Long, NeoComAsset>	containers							= new HashMap<Long, NeoComAsset>();
	private ArrayList<NeoComAsset>						planetaryAssetList			= new ArrayList<NeoComAsset>();
	public String															iconName;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public PlanetaryManager(final NeoComCharacter pilot) {
		super(pilot);
		// Get all the Planetary assets and classify them into lists.
		this.accessAllAssets();
		jsonClassname = "PlanetaryManager";
		iconName = "planets.png";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Reads from the database all the Planetary assets and classifies them depending on Locations.
	 */
	public void accessAllAssets() {
		//		try {
		// Initialize the model
		regions.clear();
		locations.clear();
		containers.clear();
		// Get all the assets of the Planetary categories.
		planetaryAssetList = AppConnector.getDBConnector().searchAllPlanetaryAssets(this.getPilot().getCharacterID());
	}

	public long getAssetTotalCount() {
		return planetaryAssetList.size();
	}

	public String getOrderingName() {
		return "Planetary Manager";
	}
}

// - UNUSED CODE ............................................................................................
