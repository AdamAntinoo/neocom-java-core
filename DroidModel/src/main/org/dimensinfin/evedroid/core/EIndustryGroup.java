//	PROJECT:        EveIndustrialAssistant (EIA)
//	AUTHORS:        Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:      (c) 2013-2014 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT:		Android API11.
//	DESCRIPTION:		Application helper for Eve Online Industrialists. Will help on Minery and mainly on Manufacture.

package org.dimensinfin.evedroid.core;

//- IMPORT SECTION .........................................................................................

// - CLASS IMPLEMENTATION ...................................................................................
public enum EIndustryGroup {
	UNDEFINED, OUTPUT, SKILL, BLUEPRINT, COMPONENTS, HULL, CHARGE, DATACORES, DATAINTERFACES, DECRIPTORS, ITEMS, MINERAL, PLANETARYMATERIALS, REACTIONMATERIALS, REFINEDMATERIAL, SALVAGEDMATERIAL, OREMATERIALS, COMMODITY;
	public static EIndustryGroup decode(final String code) {
		if (code.equalsIgnoreCase("UNDEFINED")) return EIndustryGroup.UNDEFINED;
		if (code.equalsIgnoreCase("OUTPUT")) return EIndustryGroup.OUTPUT;
		if (code.equalsIgnoreCase("SKILL")) return EIndustryGroup.SKILL;
		if (code.equalsIgnoreCase("BLUEPRINT")) return EIndustryGroup.BLUEPRINT;
		if (code.equalsIgnoreCase("COMPONENTS")) return EIndustryGroup.COMPONENTS;
		if (code.equalsIgnoreCase("HULL")) return EIndustryGroup.HULL;
		if (code.equalsIgnoreCase("CHARGE")) return EIndustryGroup.CHARGE;
		if (code.equalsIgnoreCase("DATACORES")) return EIndustryGroup.DATACORES;
		if (code.equalsIgnoreCase("DATAINTERFACES")) return EIndustryGroup.DATAINTERFACES;
		if (code.equalsIgnoreCase("DECRIPTORS")) return EIndustryGroup.DECRIPTORS;
		if (code.equalsIgnoreCase("ITEMS")) return EIndustryGroup.ITEMS;
		if (code.equalsIgnoreCase("MINERAL")) return EIndustryGroup.MINERAL;
		if (code.equalsIgnoreCase("PLANETARYMATERIALS")) return EIndustryGroup.PLANETARYMATERIALS;
		if (code.equalsIgnoreCase("REACTIONMATERIALS")) return EIndustryGroup.REACTIONMATERIALS;
		if (code.equalsIgnoreCase("REFINEDMATERIAL")) return EIndustryGroup.REFINEDMATERIAL;
		if (code.equalsIgnoreCase("SALVAGEDMATERIAL")) return EIndustryGroup.SALVAGEDMATERIAL;
		if (code.equalsIgnoreCase("OREMATERIALS")) return EIndustryGroup.OREMATERIALS;
		return EIndustryGroup.UNDEFINED;
	}
}

// - UNUSED CODE ............................................................................................
