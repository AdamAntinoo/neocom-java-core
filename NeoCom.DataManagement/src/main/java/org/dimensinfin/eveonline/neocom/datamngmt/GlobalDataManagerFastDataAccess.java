//  PROJECT:     NeoCom.Android (NEOC.A)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Android API22.
//  DESCRIPTION: Android Application related to the Eve Online game. The purpose is to download and organize
//               the game data to help capsuleers organize and prioritize activities. The strong points are
//               help at the Industry level tracking and calculating costs and benefits. Also the market
//               information update service will help to identify best prices and locations.
//               Planetary Interaction and Ship fittings are point under development.
//               ESI authorization is a new addition that will give continuity and allow download game data
//               from the new CCP data services.
//               This is the Android application version but shares libraries and code with other application
//               designed for Spring Boot Angular 4 platform.
//               The model management is shown using a generic Model View Controller that allows make the
//               rendering of the model data similar on all the platforms used.
package org.dimensinfin.eveonline.neocom.datamngmt;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.database.entity.Credential;
import org.dimensinfin.eveonline.neocom.exception.NeoComRegisteredException;
import org.dimensinfin.eveonline.neocom.model.PilotV2;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Implements a model cache for fast data access. Updates will be done on background on the Future mechanics and probably there will be
 * events to notify the Parts of the Model updates to refresh the Renders.
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class GlobalDataManagerFastDataAccess extends GlobalDataManagerExceptions {
	public enum EModelDataTypes {
		PILOTV2
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger("GlobalDataManagerFastDataAccess");
	private static ModelTimedCache modelcache = new ModelTimedCache();

	private static final ExecutorService modelUpdaterExecutor = Executors.newSingleThreadExecutor();

	// --- F A S T   O B J E C T   M O D E L - ST O R E   S E C T I O N
	public static PilotV2 fastRequestPilotV2( final Credential credential ) throws NeoComRegisteredException {
		return (PilotV2) modelcache.access(EModelDataTypes.PILOTV2, credential);
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ModelTimedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, ICollaboration> _instanceCacheStore = new Hashtable();
		private Hashtable<String, Instant> _timeCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size() {
			return _instanceCacheStore.size();
		}

		public ICollaboration access( final EModelDataTypes variant, final Credential credential ) {
			logger.info(">> [GlobalDataManagerFastDataAccess.access]");
			if (variant == EModelDataTypes.PILOTV2) {
				final String locator = EModelDataTypes.PILOTV2.name() + "/" + Integer.valueOf(credential.getAccountId()).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				if (null != hit) {
					// Check if the object has expired. If so fire a background updating event.
					if (_timeCacheStore.get(locator).isBefore(Instant.now())) {
						GlobalDataManager.submitJob2ui(() -> {
							final PilotV2 instance;
							try {
								instance = GlobalDataManager.requestPilotV2(credential);
								store(EModelDataTypes.PILOTV2
										, instance
										, Instant.now().plus(ESICacheTimes.get(ECacheTimes.CHARACTER_PUBLIC))
										, credential.getAccountId());
							} catch (NeoComRegisteredException neoe) {
								neoe.printStackTrace();
							}
						});
						return hit;
					} else
						return hit;
				} else {
					// The object is not cached. Get it from the network and wait until the Future completes.
					final Future<PilotV2> fut = modelUpdaterExecutor.submit(() -> {
						try {
							final PilotV2 instance = GlobalDataManager.requestPilotV2(credential);
							store(EModelDataTypes.PILOTV2
									, instance
									, Instant.now().plus(ESICacheTimes.get(ECacheTimes.CHARACTER_PUBLIC))
									, credential.getAccountId());
							return instance;
						} catch (NeoComRegisteredException neoe) {
							return null;
						}
					});
					try {
						return fut.get();
					} catch (InterruptedException ie) {
						return null;
					} catch (ExecutionException ee) {
						ee.printStackTrace();
						return null;
					}
				}
			}
			return null;
		}

		public ICollaboration delete( final EModelDataTypes variant, long longIdentifier ) {
			if (variant == EModelDataTypes.PILOTV2) {
				final String locator = EModelDataTypes.PILOTV2.name() + "/" + Long.valueOf(longIdentifier).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				_instanceCacheStore.put(locator, null);
				return hit;
			}
			return null;
		}

		public boolean store( final EModelDataTypes variant, final ICollaboration instance, final Instant expirationTime, final long longIdentifier ) {
			logger.info(">> [GlobalDataManagerFastDataAccess.store]");
			// Store command for PILOTV1 instances.
			if (variant == EModelDataTypes.PILOTV2) {
				final String locator = EModelDataTypes.PILOTV2.name() + "/" + Long.valueOf(longIdentifier).toString();
				_instanceCacheStore.put(locator, instance);
				_timeCacheStore.put(locator, expirationTime);
				logger.info("<< [GlobalDataManagerFastDataAccess.store]");
				return true;
			}
			//			// Store command for APIKEY instances.
			//			if (variant == EModelVariants.APIKEY) {
			//				final String locator = EModelVariants.APIKEY.name() + "/" + Long.valueOf(longIdentifier).toString();
			//				_instanceCacheStore.put(locator, instance);
			//				_timeCacheStore.put(locator, expirationTime);
			//				return true;
			//			}
			logger.info("<<>>>> [GlobalDataManagerFastDataAccess.store]");
			return false;
		}
	}
	// ........................................................................................................
}
// - UNUSED CODE ............................................................................................
//[01]
