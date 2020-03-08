package org.dimensinfin.eveonline.neocom.miningextraction.domain;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.MiningExtractionEntity;
import org.dimensinfin.eveonline.neocom.domain.NeoItem;
import org.dimensinfin.eveonline.neocom.domain.space.SpaceSystemImplementation;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

public class MiningExtractionTest {
	@Test
	public void buildComplete() {
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 24 )
				.withSpaceSystem( spaceSystem )
				.build();
		Assertions.assertNotNull( miningExtraction );
	}
	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors( MiningExtraction.class );
	}

	@Test
	public void toStringContract() {
		final NeoItem neoItem = Mockito.mock( NeoItem.class );
		final SpaceSystemImplementation spaceSystem = Mockito.mock( SpaceSystemImplementation.class );
		final MiningExtraction miningExtraction = new MiningExtraction.Builder()
				.withOwnerId( 4321 )
				.withExtractionDate( LocalDate.now().toString( MiningExtractionEntity.EXTRACTION_DATE_FORMAT ) )
				.withNeoItem( neoItem )
				.withQuantity( 1000L )
				.withExtractionHour( 24 )
				.withSpaceSystem( spaceSystem )
				.build();
		final String expected = "{\"quantity\":1000,\"delta\":0,\"extractionDateName\":\"2020-03-08\",\"extractionHour\":24,\"ownerId\":4321,\"id\":\"2020-03-08:24-0-0-4321\",\"typeId\":0,\"locationId\":0,\"resourceName\":null,\"systemName\":null,\"URLForItem\":null,\"volume\":0.0,\"price\":0.0}";
		Assertions.assertEquals( expected,  miningExtraction.toString());
	}

	//	@Test
	public void generateRecordId() {}

//	@Test
	public void getQuantity() {
	}
}
