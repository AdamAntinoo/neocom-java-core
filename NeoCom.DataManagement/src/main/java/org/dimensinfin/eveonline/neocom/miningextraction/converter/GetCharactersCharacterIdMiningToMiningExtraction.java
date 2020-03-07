package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtraction;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;

import retrofit2.Converter;

public class GetCharactersCharacterIdMiningToMiningExtraction implements Converter<GetCharactersCharacterIdMining200Ok, MiningExtraction> {
	private LocationCatalogService locationCatalogService;

	public GetCharactersCharacterIdMiningToMiningExtraction( final LocationCatalogService locationCatalogService ) {
		this.locationCatalogService = locationCatalogService;
	}

	@Override
	public MiningExtraction convert( final GetCharactersCharacterIdMining200Ok value ) {
		final SpaceLocation spaceLocation = this.locationCatalogService.searchLocation4Id( value.getSolarSystemId().longValue() );
		if ( spaceLocation instanceof SpaceSystemImplementation)
		return new MiningExtraction.Builder()
				.withExtractionDate( value.getDate() )
				.withTypeId( value.getTypeId() )
//				.withOwnerId( value.ge )
				.withQuantity( value.getQuantity().intValue() )
				.withSpaceSystem((SpaceSystem) spaceLocation )
				.build();
	}
}