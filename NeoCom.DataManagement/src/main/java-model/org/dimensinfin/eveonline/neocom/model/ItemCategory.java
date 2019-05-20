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

// - IMPORT SECTION .........................................................................................

//- CLASS IMPLEMENTATION ...................................................................................
public class ItemCategory {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private int			categoryid		= -1;
	private String	categoryName	= "-CATEGORY-";
	private String	iconLinkName	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ItemCategory() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public String getIconLinkName() {
		return iconLinkName;
	}

	public int getCategoryId() {
		return categoryid;
	}

	public void setCategoryId(final int categoryid) {
		this.categoryid = categoryid;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(final String categoryName) {
		this.categoryName = categoryName;
	}

	public void setIconLinkName(final String iconLinkName) {
		this.iconLinkName = iconLinkName;
	}
}
// - UNUSED CODE ............................................................................................
