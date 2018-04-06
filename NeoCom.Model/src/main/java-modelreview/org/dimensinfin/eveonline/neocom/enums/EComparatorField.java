//	PROJECT:        NeoCom.model (NEOC.M)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2016 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API16.
//	DESCRIPTION:		Isolated model structures to access and manage Eve Online character data and their
//									available databases.
//									This version includes the access to the latest 6.x version of eveapi libraries to
//									download ad parse the CCP XML API data.
//									Code integration that is not dependent on any specific platform.
package org.dimensinfin.eveonline.neocom.enums;

// - CLASS IMPLEMENTATION ...................................................................................
/**
 * This enumerated defines the possible values for the comparator fields. Depending on this value the
 * comparator will check if the instance received fits the api required and then will get the right field for
 * comparison.
 * 
 * @author Adam Antinoo
 */
public enum EComparatorField {
	NAME, ASSET_COUNT, RESOURCE_TYPE, WEIGHT, TIMEPENDING, REQUEST_PRIORITY
}

// - UNUSED CODE ............................................................................................
