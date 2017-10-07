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

import org.dimensinfin.android.model.INamed;
import org.dimensinfin.eveonline.neocom.connector.AppConnector;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComAsset;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;
import org.dimensinfin.eveonline.neocom.model.Region;

import com.fasterxml.jackson.annotation.JsonIgnore;

// - CLASS IMPLEMENTATION ...................................................................................
public class BlueprintManager extends AbstractManager implements INamed {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long											serialVersionUID		= 3375621346232249963L;

	// - F I E L D - S E C T I O N ............................................................................
	//	private final long												totalAssets							= -1;
	//	private long																	verificationAssetCount	= 0;
	//	public double																	totalAssetsValue				= 0.0;
	public int																		blueprintTotalCount	= -1;
	public int																		bpoCount						= -1;
	public int																		bpcCount						= -1;
	public final HashMap<Long, Region>						regions							= new HashMap<Long, Region>();
	private final HashMap<Long, EveLocation>			locations						= new HashMap<Long, EveLocation>();
	private final HashMap<Long, NeoComAsset>			containers					= new HashMap<Long, NeoComAsset>();
	@JsonIgnore
	public ArrayList<NeoComAsset>									blueprintAssetList	= new ArrayList<NeoComAsset>();
	public String																	iconName						= "industry.png";

	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	/** Used during the processing of the assets into the different structures. */
	@JsonIgnore
	private transient HashMap<Long, NeoComAsset>	assetMap						= new HashMap<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public BlueprintManager(final NeoComCharacter pilot) {
		super(pilot);
		// Get all the Planetary assets and classify them into lists.
		jsonClass = "BlueprintManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Reads from the database all the Planetary assets and classifies them depending on Locations.
	 */
	public void accessAllBlueprints() {
		//		try {
		// Initialize the model
		regions.clear();
		locations.clear();
		containers.clear();
		// Get all the assets of the Planetary categories.
		int assetCounter = 0;
		try {
			// Read all the assets for this character if not done already.
			blueprintAssetList = AppConnector.getDBConnector().searchAllBlueprintAssets(this.getPilot().getCharacterID());
			blueprintTotalCount = blueprintAssetList.size();
		} catch (final RuntimeException rex) {
			rex.printStackTrace();
		}
	}

	public String getOrderingName() {
		return "Industry Manager";
	}

	public BlueprintManager initialize() {
		this.accessAllBlueprints();
		return this;
	}
}

// - UNUSED CODE ............................................................................................
