//	PROJECT:        EVEIndustrialist (EVEI)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Industry and Manufacture.

package org.dimensinfin.evedroid.model;

// - IMPORT SECTION .........................................................................................
import org.dimensinfin.core.model.AbstractGEFNode;

// - CLASS IMPLEMENTATION ...................................................................................
public class Separator extends AbstractGEFNode {
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

	public String toString() {
		StringBuffer buffer = new StringBuffer("Separator [");
		buffer.append(title).append(" - ").append(content).append(" ");
		buffer.append("]");
		return buffer.toString();
	}
}

// - UNUSED CODE ............................................................................................
