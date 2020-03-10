package org.dimensinfin.eveonline.neocom.miningextraction.converter;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.dimensinfin.eveonline.neocom.adapter.LocationCatalogService;
import org.dimensinfin.eveonline.neocom.miningextraction.domain.MiningExtraction;
import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceLocation;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdMining200Ok;
import org.dimensinfin.eveonline.neocom.exception.ErrorInfoCatalog;
import org.dimensinfin.eveonline.neocom.exception.NeoComRuntimeException;
import org.dimensinfin.eveonline.neocom.service.NeoItemFactory;

import retrofit2.Converter;

public class GetCharactersCharacterIdMiningToMiningExtractionConverter implements Converter<GetCharactersCharacterIdMining200Ok, MiningExtraction> {
	private LocationCatalogService locationCatalogService;
	private Integer ownerId;
	private LocalDate processingDate;

	public GetCharactersCharacterIdMiningToMiningExtractionConverter( final LocationCatalogService locationCatalogService,
	                                                                  final Integer ownerId,
	                                                                  final LocalDate processingDate ) {
		this.locationCatalogService = locationCatalogService;
		this.ownerId = ownerId;
		this.processingDate = processingDate;
	}

	@Override
	public MiningExtraction convert( final GetCharactersCharacterIdMining200Ok value ) {
		int extractionHour = 24;
		if (value.getDate().equals( this.processingDate )) extractionHour = LocalDateTime.now().getHourOfDay();
		final SpaceLocation spaceLocation = this.locationCatalogService.searchLocation4Id( value.getSolarSystemId().longValue() );
		if (spaceLocation instanceof SpaceSystemImplementation)
			return new MiningExtraction.Builder()
					.withExtractionDate( value.getDate().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
					.withExtractionHour( extractionHour )
					.withNeoItem( NeoItemFactory.getSingleton().getItemById( value.getTypeId() ) )
					.withOwnerId( this.ownerId )
					.withQuantity( value.getQuantity() )
					.withSpaceSystem( (SpaceSystem) spaceLocation )
					.build();
		else throw new NeoComRuntimeException( ErrorInfoCatalog.LOCATION_NOT_THE_CORRECT_TYPE.getErrorMessage() );
	}
}