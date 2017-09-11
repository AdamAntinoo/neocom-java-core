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

import java.util.ArrayList;
import java.util.logging.Logger;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.core.AbstractNeoComNode;
import org.dimensinfin.eveonline.neocom.model.NeoComCharacter;

import com.fasterxml.jackson.annotation.JsonIgnore;

// - CLASS IMPLEMENTATION ...................................................................................
public abstract class AbstractManager extends AbstractNeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long					serialVersionUID	= -3012043551959443176L;
	private static Logger							logger						= Logger.getLogger("AbstractManager");

	// - F I E L D - S E C T I O N ............................................................................
	public String											jsonClassname			= "AbstractManager";
	@JsonIgnore
	private transient NeoComCharacter	pilot							= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public AbstractManager(final NeoComCharacter pilot) {
		super();
		this.setPilot(pilot);
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		return new ArrayList<AbstractComplexNode>();
	}

	public NeoComCharacter getPilot() {
		return pilot;
	}

	public void setPilot(final NeoComCharacter newPilot) {
		pilot = newPilot;
	}

}

// - UNUSED CODE ............................................................................................
