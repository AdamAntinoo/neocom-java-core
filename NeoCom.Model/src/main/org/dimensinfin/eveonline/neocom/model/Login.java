//	PROJECT:      NeoCom.model (NEOC.M)
//	AUTHORS:      Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:    (c) 2013-2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:	Java 1.8 Library.
//	DESCRIPTION:	Isolated model structures to access and manage Eve Online character data and their
//								available databases.
//								This version includes the access to the latest 6.x version of eveapi libraries to
//								download ad parse the CCP XML API data.
//								Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import com.beimin.eveapi.exception.ApiException;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This class stores the list of available Logins that are declared inside the back end NeoCom database. A
 * login is a name given to a set of keys. The keys are then processed to get access to the declared
 * Characters and the distinct list of those Characters is the content of a Login. So there are three key
 * elements. The Login unique name, the list of keys and the resulting set of Character from all those keys.
 * 
 * @author Adam Antinoo
 */
public class Login {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static Logger										logger			= Logger.getLogger("Login");

	// - F I E L D - S E C T I O N ............................................................................
	private String													_name				= "-Default-";
	private final Vector										_keys				= new Vector();
	private final TreeSet<NeoComCharacter>	_characters	= new TreeSet();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Login(final String name) {
		_name = name;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Adds a new key to the login. When the key is added it fires the Character identification phase to collect
	 * Character data into the character list of this login.
	 */
	public Login addKey(final ApiKey newkey) {
		_keys.add(newkey);
		// Process the key to get the next level of data.
		try {
			NeoComApiKey key = NeoComApiKey.build(newkey.getKeynumber(), newkey.getValidationcode());
			// Scan for the characters declared into this key.
			for (NeoComCharacter pilot : key.getApiCharacters()) {
				// REFACTOR There is no need to update the Character information since it is already present and Assets and other data will be collected autoamtically.
				//					// Post the request to update the Character.
				//					AppConnector.getCacheConnector().addCharacterUpdateRequest(pilot.getCharacterID());
				_characters.add(pilot);
				Login.logger.info("-- [Login.addKey]> Adding " + pilot.getName() + " to the _characters");
			}
		} catch (ApiException apiex) {
			apiex.printStackTrace();
		}

		return this;
	}

	public String getName() {
		return _name;
	}

	/**
	 * Search for a character with this ID on the list of characters linked to this Login.
	 * 
	 * @param id
	 * @return null if not found.
	 */
	public NeoComCharacter searchCharacter(final long id) {
		for (NeoComCharacter neoch : _characters) {
			if (neoch.getCharacterID() == id) return neoch;
		}
		return null;
	}
}

// - UNUSED CODE ............................................................................................
