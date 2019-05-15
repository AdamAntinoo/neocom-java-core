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
package org.dimensinfin.eveonline.neocom.app.factory;

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.connectors.CachingConnector;
import com.beimin.eveapi.connectors.LoggingConnector;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;

import org.dimensinfin.eveonline.neocom.core.NeoComConnector;
import org.dimensinfin.eveonline.neocom.datamngmt.manager.GlobalDataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * The responsibility of this class is to serve as a Model constructor and Model cache. Upon request of a Model
 * variant the class will check if that variant is already available. If so it will return a pointer to the cached
 * data.
 * If the Model requested is not at the store, then and depending on the model variant requested the class
 * creates a new instance and populates it contents with data from the back end network adapters. During some time
 * this will allow to instantiate different Model variants coming from different API upon request until the
 * api get deprecated.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ModelFactory {
//	private enum EModelVariants {
//		PILOTV1, APIKEY
//	}

	// - CLASS IMPLEMENTATION ...................................................................................
//	public static class ModelTimedCache {
//
//		// - F I E L D - S E C T I O N ............................................................................
//		private Hashtable<String, ICollaboration> _instanceCacheStore = new Hashtable();
//		private Hashtable<String, Instant> _timeCacheStore = new Hashtable();
//
//		// - M E T H O D - S E C T I O N ..........................................................................
//		public int size () {
//			return _instanceCacheStore.size();
//		}
//
//		public ICollaboration access (final EModelVariants variant, long longIdentifier) {
//			if ( variant == EModelVariants.PILOTV1 ) {
//				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
//				final ICollaboration hit = _instanceCacheStore.get(locator);
//				if ( null != hit ) {
//					final Instant expitationTime = _timeCacheStore.get(locator);
//					if ( expitationTime.isBefore(Instant.now()) ) return null;
//					else return hit;
//				}
//			}
//			return null;
//		}
//
//		public ICollaboration delete (final EModelVariants variant, long longIdentifier) {
//			if ( variant == EModelVariants.PILOTV1 ) {
//				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
//				final ICollaboration hit = _instanceCacheStore.get(locator);
//				_instanceCacheStore.put(locator, null);
//				return hit;
//			}
//			return null;
//		}
//
//		public boolean store (final EModelVariants variant, final ICollaboration instance, final Instant expirationTime, final long longIdentifier) {
//			// Store command for PILOTV1 instances.
//			if ( variant == EModelVariants.PILOTV1 ) {
//				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
//				_instanceCacheStore.put(locator, instance);
//				_timeCacheStore.put(locator, expirationTime);
//				return true;
//			}
//			// Store command for APIKEY instances.
//			if ( variant == EModelVariants.APIKEY ) {
//				final String locator = EModelVariants.APIKEY.name() + "/" + Long.valueOf(longIdentifier).toString();
//				_instanceCacheStore.put(locator, instance);
//				_timeCacheStore.put(locator, expirationTime);
//				return true;
//			}
//			return false;
//		}
//	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ModelFactory.class);
	private static final ModelFactory singleton = new ModelFactory();
//	private static ModelTimedCache modelCache = new ModelTimedCache();

	/** Initialize the beimin Eve Api connector to remove SSL certification. From this point on we can use the beimin
	 * XML api to access CCP data. */
	static {
		EveApi.setConnector(new NeoComConnector(new CachingConnector(new LoggingConnector())));
		// Remove the secure XML access and configure the ApiConnector.
		ApiConnector.setSecureXmlProcessing(false);
	}
	public static String constructReference (final GlobalDataManager.EDataUpdateJobs type, final long identifier) {
		return new StringBuffer(type.name()).append("/").append(identifier).toString();
	}

	// - S T A T I C   R E P L I C A T E D   M E T H O D S
//	public static PilotV1 getPilotV2 (final int identifier) {
//		return singleton.getPilotV1Impl(identifier);
//	}



	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ModelFactory () {
		super();
	}
	// - M E T H O D - S E C T I O N ..........................................................................


//	private NeoComApiKey getApiKeyImpl (final int keynumber, final String validationcode) {
//		// Check if this request is already available on the cache.
//		final ICollaboration hit = modelCache.access(EModelVariants.APIKEY, keynumber);
//		if ( null == hit ) {
//			try {
//				// Get the ApiKey Information block.
//				ApiAuthorization authorization = new ApiAuthorization(keynumber, validationcode);
//				ApiKeyInfoParser infoparser = new ApiKeyInfoParser();
//				ApiKeyInfoResponse inforesponse = infoparser.getResponse(authorization);
//				NeoComApiKey apiKey = null;
//				if ( null != inforesponse ) {
//					apiKey = new NeoComApiKey();
//					apiKey.setAuthorization(authorization);
//					apiKey.setDelegate(inforesponse);
//					apiKey.setKey(keynumber);
//					apiKey.setValidationCode(validationcode);
//					apiKey.setCachedUntil(inforesponse.getCachedUntil());
//					// Store the result on the cache with the timing indicator to where this entry is valid.
//					modelCache.store(EModelVariants.APIKEY, apiKey, new Instant(inforesponse.getCachedUntil()), keynumber);
//					return apiKey;
//				} else return null;
//			} catch (ApiException apie) {
//				apie.printStackTrace();
//			}
//		} else return (NeoComApiKey) hit;
//		return null;
//	}

	// --- N O T   E X P O R T E D   M E T H O D S
	private Character accessApiKeyCoreChar (final long identifier, final ApiAuthorization auth) {
		try {
			EveApi.setConnector(new NeoComConnector(new CachingConnector(new LoggingConnector())));
			// Remove the secure XML access and configure the ApiConnector.
			ApiConnector.setSecureXmlProcessing(false);
			ApiAuthorization authorization = new ApiAuthorization(auth.getKeyID(), auth.getVCode());
			ApiKeyInfoParser infoparser = new ApiKeyInfoParser();
			ApiKeyInfoResponse inforesponse = infoparser.getResponse(authorization);
			if ( null != inforesponse ) {
				Collection<Character> coreList = inforesponse.getApiKeyInfo().getEveCharacters();
				for (Character character : coreList) {
					if ( character.getCharacterID() == identifier ) return character;
				}
			}
		} catch (ApiException apie) {
			apie.printStackTrace();
		}
		return null;
	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ModelFactory [");
//		buffer.append("Models cached: ").append(modelCache.size());
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
