//  PROJECT:     NeoCom.DataManagement(NEOC.DTM)
//  AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//  COPYRIGHT:   (c) 2013-2018 by Dimensinfin Industries, all rights reserved.
//  ENVIRONMENT: Java 1.8 Library.
//  DESCRIPTION: NeoCom project library that comes from the old Models package but that includes much more
//               functionality than the model definitions for the Eve Online NeoCom application.
//               If now defines the pure java code for all the repositories, caches and managers that do
//               not have an specific Android implementation serving as a code base for generic platform
//               development. The architecture model has also changed to a better singleton/static
//               implementation that reduces dependencies and allows separate use of the modules. Still
//               there should be some initialization/configuration code to connect the new library to the
//               runtime implementation provided by the Application.
package org.dimensinfin.eveonline.neocom.enums;

// - CLASS IMPLEMENTATION ...................................................................................
public enum EIndustryGroup {
	UNDEFINED, OUTPUT, SKILL, BLUEPRINT, COMPONENTS, HULL, CHARGE, DATACORES, DATAINTERFACES, DECRIPTORS, ITEMS, MINERAL, PLANETARYMATERIALS, REACTIONMATERIALS, REFINEDMATERIAL, SALVAGEDMATERIAL, OREMATERIALS, COMMODITY
//	public static EIndustryGroup decode(final String code) {
//		if (code.equalsIgnoreCase("UNDEFINED")) return EIndustryGroup.UNDEFINED;
//		if (code.equalsIgnoreCase("OUTPUT")) return EIndustryGroup.OUTPUT;
//		if (code.equalsIgnoreCase("SKILL")) return EIndustryGroup.SKILL;
//		if (code.equalsIgnoreCase("BLUEPRINT")) return EIndustryGroup.BLUEPRINT;
//		if (code.equalsIgnoreCase("COMPONENTS")) return EIndustryGroup.COMPONENTS;
//		if (code.equalsIgnoreCase("HULL")) return EIndustryGroup.HULL;
//		if (code.equalsIgnoreCase("CHARGE")) return EIndustryGroup.CHARGE;
//		if (code.equalsIgnoreCase("DATACORES")) return EIndustryGroup.DATACORES;
//		if (code.equalsIgnoreCase("DATAINTERFACES")) return EIndustryGroup.DATAINTERFACES;
//		if (code.equalsIgnoreCase("DECRIPTORS")) return EIndustryGroup.DECRIPTORS;
//		if (code.equalsIgnoreCase("ITEMS")) return EIndustryGroup.ITEMS;
//		if (code.equalsIgnoreCase("MINERAL")) return EIndustryGroup.MINERAL;
//		if (code.equalsIgnoreCase("PLANETARYMATERIALS")) return EIndustryGroup.PLANETARYMATERIALS;
//		if (code.equalsIgnoreCase("REACTIONMATERIALS")) return EIndustryGroup.REACTIONMATERIALS;
//		if (code.equalsIgnoreCase("REFINEDMATERIAL")) return EIndustryGroup.REFINEDMATERIAL;
//		if (code.equalsIgnoreCase("SALVAGEDMATERIAL")) return EIndustryGroup.SALVAGEDMATERIAL;
//		if (code.equalsIgnoreCase("OREMATERIALS")) return EIndustryGroup.OREMATERIALS;
//		return EIndustryGroup.UNDEFINED;
//	}
}

// - UNUSED CODE ............................................................................................
