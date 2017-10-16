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

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Logger;

import org.dimensinfin.android.model.AbstractViewableNode;
import org.dimensinfin.core.model.AbstractComplexNode;

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
public class Login extends AbstractViewableNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long								serialVersionUID	= -1654191267396975701L;
	private static Logger										logger						= Logger.getLogger("Login");

	// - F I E L D - S E C T I O N ............................................................................
	private String													_name							= "-Default-";
	private final Vector<ApiKey>						_keys							= new Vector<ApiKey>();
	private final TreeSet<NeoComCharacter>	_characters				= new TreeSet<NeoComCharacter>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Login() {
		super();
		jsonClass = "Login";
	}

	public Login(final String name) {
		_name = name;
		jsonClass = "Login";
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
			if (newkey.isActive()) {
				NeoComApiKey key = NeoComApiKey.build(newkey.getKeynumber(), newkey.getValidationcode());
				// Add the Characters only if the Key is active.
				// Scan for the characters declared into this key.
				for (NeoComCharacter pilot : key.getApiCharacters()) {
					// REFACTOR There is no need to update the Character information since it is already present and Assets and other data will be collected autoamtically.
					//					// Post the request to update the Character.
					//					AppConnector.getCacheConnector().addCharacterUpdateRequest(pilot.getCharacterID());
					_characters.add(pilot);
					// Update the pilot parentship.
					pilot.connectLogin(this);
					Login.logger.info("-- [Login.addKey]> Adding " + pilot.getName() + " to the _characters");
				}
			}
		} catch (ApiException apiex) {
			apiex.printStackTrace();
		}

		return this;
	}

	/**
	 * Assets should collaborate to the model by adding the Characters if they are expanded. In the case the
	 * Login has no associated chaactrs because their keys are not active, set an special Separator that says
	 * the item is expanded but empty.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		if (this.isVisible()) if (this.isExpanded()) {
			//			results.add(new Separator());
			//			if (_characters.size() < 1) {
			//				results.add(new Separator().setType(ESeparatorType.EMPTY_SIGNAL));
			//			} else {
			results.addAll(this.getCharacters());
			//			}
			//			results.add(new Separator());
		}
		return results;
	}

	public Vector<NeoComCharacter> getCharacters() {
		Vector<NeoComCharacter> result = new Vector<NeoComCharacter>();
		result.addAll(_characters);
		return result;
	}

	public int getContentCount() {
		return _characters.size();
	}

	public String getName() {
		return _name;
	}

	@Override
	public boolean isEmpty() {
		if (_characters.size() > 0)
			return false;
		else
			return true;
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

	protected ArrayList<AbstractComplexNode> concatenateNeoComCharacter(final ArrayList<AbstractComplexNode> target,
			final List<NeoComCharacter> children) {
		for (NeoComCharacter node : children) {
			if (node instanceof AbstractComplexNode) {
				target.add(node);
			}
		}
		return target;
	}
}

// - UNUSED CODE ............................................................................................
