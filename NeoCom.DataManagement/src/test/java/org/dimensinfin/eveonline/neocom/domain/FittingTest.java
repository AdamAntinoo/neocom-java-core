package org.dimensinfin.eveonline.neocom.domain;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.esiswagger.model.CharacterscharacterIdfittingsItems;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdFittings200Ok;

public class FittingTest {

	@Test
	public void buildComplete() {
		final GetCharactersCharacterIdFittings200Ok fittingData = Mockito.mock( GetCharactersCharacterIdFittings200Ok.class );
		final Fitting fitting = new Fitting.Builder()
				.withFittingData( fittingData )
				.build();
		Assert.assertNotNull( fitting );
	}

	@Test
	public void buildMissingWithA() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new Fitting.Builder()
						.withFittingData( null )
						.build(),
				"Expected Fitting.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void buildMissingWithB() {
		NullPointerException thrown = Assertions.assertThrows( NullPointerException.class,
				() -> new Fitting.Builder()
						.build(),
				"Expected Fitting.Builder() to throw null verification, but it didn't." );

	}

	@Test
	public void downloadFittingItems() {
		final GetCharactersCharacterIdFittings200Ok fittingData = Mockito.mock( GetCharactersCharacterIdFittings200Ok.class );
		final List<CharacterscharacterIdfittingsItems> itemList = new ArrayList<>();
		final CharacterscharacterIdfittingsItems item = new CharacterscharacterIdfittingsItems();
		item.setTypeId( 12056 );
		item.setQuantity( 1 );
		item.setFlag( CharacterscharacterIdfittingsItems.FlagEnum.MEDSLOT0 );
		itemList.add( item );
		Mockito.when( fittingData.getItems() ).thenReturn( itemList );
		final Fitting fitting = new Fitting.Builder()
				.withFittingData( fittingData )
				.build();
		Assert.assertNotNull( fitting );
	}
}
