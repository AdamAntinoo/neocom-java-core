package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import java.io.IOException;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;

import retrofit2.Converter;

public class MiningExtractionEntityToMiningExtraction implements Converter<MiningExtractionEntity, MiningExtraction> {
	public MiningExtractionEntityToMiningExtraction() {}

	@Override
	public MiningExtraction convert( final MiningExtractionEntity value ) {
		return new MiningExtraction.Builder()
				.withTypeId( value.getTypeId() )
				.withSpaceSystem( value.getSolarSystemId() )
				.withExtractionDate( value.getExtractionDateName() )
				.withQuantity( value.getQuantity() )
				.build();
	}
}