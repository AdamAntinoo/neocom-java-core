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
public class ItemGroup {
	// - S T A T I C - S E C T I O N ..........................................................................

	// - F I E L D - S E C T I O N ............................................................................
	private int			groupid				= -1;
	private int			categoryid		= -1;
	private String	groupname			= "-GROUP-";
	private String	iconLinkName	= null;

	// - C O N S T R U C T O R - S E C T I O N ................................................................
	public ItemGroup() {
	}

	// - M E T H O D - S E C T I O N ..........................................................................
	public int getGroupId() {
		return groupid;
	}

	public void setGroupId(final int groupid) {
		this.groupid = groupid;
	}

	public int getCategoryId() {
		return categoryid;
	}

	public void setCategoryId(final int categoryid) {
		this.categoryid = categoryid;
	}

	public String getGroupName() {
		return groupname;
	}

	public void setGroupName(final String groupname) {
		this.groupname = groupname;
	}

	public String getIconLinkName() {
		return iconLinkName;
	}

	public void setIconLinkName(final String iconLinkName) {
		this.iconLinkName = iconLinkName;
	}
}
// - UNUSED CODE ............................................................................................
