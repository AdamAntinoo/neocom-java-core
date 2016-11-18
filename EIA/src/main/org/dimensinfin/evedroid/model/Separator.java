//	PROJECT:        NeoCom.Android (NEOC.A)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2015 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application to get access to CCP api information and help manage industrial activities
//									for characters and corporations at Eve Online. The set is composed of some projects
//									with implementation for Android and for an AngularJS web interface based on REST
//									services on Sprint Boot Cloud.
package org.dimensinfin.evedroid.model;

//- IMPORT SECTION .........................................................................................
import java.util.ArrayList;
import java.util.Collection;

import org.dimensinfin.core.model.AbstractComplexNode;
import org.dimensinfin.evedroid.core.INeoComNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class Separator extends AbstractComplexNode implements INeoComNode {
	// - S T A T I C - S E C T I O N ..........................................................................
	private static final long	serialVersionUID	= 7064637836405461264L;
	//	private static Logger			logger						= Logger.getLogger("Separator");

	// - F I E L D - S E C T I O N ............................................................................
	private String						title							= "TITLE";
	private String						content						= "";

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
		final ArrayList<AbstractComplexNode> results = new ArrayList<AbstractComplexNode>();
		//		if (renderWhenEmpty()) {
		//			results.add(this);
		//		}
		//		if (isExpanded()) {
		results.addAll((Collection<? extends AbstractComplexNode>) getChildren());
		//		}
		return results;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer("Separator [");
		buffer.append(title).append(" - ").append(content).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
