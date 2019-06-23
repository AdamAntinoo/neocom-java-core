package org.dimensinfin.eveonline.neocom.planetary;

public enum PlanetaryFacilityType {
	COMMAND_CENTER,
	STORAGE,
	LAUNCHPAD,
	PLANETARY_FACTORY,
	EXTRACTOR_HEAD,
	EXTRACTOR_CONTROL_UNIT;

	public static PlanetaryFacilityType getTypeByStructureGroup( final int structureGroup ) {
		//		if (structureGroup == 1027) return PlanetaryFacilityType.COMMAND_CENTER;
		//		if (structureGroup == 2480) return PlanetaryFacilityType.ADVANCED_INDUSTRY;
		//		if (structureGroup == 2484) return PlanetaryFacilityType.ADVANCED_INDUSTRY;
		//		if (structureGroup == 2472) return PlanetaryFacilityType.ADVANCED_INDUSTRY;
		// This is the list extractable by the structure group.
		if (structureGroup == 1027) return PlanetaryFacilityType.COMMAND_CENTER;
		if (structureGroup == 1026) return PlanetaryFacilityType.EXTRACTOR_HEAD;
		//		if (structureGroup == 1028) return PlanetaryFacilityType.BASIC_INDUSTRY;
		if (structureGroup == 1063) return PlanetaryFacilityType.EXTRACTOR_CONTROL_UNIT;
		if (structureGroup == 1030) return PlanetaryFacilityType.LAUNCHPAD;
		if (structureGroup == 1029) return PlanetaryFacilityType.STORAGE;
		// If not in that list then we should know the schematic.
		//		if (schematic == 104) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		//		if (schematic == 111) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		//		if (schematic == 95) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		//		if (schematic == 110) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		//		if (schematic == 103) return ADVANCED_INDUSTRY_MULTICOMPONENT;
		//		if (schematic == 96) return ADVANCED_INDUSTRY_MULTICOMPONENT;

		return PlanetaryFacilityType.PLANETARY_FACTORY;
	}

	//	private final int inputCapacity;
	//	private final int output;
	////	private final int structureRingColorReference;
	////	private final long cycleDuration;
	//
	//	PlanetaryFacilityType( final int inputCapacity
	//			, final int output ) {
	//		this.inputCapacity = inputCapacity;
	//		this.output = output;
	////		this.structureRingColorReference = structureRingColorReference;
	////		this.cycleDuration = duration;
	//	}

	//	public int getIconReferenceId() {
	//		return this.iconReferenceId;
	//	}
	//
	//	public int getIconColorReference() {
	//		return this.iconColorReference;
	//	}
	//
	//	public int getStructureRingColorReference() {
	//		return this.structureRingColorReference;
	//	}

	//	public long getCycleDuration() {
	//		return this.cycleDuration;
	//	}
}
