//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.datamngmt;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dimensinfin.eveonline.neocom.database.INeoComDBHelper;
import org.dimensinfin.eveonline.neocom.entities.Credential;
import org.dimensinfin.eveonline.neocom.entities.Job;
import org.dimensinfin.eveonline.neocom.entities.MarketOrder;
import org.dimensinfin.eveonline.neocom.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.entities.NeoComAsset;
import org.dimensinfin.eveonline.neocom.entities.Property;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.exception.NEOE;
import org.dimensinfin.eveonline.neocom.exception.NeoComRegisteredException;
import org.dimensinfin.eveonline.neocom.model.AllianceV1;
import org.dimensinfin.eveonline.neocom.model.CorporationV1;
import org.dimensinfin.eveonline.neocom.model.PilotV2;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

/**
 * @author Adam Antinoo
 */
// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerDataAccess extends GlobalDataManagerNetwork {
	// - S T A T I C - S E C T I O N ..........................................................................
//	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerDataAccess");

	// --- M O D E L - S T O R E   I N T E R F A C E
	//--- ALLIANCE
	//	public static AllianceV1 reachAllianceV1( final int identifier, final SessionContext context ) {
	public AllianceV1 requestAllianceV1( final int allianceIdentifier, final Credential credential ) {
		logger.info(">> [GlobalDataManager.requestAllianceV1]> Identifier: {}", credential.getAccountId());
		try {
			//			// Check if this request is already available on the cache.
			//			final ICollaboration hit = modelCache.access(EModelVariants.ALLIANCEV1, identifier);
			//			if (null == hit) {
			//				logger.info("-- [GlobalDataManager.reachAllianceV1]> Instance not found at cache. Downloading Alliance <{}> info.",
			//						identifier);
			final AllianceV1 newalliance = new AllianceV1();
			// Get the credential from the Store.
			//				final Credential credential = context.getCredential();

			// Corporation information.
			logger.info("-- [GlobalDataManager.requestAllianceV1]> ESI Compatible. Download corporation information.");
			final GetAlliancesAllianceIdOk publicData = this.esiAdapter.getAlliancesAllianceId(Long.valueOf(allianceIdentifier)
					                                                                                     .intValue()
					, credential.getRefreshToken()
					, credential.getDataSource());
			newalliance.setAllianceId(allianceIdentifier)
					.setPublicData(publicData);
			//					.setExecutorCorporation(GlobalDataManager.requestCorporationV1(publicData.getExecutorCorporationId(), credential));
			return newalliance;
			//			} else {
			//				logger.info("-- [GlobalDataManager.requestAllianceV1]> Alliance <{}> found at cache.", identifier);
			//				return (AllianceV1) hit;
			//			}
		} finally {
			logger.info("<< [GlobalDataManager.requestAllianceV1]");
		}
	}

	//--- CORPORATION
	//	public static CorporationV1 reachCorporationV1( final int identifier, final SessionContext context ) {
	public CorporationV1 requestCorporationV1( final int corpIdentifier, final Credential credential ) {
		logger.info(">> [GlobalDataManager.requestCorporationV1]> Identifier: {}", credential.getAccountId());
		try {
			// Check if this request is already available on the cache.
			//			final ICollaboration hit = modelCache.access(EModelVariants.CORPORATIONV1, identifier);
			//			if (null == hit) {
			//				logger.info("-- [GlobalDataManager.reachCorporationV1]> Instance not found at cache. Downloading Corporation <{}> info.",identifier);
			final CorporationV1 newcorp = new CorporationV1();
			// Corporation information.
			//			logger.info("-- [GlobalDataManager.requestCorporationV1]> ESI Compatible. Download corporation information.");
			final GetCorporationsCorporationIdOk publicData = this.esiAdapter.getCorporationsCorporationId(corpIdentifier
					, credential.getRefreshToken()
					, credential.getDataSource());
			newcorp.setCorporationId(corpIdentifier)
					.setPublicData(publicData);
			if (null != publicData.getAllianceId())
				newcorp.setAlliance(GlobalDataManager.getSingleton().requestAllianceV1(publicData.getAllianceId(), credential));

			return newcorp;
			//			} else {
			//				logger.info("-- [GlobalDataManager.requestCorporationV1]> Corporation <{}> found at cache.", identifier);
			//				return (CorporationV1) hit;
			//			}
		} finally {
			logger.info("<< [GlobalDataManager.requestCorporationV1]");
		}
	}

	//--- PILOT

	/**
	 * Construct a minimal implementation of a Pilot from the XML api. This will get deprecated soon but during
	 * some time It will be compatible and I will have a better view of what variants are being used.
	 * <p>
	 * Once the XML api is deprecated we implement the Pilot version 2. This will replace old data structures by its equivalents
	 * and also add new data and dependencies. This is the most up to date evolver version and comes from the Infinity requirements.
	 *
	 * @param credential current credential to be used for ESI authorization to access the server data.
	 * @return an instance of a PilotV2 class that has some of the required information to be shown on the ui at this
	 * 		point.
	 */
	public PilotV2 requestPilotV2( final Credential credential ) throws NeoComRegisteredException {
		logger.info(">> [GlobalDataManager.requestPilotV2]> Identifier: {}", credential.getAccountId());
		try {
			final PilotV2 newchar = new PilotV2();
			logger.info("-- [GlobalDataManager.requestPilotV2]> Processing data with Credential <{}>.", credential.getAccountName());

			// Public information.
			logger.info("-- [GlobalDataManager.requestPilotV2]> Download public data information.");
			final GetCharactersCharacterIdOk publicData = this.esiAdapter.getCharactersCharacterId(credential.getAccountId()
					, credential.getRefreshToken()
					, credential.getDataSource());
			// Public data can be null if there are problems accessing the server.
			if (null == publicData) throw new NeoComRegisteredException(NEOE.ESIDATA_NULL);
			newchar.setCharacterId(credential.getAccountId())
					.setPublicData(publicData);
			// Process the public data and get the referenced instances for the Corporation, race, etc.
			newchar
					.setRace(this.esiAdapter.searchSDERace(publicData.getRaceId()))
					.setBloodline(this.esiAdapter.searchSDEBloodline(publicData.getBloodlineId()))
					.setAncestry(this.esiAdapter.searchSDEAncestry(publicData.getAncestryId()));
			if (null != publicData.getCorporationId())
				newchar.setCorporation(GlobalDataManager.getSingleton().requestCorporationV1(publicData.getCorporationId(), credential));
			if (null != publicData.getAllianceId())
				newchar.setAlliance(GlobalDataManager.getSingleton().requestAllianceV1(publicData.getAllianceId(), credential));
			// Wallet status
			logger.info("-- [GlobalDataManager.requestPilotV2]> Download Wallet amount.");
			final Double walletAmount = ESINetworkManager.getCharactersCharacterIdWallet(credential.getAccountId()
					, credential.getRefreshToken()
					, credential.getDataSource());
			newchar.setAccountBalance(walletAmount);
			// Properties
			logger.info("-- [GlobalDataManager.requestPilotV2]> Download Pilot Properties.");
			try {
				final List<Property> properties =  GlobalDataManager.getSingleton().getNeocomDBHelper().getPropertyDao().queryForEq("ownerId"
						, credential.getAccountId());
				newchar.setProperties(properties);
			} catch (SQLException sqle) {
			}
			// Clone data
			logger.info("-- [GlobalDataManager.requestPilotV2]> Download clone information.");
			final GetCharactersCharacterIdClonesOk cloneInformation = this.esiAdapter.getCharactersCharacterIdClones(credential.getAccountId()
					, credential.getRefreshToken()
					, credential.getDataSource());
			if (null != cloneInformation) {
				newchar.setCloneInformation(cloneInformation);
				newchar.setHomeLocation(cloneInformation.getHomeLocation());
			}
			//
			//					// Register instance into the cache. Expiration time is about 3600 seconds.
			//					try {
			//						final Instant expirationTime = Instant.now().plus(TimeUnit.SECONDS.toMillis(3600));
			//						modelCache.store(EModelVariants.PILOTV2, newchar, expirationTime, identifier);
			//						// Store this same information on the database to record the TimeStamp.
			//						final String reference = GlobalDataManager.constructModelStoreReference(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE, credential.getAccountId());
			//						TimeStamp timestamp = getNeocomDBHelper().getTimeStampDao().queryForId(reference);
			//						if (null == timestamp) timestamp = new TimeStamp(reference, expirationTime);
			//						logger.info("-- [GlobalDataManager.reachPilotV2]> Updating character TimeStamp {}.", reference);
			//						timestamp.setTimeStamp(expirationTime)
			//								.setCredentialId(credential.getAccountId())
			//								.store();
			//					} catch (SQLException sqle) {
			//						sqle.printStackTrace();
			//					}
			// TODO End checkpoint --------------------------------------------
			//				}
			return newchar;
			//			} else {
			//				logger.info("-- [GlobalDataManager.requestPilotV2]> Pilot <{}> found at cache.", identifier);
			//				return (PilotV2) hit;
			//			}

		} finally {
			logger.info("<< [GlobalDataManager.requestPilotV2]");
		}
	}

	/**
	 * Reference to the NeoCom persistence database Dao provider. This filed should be injected on startup.
	 */
	private static INeoComDBHelper neocomDBHelper = null;

	public INeoComDBHelper getNeocomDBHelper() {
		if (null == neocomDBHelper)
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
		return neocomDBHelper;
	}

	public static INeoComDBHelper connectNeoComDBConnector( final INeoComDBHelper newhelper ) {
		if (null != newhelper) neocomDBHelper = newhelper;
		else
			throw new RuntimeException("[NeoComDatabase]> NeoCom database neocomDBHelper not defined. No access to platform library to get database results.");
		return neocomDBHelper;
	}

	// --- N E O C O M   P R I V A T E   D A T A B A S E   S E C T I O N

	/**
	 * Reads all the list of credentials stored at the Database and returns them. Activation depends on the interpretation used by the
	 * application.
	 * The method only returns Credentials authorized for the current eve online server. This way I can have different sets of
	 * credentials with not overriding them and still having the guarantee that they are unique.
	 */
	public static List<Credential> accessAllCredentials( final String sourceServer ) {
		List<Credential> credentialList = new ArrayList<>();
		try {
			return GlobalDataManager.getSingleton().getNeocomDBHelper().getCredentialDao().queryForAll();
//					       .queryForEq("dataSource", sourceServer);
		} catch (java.sql.SQLException sqle) {
			sqle.printStackTrace();
			logger.warn("W [GlobalDataManagerDataAccess.accessAllCredentials]> Exception reading all Credentials. " + sqle.getMessage());
		}
		return credentialList;
	}

	public static List<NeoComAsset> accessAllAssets4Credential( final Credential credential ) throws SQLException {
		final List<NeoComAsset> assetList = GlobalDataManager.getSingleton().getNeocomDBHelper().getAssetDao()
				                                    .queryForEq("ownerId", credential.getAccountId());
		if (GlobalDataManager.getResourceBoolean("R.runtime.mockdata")) {
			// Write down the credential list ot be used as mock data.
			final File outFile = new File(GlobalDataManager.getResourceString("R.runtime.mockdata.location")
					                              + "accessAllAssets4Credential-" + credential.getAccountId() + ".data");
			try {
				final BufferedOutputStream buffer = new BufferedOutputStream(new FileOutputStream(outFile));
				final ObjectOutput output = new ObjectOutputStream(buffer);
				try {
					output.writeObject(assetList);
					logger.info(
							"-- [GlobalDataManagerDataAccess.accessAllCredentials]> Wrote asset list: {} entries."
							, assetList.size());
				} finally {
					output.flush();
					output.close();
					buffer.close();
				}
			} catch (final FileNotFoundException fnfe) {
				logger.warn("W> [GlobalDataManagerDataAccess.accessAllCredentials]> FileNotFoundException."); //$NON-NLS-1$
			} catch (final IOException ex) {
				logger.warn("W> [GlobalDataManagerDataAccess.accessAllCredentials]> IOException."); //$NON-NLS-1$
			}
		}
		return assetList;
	}

	public static List<Job> accessIndustryJobs4Credential( final Credential credential ) throws SQLException {
		return GlobalDataManager.getSingleton().getNeocomDBHelper().getJobDao()
				       .queryForEq("ownerId", credential.getAccountId());
	}

	public static List<MarketOrder> accessMarketOrders4Credential( final Credential credential ) throws SQLException {
		return GlobalDataManager.getSingleton().getNeocomDBHelper().getMarketOrderDao()
				       .queryForEq("ownerId", credential.getAccountId());
	}

	/**
	 * Get the list of Mining Extractions that are registered on the database. This can be a lot of records that need sorting and also
	 * grouping previously to rendering. This method can do the sorting but the grouping it not one of its features.
	 * The mining operations for a single day aggregate all the ore for a single type, but have different records for different systems
	 * and
	 * for different ores. So for a single day we can have around 6-8 records. The mining ledger information at the neocom database has
	 * to
	 * expiration time so the number of days is still not predetermined.
	 */
	public static List<MiningExtraction> accessMiningExtractions4Pilot( final Credential credential ) throws SQLException {
		final Dao<MiningExtraction, String> dao = GlobalDataManager.getSingleton().getNeocomDBHelper().getMiningExtractionDao();
		final QueryBuilder<MiningExtraction, String> builder = dao.queryBuilder();
		builder.where().eq("ownerId", credential.getAccountId());
		builder.orderBy("id", false);
		final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
		return dao.query(preparedQuery);
	}

	/**
	 * This other method does the same Mining Extractions processing but only for the records for the current date. The difference is
	 * that today records are aggregated by hour instead of by day. So we will have a record for one ore/system since the hour we did the
	 * extractions until the 23 hours. The first extraction will add to the hour until the next hour starts. Then the accounting for this
	 * new hour will show the new ore totals and so on hour after hour.
	 */
	public static List<MiningExtraction> accessTodayMiningExtractions4Pilot( final Credential credential ) throws SQLException {
		final Dao<MiningExtraction, String> dao = GlobalDataManager.getSingleton().getNeocomDBHelper().getMiningExtractionDao();
		final QueryBuilder<MiningExtraction, String> builder = dao.queryBuilder();
		builder.where().eq("ownerId", credential.getAccountId());
		builder.orderBy("extractionDateName", true)
				.orderBy("extractionHour", true)
				.orderBy("solarSystemId", true)
				.orderBy("typeId", true);
		final PreparedQuery<MiningExtraction> preparedQuery = builder.prepare();
		final List<MiningExtraction> dataList = dao.query(preparedQuery);
		List<MiningExtraction> results = new ArrayList<>();
		final String filterDate = DateTime.now().toString("YYYY/MM/dd");
		// Filter out all records not belonging to today.
		for (MiningExtraction extraction : dataList) {
			final String date = extraction.getExtractionDate().split(":")[0];
			if (date.equalsIgnoreCase(filterDate)) results.add(extraction);
		}
		return results;
	}
}
