package org.dimensinfin.eveonline.neocom.support.miningExtractions;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;

import java.util.List;

public class MiningExtractionsWorld {
	private List<MiningExtraction> miningExtractionRecords;

	public List<MiningExtraction> getMiningExtractionRecords() {
		return this.miningExtractionRecords;
	}

	public MiningExtractionsWorld setMiningExtractionRecords( final List<MiningExtraction> miningExtractionRecords ) {
		this.miningExtractionRecords = miningExtractionRecords;
		return this;
	}
}
