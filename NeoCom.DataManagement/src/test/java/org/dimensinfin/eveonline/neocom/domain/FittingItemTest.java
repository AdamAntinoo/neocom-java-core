package org.dimensinfin.eveonline.neocom.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;

public class FittingItemTest {
	@Test
	public void buildComplete() {
		final CharacterscharacterIdfittingsItems fittingData = Mockito.mock( CharacterscharacterIdfittingsItems.class );
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingItem( fittingData )
				.build();
		Assertions.assertNotNull( fittingItem );
	}

	@Test
	public void buildMissingWithA() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new FittingItem.Builder()
						.withFittingItem( null )
						.build(),
				"Expected FittingItem.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void buildMissingWithB() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new FittingItem.Builder()
						.build(),
				"Expected FittingItem.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void gettersContract() {
		final CharacterscharacterIdfittingsItems fittingData = Mockito.mock( CharacterscharacterIdfittingsItems.class );
		Mockito.when( fittingData.getTypeId() ).thenReturn( 12056 );
		Mockito.when( fittingData.getFlag() ).thenReturn( CharacterscharacterIdfittingsItems.FlagEnum.CARGO );
		Mockito.when( fittingData.getQuantity() ).thenReturn( 2 );
		final FittingItem fittingItem = new FittingItem.Builder()
				.withFittingItem( fittingData )
				.build();
		Assertions.assertNotNull( fittingItem );

		Assertions.assertEquals( 12056, fittingItem.getTypeId() );
		Assertions.assertEquals( CharacterscharacterIdfittingsItems.FlagEnum.CARGO, fittingItem.getFlag() );
		Assertions.assertEquals( 2, fittingItem.getQuantity() );
	}
}
