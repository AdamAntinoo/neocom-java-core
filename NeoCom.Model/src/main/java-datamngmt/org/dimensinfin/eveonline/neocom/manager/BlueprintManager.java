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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.dimensinfin.eveonline.neocom.connector.ModelAppConnector;
import org.dimensinfin.eveonline.neocom.constant.ModelWideConstants;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ExtendedLocation;
import org.dimensinfin.eveonline.neocom.model.NeoComBlueprint;
import org.joda.time.Duration;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

// - CLASS IMPLEMENTATION ...................................................................................
public class BlueprintManager extends AbstractManager {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long serialVersionUID = 3375621346232249963L;
	protected static transient Dao<NeoComBlueprint, String> blueprintDao = null;

	//public enum EBlueprintLevel{
	//	TECH_I, TECH_II, TECH_III
	//}
	// - CLASS IMPLEMENTATION ...................................................................................
	public static class BlueprintDelegate {
		// - S T A T I C - S E C T I O N ..........................................................................
		// - F I E L D - S E C T I O N ............................................................................
		//		private EBlueprintLevel level;
		private long characterId = -1;
		private String techLevel;
		@JsonIgnore
		public final List<NeoComBlueprint> blueprintAssetList = new Vector<NeoComBlueprint>();
		public final Hashtable<Long, ExtendedLocation> locations = new Hashtable<Long, ExtendedLocation>();

		// - C O N S T R U C T O R - S E C T I O N ................................................................
		public BlueprintDelegate (final long characterId, final String newtech) {
			this.characterId = characterId;
			techLevel = newtech;
		}

		// - M E T H O D - S E C T I O N ..........................................................................
		public int getBlueprintCount () {
			return blueprintAssetList.size();
		}

		/**
		 * Reads from the database all the Blueprints that match the Delegate tech level.
		 */
		public List<NeoComBlueprint> accessAllBlueprints () {
			// Get access to all ApiKey registers
			blueprintAssetList.clear();
			try {
				if ( null == blueprintDao ) blueprintDao = ModelAppConnector.getSingleton().getDBConnector().getBlueprintDao();
				QueryBuilder<NeoComBlueprint, String> queryBuilder = blueprintDao.queryBuilder();
				Where<NeoComBlueprint, String> where = queryBuilder.where();
				where.eq("tech", techLevel);
				PreparedQuery<NeoComBlueprint> preparedQuery = queryBuilder.prepare();
				blueprintAssetList.addAll(blueprintDao.query(preparedQuery));
			} catch (java.sql.SQLException sqle) {
				sqle.printStackTrace();
				logger
						.warning("W [BlueprintDelegate.accessAllBlueprints]> Exception reading all Blueprints. " + sqle.getMessage());
			}
			//			// Classify the keys on they matching Logins.
			//			Hashtable<String, Login> loginList = new Hashtable<String, Login>();
			//			for (NeoComBlueprint apiKey : blueprintList) {
			//				String name = apiKey.getLogin();
			//				// Search for this on the list before creating a new Login.
			//				Login hit = loginList.get(name);
			//				if (null == hit) {
			//					Login login = new Login(name).addKey(apiKey);
			//					loginList.put(name, login);
			//				} else {
			//					hit.addKey(apiKey);
			//				}
			//			}
			return blueprintAssetList;
		}

		public List<ExtendedLocation> getLocations () {
			if ( getBlueprintCount() < 1 ) accessAllBlueprints();
			locations.clear();
			for (NeoComBlueprint blue : blueprintAssetList) {
				ExtendedLocation hit = locations.get(blue.getLocationID());
				if ( null == hit ) {
					final EveLocation targetLocation = ModelAppConnector.getSingleton().getCCPDBConnector().searchLocationbyID(blue.getLocationID());
					// Convert the Location to a new Extended Location with the new Contents Manager.
					hit = new ExtendedLocation(characterId, targetLocation);
					hit.setContentManager(new BlueprintContentManager(hit));
					locations.put(blue.getLocationID(), hit);
				}
				((BlueprintContentManager) hit.getContentManager()).addBlueprint(blue);
			}
			return (List<ExtendedLocation>) locations.values();
		}
	}

	// - F I E L D - S E C T I O N ............................................................................
	public String iconName = "industry.png";
	//	private final Hashtable<Long, Region> regions = new Hashtable<Long, Region>();
	//	private final Hashtable<Long, EveLocation> locations = new Hashtable<Long, EveLocation>();
	//	private final Hashtable<Long, NeoComAsset> containers = new Hashtable<Long, NeoComAsset>();
	////	private int bpoCount = -1;
	////	private int bpcCount = -1;
	//	@JsonIgnore
	//	private List<NeoComAsset> blueprintAssetList = new Vector<NeoComAsset>();
	private final Vector<NeoComBlueprint> blueprintCache = new Vector<NeoComBlueprint>();
	private int blueprintTotalCount = -1;
	private BlueprintDelegate blueDelegateT1 = null;
	private BlueprintDelegate blueDelegateT2 = null;
	private BlueprintDelegate blueDelegateT3 = null;


	// - P R I V A T E   I N T E R C H A N G E   V A R I A B L E S
	//	/** Used during the processing of the assets into the different structures. */
	//	@JsonIgnore
	//	private transient HashMap<Long, NeoComAsset> assetMap = new HashMap<Long, NeoComAsset>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public BlueprintManager (final Credential credential) {
		super(credential);
		jsonClass = "BlueprintManager";
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public BlueprintManager initialize () {
		if ( !initialized ) {
			if ( null == blueDelegateT1 )
				blueDelegateT1 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechI);
			if ( null == blueDelegateT2 )
				blueDelegateT2 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechII);
			if ( null == blueDelegateT3 )
				blueDelegateT3 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechIII);
			// Just count the total numbers of blueprints.
			blueprintTotalCount = queryTotalBlueprintCount();
			initialized = true;
		}
		return this;
	}

	public List<NeoComBlueprint> accessAllT1Blueprints () {
		if ( null == blueDelegateT1 )
			blueDelegateT1 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechI);
		return blueDelegateT1.accessAllBlueprints();
	}
	public List<NeoComBlueprint> accessAllT2Blueprints () {
		if ( null == blueDelegateT2 )
			blueDelegateT2 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechII);
		return blueDelegateT2.accessAllBlueprints();
	}

	public BlueprintDelegate getT1Delegate () {
		if ( null == blueDelegateT1 )
			blueDelegateT1 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechI);
		return blueDelegateT1;
	}
	public BlueprintDelegate getT2Delegate () {
		if ( null == blueDelegateT2 )
			blueDelegateT2 = new BlueprintDelegate(getCredentialIdentifier(), ModelWideConstants.eveglobal.TechII);
		return blueDelegateT2;
	}

	/**
	 * Counts the total number of blueprints that belong to the target character.
	 */
	private int queryTotalBlueprintCount () {
		try {
			if ( null == blueprintDao ) blueprintDao = ModelAppConnector.getSingleton().getDBConnector().getBlueprintDao();
			QueryBuilder<NeoComBlueprint, String> queryBuilder = blueprintDao.queryBuilder();
			queryBuilder.setCountOf(true).where().eq("ownerID", getCredentialIdentifier());
			long totalAssets = blueprintDao.countOf(queryBuilder.prepare());
			return Long.valueOf(totalAssets).intValue();
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger
					.warning("W [BlueprintManager.queryTotalBlueprintCount]> Exception reading number of Blueprints."
							+ sqle.getMessage());
			return 0;
		}
	}

	public List<NeoComBlueprint> getBlueprints () {
		if ( null == blueprintCache ) {
			this.updateBlueprints();
		}
		if ( blueprintCache.size() == 0 ) {
			this.updateBlueprints();
		}
		return blueprintCache;
	}

	private void updateBlueprints () {
		logger.info(">> AssetsManager.updateBlueprints");
		try {
			ModelAppConnector.getSingleton().startChrono();
			Dao<NeoComBlueprint, String> blueprintDao = ModelAppConnector.getSingleton().getDBConnector().getBlueprintDao();
			QueryBuilder<NeoComBlueprint, String> queryBuilder = blueprintDao.queryBuilder();
			Where<NeoComBlueprint, String> where = queryBuilder.where();
			where.eq("ownerID", getCredentialIdentifier());
			PreparedQuery<NeoComBlueprint> preparedQuery = queryBuilder.prepare();
			blueprintCache.addAll(blueprintDao.query(preparedQuery));
			Duration lapse = ModelAppConnector.getSingleton().timeLapse();
			logger.info("~~ Time lapse for BLUEPRINT [SELECT OWNERID = " + getCredentialIdentifier()
					+ "] - " + lapse);
			// Check if the list is empty. Then force a refresh download.
			if ( blueprintCache.size() < 1 ) {
				//				this.getPilot().forceRefresh();
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		logger.info("<< AssetsManager.updateBlueprints [" + blueprintCache.size() + "]");
	}

	public NeoComBlueprint searchBlueprintByID (final long assetid) {
		for (NeoComBlueprint bp : this.getBlueprints()) {
			String refs = bp.getStackIDRefences();
			if ( refs.contains(Long.valueOf(assetid).toString()) ) return bp;
		}
		return null;
	}

	/**
	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T1
	 * blueprints. We expect this is not cost intensive because this function is called few times.
	 *
	 * @return list of T1 blueprints.
	 */
	public ArrayList<NeoComBlueprint> searchT1Blueprints () {
		ArrayList<NeoComBlueprint> blueprintList = new ArrayList<NeoComBlueprint>();
		for (NeoComBlueprint bp : this.getBlueprints())
			if ( bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechI) ) {
				blueprintList.add(bp);
			}
		return blueprintList;
	}

	/**
	 * From the list of blueprints returned from the AssetsManager we filter out all others that are not T2
	 * blueprints. We expect this is not cost intensive because this function is called few times.
	 *
	 * @return list of T2 blueprints.
	 */
	public ArrayList<NeoComBlueprint> searchT2Blueprints () {
		ArrayList<NeoComBlueprint> blueprintList = new ArrayList<NeoComBlueprint>();
		for (NeoComBlueprint bp : this.getBlueprints())
			if ( bp.getTech().equalsIgnoreCase(ModelWideConstants.eveglobal.TechII) ) {
				blueprintList.add(bp);
			}
		return blueprintList;
	}
}

// - UNUSED CODE ............................................................................................
