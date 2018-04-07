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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.joda.time.Instant;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.database.entity.TimeStamp;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetAlliancesAllianceIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanets200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdPlanetsPlanetIdOkPins;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.model.AllianceV1;
import org.dimensinfin.eveonline.neocom.model.CorporationV1;
import org.dimensinfin.eveonline.neocom.model.EveLocation;
import org.dimensinfin.eveonline.neocom.model.ItemCategory;
import org.dimensinfin.eveonline.neocom.model.ItemGroup;
import org.dimensinfin.eveonline.neocom.model.PilotV2;
import org.dimensinfin.eveonline.neocom.planetary.Schematics;

/**
 * This is the Infinity special extension to get .
 * <p>
 * The initial release will start transferring the ModelFactory functionality.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class InfinityGlobalDataManager extends GlobalDataManager{
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("InfinityGlobalDataManager");

	// --- M O D E L - S T O R E   I N T E R F A C E
	//--- ALLIANCE
	public static AllianceV1 reachAllianceV1( final int identifier, final SessionContext context ) {
		logger.info(">> [InfinityGlobalDataManager.reachAllianceV1]> Identifier: {}", identifier);
		try {
//			// Check if this request is already available on the cache.
//			final ICollaboration hit = modelCache.access(EModelVariants.ALLIANCEV1, identifier);
//			if (null == hit) {
//				logger.info("-- [InfinityGlobalDataManager.reachAllianceV1]> Instance not found at cache. Downloading Alliance <{}> info.",
//						identifier);
				final AllianceV1 newalliance = new AllianceV1();
				// Get the credential from the Store.
				final Credential credential = context.getCredential();

				// Corporation information.
				logger.info("-- [InfinityGlobalDataManager.reachAllianceV1]> ESI Compatible. Download corporation information.");
				final GetAlliancesAllianceIdOk publicData = ESINetworkManager.getAlliancesAllianceId(Long.valueOf(identifier).intValue()
						, credential.getRefreshToken()
						, SERVER_DATASOURCE);
				newalliance.setPublicData(publicData);
				return newalliance;
//			} else {
//				logger.info("-- [InfinityGlobalDataManager.reachAllianceV1]> Alliance <{}> found at cache.", identifier);
//				return (AllianceV1) hit;
//			}
		} finally {
			logger.info("<< [InfinityGlobalDataManager.reachAllianceV1]");
		}
	}

	//--- CORPORATION
	public static CorporationV1 reachCorporationV1( final int identifier, final SessionContext context ) {
		logger.info(">> [InfinityGlobalDataManager.reachCorporationV1]> Identifier: {}", identifier);
		try {
			// Check if this request is already available on the cache.
//			final ICollaboration hit = modelCache.access(EModelVariants.CORPORATIONV1, identifier);
//			if (null == hit) {
//				logger.info("-- [InfinityGlobalDataManager.reachCorporationV1]> Instance not found at cache. Downloading Corporation <{}> info.",identifier);
				final CorporationV1 newcorp = new CorporationV1();
				// Get the credential from the Session.
				final Credential credential = context.getCredential();

				// Corporation information.
				logger.info("-- [InfinityGlobalDataManager.reachCorporationV1]> ESI Compatible. Download corporation information.");
				final GetCorporationsCorporationIdOk publicData = ESINetworkManager.getCorporationsCorporationId(Long.valueOf(identifier).intValue()
						, credential.getRefreshToken()
						, SERVER_DATASOURCE);
				newcorp.setPublicData(publicData);
				// Process the public data and get the referenced instances for the Corporation, race, etc.
				newcorp.setAlliance(InfinityGlobalDataManager.reachAllianceV1(publicData.getAllianceId(), context));

				return newcorp;
//			} else {
//				logger.info("-- [InfinityGlobalDataManager.useCorporationV1]> Corporation <{}> found at cache.", identifier);
//				return (CorporationV1) hit;
//			}
		} finally {
			logger.info("<< [InfinityGlobalDataManager.reachCorporationV1]");
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
	 * @param identifier character identifier from the valid Credential.
	 * @return an instance of a PilotV1 class that has some of the required information to be shown on the ui at this
	 * point.
	 */
	public static PilotV2 reachPilotV2( final int identifier, final SessionContext context ) {
		logger.info(">> [InfinityGlobalDataManager.reachPilotV2]> Identifier: {}", identifier);
		try {
//			// Check if this request is already available on the cache.
//			final ICollaboration hit = modelCache.access(EModelVariants.PILOTV2, identifier);
//			if (null == hit) {
//				logger.info("-- [GlobalDataManager.getPilotV2]> Instance not found at cache. Downloading pilot <{}> info.", identifier);
				final PilotV2 newchar = new PilotV2();
				// Get the credential from the Store and check if this identifier has access to the XML api.
				final Credential credential = context.getCredential();
				if (null != credential) {
					logger.info("-- [InfinityGlobalDataManager.reachPilotV2]> Processing data with Credential <{}>.", credential.getAccountName());

					// Public information.
					logger.info("-- [InfinityGlobalDataManager.reachPilotV2]> ESI Compatible. Download public data information.");
					final GetCharactersCharacterIdOk publicData = ESINetworkManager.getCharactersCharacterId(Long.valueOf(identifier).intValue()
							, credential.getRefreshToken()
							, SERVER_DATASOURCE);
					newchar.setPublicData(publicData);
					// Process the public data and get the referenced instances for the Corporation, race, etc.
					newchar.setCorporation ( InfinityGlobalDataManager.reachCorporationV1(publicData.getCorporationId(),context))
							.setAlliance (InfinityGlobalDataManager.reachAllianceV1(publicData.getAllianceId(),context))
							.setRace (GlobalDataManager.searchSDERace(publicData.getRaceId(),context))
							.setBloodline (GlobalDataManager.searchSDEBloodline(publicData.getBloodlineId(),context))
							.setAncestry (GlobalDataManager.searchSDEAncestry(publicData.getAncestryId(),context));

					// Clone data
					logger.info("-- [InfinityGlobalDataManager.reachPilotV2]> ESI Compatible. Download clone information.");
					final GetCharactersCharacterIdClonesOk cloneInformation = ESINetworkManager.getCharactersCharacterIdClones(Long.valueOf(identifier).intValue(), credential.getRefreshToken(), "tranquility");
					if (null != cloneInformation) {
						newchar.setCloneInformation(cloneInformation);
						newchar.setHomeLocation(cloneInformation.getHomeLocation());
					}

					// Roles
					// TODO To be implemented
					// Register instance into the cache. Expiration time is about 3600 seconds.
					try {
						final Instant expirationTime = Instant.now().plus(TimeUnit.SECONDS.toMillis(3600));
						modelCache.store(EModelVariants.PILOTV2, newchar, expirationTime, identifier);
						// Store this same information on the database to record the TimeStamp.
						final String reference = GlobalDataManager.constructModelStoreReference(GlobalDataManager.EDataUpdateJobs.CHARACTER_CORE, credential.getAccountId());
						TimeStamp timestamp = getNeocomDBHelper().getTimeStampDao().queryForId(reference);
						if (null == timestamp) timestamp = new TimeStamp(reference, expirationTime);
						logger.info("-- [InfinityGlobalDataManager.reachPilotV2]> Updating character TimeStamp {}.", reference);
						timestamp.setTimeStamp(expirationTime)
								.setCredentialId(credential.getAccountId())
								.store();
					} catch (SQLException sqle) {
						sqle.printStackTrace();
					}
				}
				return newchar;
//			} else {
//				logger.info("-- [InfinityGlobalDataManager.getPilotV2]> Pilot <{}> found at cache.", identifier);
//				return (PilotV2) hit;
//			}
		} finally {
			logger.info("<< [InfinityGlobalDataManager.reachPilotV2]");
		}
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................

	// - M E T H O D - S E C T I O N ..........................................................................
}
// - UNUSED CODE ............................................................................................
//[01]
