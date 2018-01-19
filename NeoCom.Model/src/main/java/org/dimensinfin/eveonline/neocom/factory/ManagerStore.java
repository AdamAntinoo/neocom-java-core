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
package org.dimensinfin.eveonline.neocom.factory;

import org.dimensinfin.eveonline.neocom.manager.AbstractManager;
import org.dimensinfin.eveonline.neocom.manager.AssetsManager;
import org.dimensinfin.eveonline.neocom.manager.PlanetaryManager;
import org.dimensinfin.eveonline.neocom.model.Credential;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;

/**
 * This class will keep control for the different managers. It will cache their contents and also clean when
 * models or contents change so each time somebody needs data it can request an instance and get a cached
 * one if available and required.
 *
 * @author Adam Antinoo
 */

// - CLASS IMPLEMENTATION ...................................................................................
public class ManagerStore {
	private enum EManagerCodes {
		PLANETARY_MANAGER, ASSETS_MANAGER
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ManagerOptimizedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, AbstractManager> _managerCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size () {
			return _managerCacheStore.size();
		}

		public AbstractManager access (final EManagerCodes variant, long longIdentifier) {
			final String locator = variant.name() + "/" + Long.valueOf(longIdentifier).toString();
			final AbstractManager hit = _managerCacheStore.get(locator);
			return hit;
		}

		public boolean store (final EManagerCodes variant, final AbstractManager instance, final long longIdentifier) {
			final String locator = variant.name() + "/" + Long.valueOf(longIdentifier).toString();
			_managerCacheStore.put(locator, instance);
			return true;
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ManagerStore.class);

	private static final ManagerStore singleton = new ManagerStore();
	private static ManagerOptimizedCache managerCache = new ManagerOptimizedCache();

	// - S T A T I C   R E P L I C A T E D   M E T H O D S
	public static AssetsManager getAssetsManager (final long identifier) {
		return singleton.getAssetsManagerImpl(identifier, false);
	}

	public static AssetsManager getAssetsManager (final long identifier, final boolean forceNew) {
		return singleton.getAssetsManagerImpl(identifier, forceNew);
	}

	public static PlanetaryManager getPlanetaryManager (final Credential credential) {
		return getPlanetaryManager(credential, false);
	}

	public static PlanetaryManager getPlanetaryManager (final Credential credential, final boolean forceNew) {
		// Check if this request is already available on the cache.
		final PlanetaryManager hit = (PlanetaryManager) managerCache.access(EManagerCodes.PLANETARY_MANAGER, credential.getAccountId());
		if ( (null == hit) || (forceNew) ) {
			// TODO This line depends on the architecture of the data loading when it should not.
			final PlanetaryManager manager = new PlanetaryManager(credential);
			managerCache.store(EManagerCodes.PLANETARY_MANAGER, manager, credential.getAccountId());
			return manager;
		} else return hit;
	}

	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ManagerStore () {
		super();
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	private AssetsManager getAssetsManagerImpl (final long identifier, final boolean forceNew) {
		// Check if this request is already available on the cache.
		final AssetsManager hit = (AssetsManager) managerCache.access(EManagerCodes.ASSETS_MANAGER, identifier);
		if ( (null == hit) || (forceNew) ) {
			final AssetsManager manager = new AssetsManager(DataManagementModelStore.getCredential4Id(identifier));
			managerCache.store(EManagerCodes.ASSETS_MANAGER, manager, identifier);
			return manager;
		} else return hit;
	}

//	private PlanetaryManager getPlanetaryManagerImpl (final Credential credential, final boolean forceNew) {
//	}

	@Override
	public String toString () {
		StringBuffer buffer = new StringBuffer("ManagerStore [");
		buffer.append("Managers controlled: ").append(managerCache.size());
		buffer.append("]");
		//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
