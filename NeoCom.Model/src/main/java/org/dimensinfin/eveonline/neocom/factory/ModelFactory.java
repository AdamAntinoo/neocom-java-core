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

import com.beimin.eveapi.EveApi;
import com.beimin.eveapi.connectors.ApiConnector;
import com.beimin.eveapi.connectors.CachingConnector;
import com.beimin.eveapi.connectors.LoggingConnector;
import com.beimin.eveapi.exception.ApiException;
import com.beimin.eveapi.model.account.Character;
import com.beimin.eveapi.model.shared.EveAccountBalance;
import com.beimin.eveapi.parser.ApiAuthorization;
import com.beimin.eveapi.parser.account.ApiKeyInfoParser;
import com.beimin.eveapi.parser.eve.CharacterInfoParser;
import com.beimin.eveapi.parser.pilot.PilotAccountBalanceParser;
import com.beimin.eveapi.response.account.ApiKeyInfoResponse;
import com.beimin.eveapi.response.eve.CharacterInfoResponse;
import com.beimin.eveapi.response.shared.AccountBalanceResponse;

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.eveonline.neocom.core.NeoComConnector;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdClonesOk;
import org.dimensinfin.eveonline.neocom.model.Credential;
import org.dimensinfin.eveonline.neocom.model.PilotV1;
import org.dimensinfin.eveonline.neocom.network.NetworkManager;
import org.dimensinfin.eveonline.neocom.storage.DataManagementModelStore;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;

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
	public enum EModelVariants {
		PILOTV1
	}

	// - CLASS IMPLEMENTATION ...................................................................................
	public static class ModelTimedCache {

		// - F I E L D - S E C T I O N ............................................................................
		private Hashtable<String, ICollaboration> _instanceCacheStore = new Hashtable();
		private Hashtable<String, Instant> _timeCacheStore = new Hashtable();

		// - M E T H O D - S E C T I O N ..........................................................................
		public int size(){
			return _instanceCacheStore.size();
		}
		public ICollaboration access (final EModelVariants variant, long longIdentifier) {
			if ( variant == EModelVariants.PILOTV1 ) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				final ICollaboration hit = _instanceCacheStore.get(locator);
				if ( null != hit ) {
					final Instant expitationTime = _timeCacheStore.get(locator);
					if ( expitationTime.isBefore(Instant.now()) ) return null;
					else return hit;
				}
			}
			return null;
		}
		public boolean store (final EModelVariants variant, final ICollaboration instance, final Instant expirationTime, final long longIdentifier) {
			if ( variant == EModelVariants.PILOTV1 ) {
				final String locator = EModelVariants.PILOTV1.name() + "/" + Long.valueOf(longIdentifier).toString();
				_instanceCacheStore.put(locator, instance);
				_timeCacheStore.put(locator, expirationTime);
				return true;
			}
			return false;
		}
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger logger = LoggerFactory.getLogger(ModelFactory.class);
	private static final ModelFactory singleton = new ModelFactory();
	private static ModelTimedCache modelCache = new ModelTimedCache();

	// - S T A T I C   R E P L I C A T E D   M E T H O D S
	public static PilotV1 getPilotV1 (final long identifier) {
		return singleton.getPilotV1Impl(identifier);
	}
	// - F I E L D - S E C T I O N ............................................................................

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ModelFactory () {
		super();
	}
	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Construct a minimal implementation of a Pilot from the XML api. This will get deprecated soon but during
	 * some time It will be compatible and I will have a better view of what variants are being used.
	 *
	 * @param identifier character identifier from the valid Credential.
	 * @return an instance of a PilotV1 class that has some of the required information to be shown on the ui at this
	 * point.
	 */
	private PilotV1 getPilotV1Impl (final long identifier) {
		// Check if this request is already available on the cache.
		final ICollaboration hit = modelCache.access(EModelVariants.PILOTV1, identifier);
		if ( null == hit ) {
			final PilotV1 newchar = new PilotV1();
			// Get the credential from the Store and check if this identifier has access to the XML api.
			final Credential credential = DataManagementModelStore.getCredential4Id(identifier);
			if ( null != credential ) {
				// Check the Credential type.
				if ( credential.isXMLCompatible() ) {
					try {
						// Copy the authorization and add to it the characterID
						final ApiAuthorization authcopy = new ApiAuthorization(credential.getKeyCode(), identifier,
								credential.getValidationCode());
						// TODO It seems this is not required on this version of the object.
						//		newchar.setAuthorization(authcopy);
						// Copy the id to a non volatile field.
						newchar.setCharacterId(identifier);
						newchar.setName(credential.getAccountName());
						newchar.setDelegatedCharacter(accessApiKeyCoreChar(identifier, authcopy));

						// Balance information
						final PilotAccountBalanceParser balanceparser = new PilotAccountBalanceParser();
						final AccountBalanceResponse balanceresponse = balanceparser.getResponse(authcopy);
						if ( null != balanceresponse ) {
							final Set<EveAccountBalance> balance = balanceresponse.getAll();
							if ( balance.size() > 0 ) {
								newchar.setAccountBalance(balance.iterator().next().getBalance());
							}
						}

						// Character information
						final CharacterInfoParser infoparser = new CharacterInfoParser();
						final CharacterInfoResponse inforesponse = infoparser.getResponse(authcopy);
						if ( null != inforesponse ) {
							newchar.setCharacterInfo(inforesponse);
						}

						// Clone data
						final GetCharactersCharacterIdClonesOk cloneInformation = NetworkManager.getCharactersCharacterIdClones(Long.valueOf(identifier).intValue(), credential.getRefreshToken(), "tranquility");
						if ( null != cloneInformation ) newchar.setHomeLocation(cloneInformation.getHomeLocation());

						// Store the result on the cache with the timing indicator to where this entry is valid.
						modelCache.store(EModelVariants.PILOTV1, newchar, new Instant(inforesponse.getCachedUntil()), identifier);
					} catch (ApiException apie) {
						apie.printStackTrace();
					}
				}
			}
			return newchar;
		} else return (PilotV1) hit;
	}
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
		buffer.append("Models cached: ").append(modelCache.size());
		buffer.append("]");
//		buffer.append("->").append(super.toString());
		return buffer.toString();
	}
}
// - UNUSED CODE ............................................................................................
//[01]
