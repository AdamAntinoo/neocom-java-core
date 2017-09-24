//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.eveonline.neocom.core.AbstractNeoComNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class Separator extends AbstractNeoComNode {
	public enum ESeparatorType {
		DEFAULT, SHIPSECTION_HIGH, SHIPSECTION_MED, SHIPSECTION_LOW, SHIPSECTION_DRONES, SHIPSECTION_CARGO, SHIPSECTION_RIGS, SHIPTYPE_BATTLECRUISER, SHIPTYPE_BATTLESHIP, SHIPTYPE_CAPITAL, SHIPTYPE_CRUISER, SHIPTYPE_DESTROYER, SHIPTYPE_FREIGHTER, SHIPTYPE_FRIGATE, EMPTY_FITTINGLIST
	}

	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7064637836405461264L;
	//	private static Logger			logger						= Logger.getLogger("Separator");

	// - F I E L D - S E C T I O N ............................................................................
	private String						title							= "TITLE";
	private String						content						= "";
	private ESeparatorType		type							= ESeparatorType.DEFAULT;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public Separator(final String title) {
		this.title = title;
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	/**
	 * Check if the Separator has children and then add all them to the model.
	 */
	@Override
	public ArrayList<AbstractComplexNode> collaborate2Model(final String variant) {
		ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		results = this.concatenateChildren(results, this.getChildren());
		return results;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public ESeparatorType getType() {
		return type;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public Separator setType(final ESeparatorType type) {
		this.type = type;
		return this;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Separator [");
		buffer.append(title).append("\n");
		//		buffer.append(title).append(" - ").append(content).append(" ");
		// Add the contents temporarily
		buffer.append(this.getChildren());
		buffer.append(" ]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
