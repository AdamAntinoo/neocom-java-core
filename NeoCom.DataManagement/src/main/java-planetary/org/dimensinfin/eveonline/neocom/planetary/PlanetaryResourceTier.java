package org.dimensinfin.eveonline.neocom.planetary;

public enum PlanetaryResourceTier {
	RAW("R", "Planetary Commodities"), TIER1("T1", "Basic Commodities - Tier 1"), TIER2("T2", "Refined Commodities - Tier 2"), TIER3("T3", "Specialized Commodities- Tier 3"), TIER4("T4", "Advanced Commodities - Tier 4");

	private String typeCode;
	private String tierGroup;

	PlanetaryResourceTier( final String typeCode, final String tierGroup ) {
		this.typeCode = typeCode;
		this.tierGroup = tierGroup;
	}

	public String getTypeCode() {
		return this.typeCode;
	}

	public String getTierGroup() {
		return this.tierGroup;
	}
}
