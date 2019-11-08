package org.dimensinfin.eveonline.neocom.adapter;

import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.domain.EsiLocation;
import org.dimensinfin.eveonline.neocom.domain.LocationClass;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetStatusOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseCategoriesCategoryIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseGroupsGroupIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseTypesTypeIdOk;
import org.dimensinfin.eveonline.neocom.provider.ESIDataProvider;
import org.dimensinfin.eveonline.neocom.support.ESIDataProviderSupportTest;

public class ESIDataProviderTest extends ESIDataProviderSupportTest {
	@Test
	public void builder_complete() {
		final ESIDataProvider adapter = new ESIDataProvider.Builder()
				.withConfigurationProvider( this.configurationProvider )
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		Assert.assertNotNull( this.esiDataProvider );
	}

	@Test(expected = NullPointerException.class)
	public void builder_failure() {
		final ESIDataProvider adapter = new ESIDataProvider.Builder()
				.withFileSystemAdapter( this.fileSystemAdapter )
				.withLocationCatalogService( this.locationCatalogService )
				.build();
		Assert.assertNotNull( this.esiDataProvider );
	}


	@Test
	public void downloadPilotFamilyData() {
//		final GetUniverseRaces200Ok raceNotFound = this.esiDataProvider.searchSDERace( 1 );
//		Assert.assertNull( "the race should not be found.", raceNotFound );
		this.esiDataProvider.downloadPilotFamilyData();
		final GetUniverseRaces200Ok race = this.esiDataProvider.searchSDERace( 1 );
		Assert.assertEquals( "the race name should match.", "Caldari", race.getName() );
		Assert.assertNotNull( this.esiDataProvider.searchSDERace( 1 ) );
		Assert.assertNotNull( this.esiDataProvider.searchSDEAncestry( 1 ) );
		Assert.assertNotNull( this.esiDataProvider.searchSDEBloodline( 8 ) );
	}

	@Test
	public void searchItemGroup4Id() {
		final GetUniverseGroupsGroupIdOk group = this.esiDataProvider.searchItemGroup4Id( 10 );
		Assert.assertEquals( "the group name should match.", "Stargate", group.getName() );
	}

	@Test
	public void searchItemCategory4Id() {
		final GetUniverseCategoriesCategoryIdOk category = this.esiDataProvider.searchItemCategory4Id( 20 );
		Assert.assertEquals( "the category name should match.", "Implant", category.getName() );
	}

	@Test
	public void getUniverseStatus() {
		final GetStatusOk status = this.esiDataProvider.getUniverseStatus( "Tranquility" );
		Assert.assertEquals( 19086, status.getPlayers().intValue() );
	}

	//	@Test
	public void fetchItem_notcached() {
//		final ESIDataProvider adapter = this.setupRealAdapter();
		final GetUniverseTypesTypeIdOk item = this.esiDataProvider.searchEsiItem4Id( 34 );
		Assert.assertNotNull( item );
	}

//	@Test
//	public void searchSDEMarketPriceSuccess() {
//		final Double price = this.esiDataProvider.searchSDEMarketPrice( 34 );
//		Assert.assertNotNull( price );
//		Assert.assertEquals( 885055.23, price, 0.01 );
//	}
//
//	@Test
//	public void searchSDEMarketPriceNotFound() {
//		final Double price = this.esiDataProvider.searchSDEMarketPrice( 80 );
//		Assert.assertEquals( -1.0D, price, 0.01 );
//	}

	@Test
	public void searchLocation4IdSuccess() {
		EsiLocation location = this.esiDataProvider.searchLocation4Id( 100 );
		Assert.assertNotNull( location );
		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
		location = this.esiDataProvider.searchLocation4Id( 1000L );
		Assert.assertNotNull( location );
		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
	}

//	@Test
//	public void getCorporationsCorporationId() {
//		final GetCorporationsCorporationIdOk corporation = this.esiDataProvider.getCorporationsCorporationId( 98384726 );
//		Assert.assertNotNull( corporation );
//		Assert.assertEquals( "Industrias Machaque", corporation.getName() );
//	}

	@Test
	public void getCharactersCharacterId() {
		final GetCharactersCharacterIdOk pilot = this.esiDataProvider.getCharactersCharacterId( 93813310 );
		Assert.assertNotNull( pilot );
		Assert.assertEquals( "Beth Ripley", pilot.getName() );
	}
}
