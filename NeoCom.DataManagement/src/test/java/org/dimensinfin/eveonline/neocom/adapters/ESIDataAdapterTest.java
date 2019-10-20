package org.dimensinfin.eveonline.neocom.adapters;

import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.support.ESIDataAdapterSupportTest;

public class ESIDataAdapterTest extends ESIDataAdapterSupportTest {
	@Test
	public void builder_complete() {
		final ESIDataAdapter adapter = new ESIDataAdapter.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		Assert.assertNotNull( this.esiDataAdapter );
	}

	@Test(expected = NullPointerException.class)
	public void builder_failure() {
		final ESIDataAdapter adapter = new ESIDataAdapter.Builder()
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		Assert.assertNotNull( this.esiDataAdapter );
	}

	@Test
	public void downloadItemPrices() {
//		final double priceNotFound = this.esiDataAdapter.searchSDEMarketPrice( 34 ); // The search should fail because the cache
//		// is empty.
//		Assert.assertTrue( "the price should be negative because not found.", priceNotFound < 0.0 );
		this.esiDataAdapter.downloadItemPrices();
		final double price = this.esiDataAdapter.searchSDEMarketPrice( 34 );
		Assert.assertTrue( "the price should be positive.", price > 0.0 );
	}

	@Test
	public void downloadPilotFamilyData() {
//		final GetUniverseRaces200Ok raceNotFound = this.esiDataAdapter.searchSDERace( 1 );
//		Assert.assertNull( "the race should not be found.", raceNotFound );
		this.esiDataAdapter.downloadPilotFamilyData();
		final GetUniverseRaces200Ok race = this.esiDataAdapter.searchSDERace( 1 );
		Assert.assertEquals( "the race name should match.", "Caldari", race.getName() );
		Assert.assertNotNull( this.esiDataAdapter.searchSDERace( 1 ) );
		Assert.assertNotNull( this.esiDataAdapter.searchSDEAncestry( 1 ) );
		Assert.assertNotNull( this.esiDataAdapter.searchSDEBloodline( 8 ) );
	}

	@Test
	public void searchItemGroup4Id() {
		final GetUniverseGroupsGroupIdOk group = this.esiDataAdapter.searchItemGroup4Id( 10 );
		Assert.assertEquals( "the group name should match.", "Stargate", group.getName() );
	}

	@Test
	public void searchItemCategory4Id() {
		final GetUniverseCategoriesCategoryIdOk category = this.esiDataAdapter.searchItemCategory4Id( 20 );
		Assert.assertEquals( "the category name should match.", "Implant", category.getName() );
	}

	@Test
	public void getUniverseStatus() {
		final GetStatusOk status = this.esiDataAdapter.getUniverseStatus( "Tranquility" );
		Assert.assertEquals( 19086, status.getPlayers().intValue() );
	}

	//	@Test
	public void fetchItem_notcached() {
//		final ESIDataAdapter adapter = this.setupRealAdapter();
		final GetUniverseTypesTypeIdOk item = this.esiDataAdapter.searchEsiItem4Id( 34 );
		Assert.assertNotNull( item );
	}

	@Test
	public void searchSDEMarketPriceSuccess() {
		final Double price = this.esiDataAdapter.searchSDEMarketPrice( 34 );
		Assert.assertNotNull( price );
		Assert.assertEquals( 885055.23, price, 0.01 );
	}

	@Test
	public void searchSDEMarketPriceNotFound() {
		final Double price = this.esiDataAdapter.searchSDEMarketPrice( 80 );
		Assert.assertEquals( -1.0D, price, 0.01 );
	}

	@Test
	public void searchLocation4IdSuccess() {
		EsiLocation location = this.esiDataAdapter.searchLocation4Id( 100 );
		Assert.assertNotNull( location );
		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
		location = this.esiDataAdapter.searchLocation4Id( 1000L );
		Assert.assertNotNull( location );
		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
	}

	@Test
	public void getCorporationsCorporationId() {
		final GetCorporationsCorporationIdOk corporation = this.esiDataAdapter.getCorporationsCorporationId( 98384726 );
		Assert.assertNotNull( corporation );
		Assert.assertEquals( "Industrias Machaque", corporation.getName() );
	}

	@Test
	public void getCharactersCharacterId() {
		final GetCharactersCharacterIdOk pilot = this.esiDataAdapter.getCharactersCharacterId( 93813310 );
		Assert.assertNotNull( pilot );
		Assert.assertEquals( "Beth Ripley", pilot.getName() );
	}
}
