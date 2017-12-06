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

import org.dimensinfin.core.interfaces.ICollaboration;
import org.dimensinfin.core.interfaces.IExpandable;

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
public class Login extends NeoComNode implements IExpandable {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long								serialVersionUID	= -1654191267396975701L;
	private static Logger										logger						= Logger.getLogger("Login");

	// - F I E L D - S E C T I O N ............................................................................
	private boolean													_expanded					= false;
	private boolean													_renderIfEmpty		= true;
	private String													_name							= "-Default-";
	private final Vector<ApiKey>						_keys							= new Vector<ApiKey>();
	private final TreeSet<NeoComCharacter>	_characters				= new TreeSet<NeoComCharacter>();

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Login() {
		super();
		//		this.setRenderWhenEmpty(false);
		jsonClass = "Login";
	}

	public Login(final String name) {
		this();
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
					Login.logger.info("-- [Login.addKey]> Adding " + pilot.getName() + " [" + pilot.getCharacterID()
							+ "] to the _characters");
				}
			}
		} catch (ApiException apiex) {
			Login.logger.info("EX [Login.addKey]> ApiException: " + apiex.getMessage());
			//			apiex.printStackTrace();
		}

		return this;
	}

	/**
	 * The collaboration to the model should add all elements below on the next level of the hierarchy. Only add
	 * items for visible nodes but independently of the expansion state because that is something that is only
	 * related to the final representation controlled by the Part or the Component.
	 */
	@Override
	public List<ICollaboration> collaborate2Model(final String variant) {
		ArrayList<ICollaboration> results = new ArrayList<ICollaboration>();
		//		if (this.isVisible()) {
		//			if (this.isExpanded()) {
		results.addAll(this.getCharacters());
		//		}
		//		}
		return results;
	}

	public Vector<NeoComCharacter> getCharacters() {
		Vector<NeoComCharacter> result = new Vector<NeoComCharacter>();
		result.addAll(_characters);
		return result;
	}

	public int getContentSize() {
		return _characters.size();
	}

	public String getName() {
		return _name;
	}

	public boolean isEmpty() {
		return (_characters.size() > 0) ? false : true;
	}

	public boolean isRenderWhenEmpty() {
		if (_renderIfEmpty)
			return true;
		else {
			if (this.isEmpty())
				return false;
			else
				return true;
		}
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

	public boolean collapse() {
		_expanded = false;
		return _expanded;
	}

	public boolean expand() {
		_expanded = true;
		return _expanded;
	}

	public boolean isExpanded() {
		return _expanded;
	}

	public IExpandable setRenderWhenEmpty(final boolean renderWhenEmpty) {
		_renderIfEmpty = renderWhenEmpty;
		return this;
	}

	//	protected ArrayList<AbstractComplexNode> concatenateNeoComCharacter(final ArrayList<AbstractComplexNode> target,
	//			final List<NeoComCharacter> children) {
	//		for (NeoComCharacter node : children) {
	//			if (node instanceof AbstractComplexNode) {
	//				target.add(node);
	//			}
	//		}
	//		return target;
	//	}
}

// - UNUSED CODE ............................................................................................
