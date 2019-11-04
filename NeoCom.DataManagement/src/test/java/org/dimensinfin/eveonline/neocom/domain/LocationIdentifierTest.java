package org.dimensinfin.eveonline.neocom.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdAssets200Ok;
import org.dimensinfin.eveonline.neocom.utility.LocationIdentifierType;

public class LocationIdentifierTest {
	private LocationIdentifier identifier4Test;

	@Before
	public void setUp() throws Exception {
		final Long spaceIdentifier = 43215L;
		this.identifier4Test = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assert.assertNotNull( this.identifier4Test );
	}

	@Test
	public void buildComplete() {
		final Long spaceIdentifier = 43215L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assert.assertNotNull( identifier );
	}

	@Test
	public void buildSuccessA() {
		final Long spaceIdentifier = 43215L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( null )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assert.assertNotNull( identifier );
	}

	@Test
	public void buildSuccessB() {
		final Long spaceIdentifier = 43215L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( null )
				.withLocationType( null )
				.build();
		Assert.assertNotNull( identifier );
	}

	@Test(expected = NullPointerException.class)
	public void buildFailureA() {
		final Long spaceIdentifier = 43215L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( null )
				.withLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assert.assertNotNull( identifier );
	}

	@Test
	public void gettersContract() {
		Assert.assertEquals( 43215L, this.identifier4Test.getSpaceIdentifier().longValue() );
		Assert.assertEquals( LocationIdentifierType.REGION, this.identifier4Test.getType() );
	}

	@Test
	public void classifyLocationRegion() {
		final Long spaceIdentifier = 10000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assert.assertEquals( LocationIdentifierType.REGION, identifier.getType() );
	}

	@Test
	public void classifyLocationConstellation() {
		final Long spaceIdentifier = 20000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assert.assertEquals( LocationIdentifierType.CONSTELLATION, identifier.getType() );
	}

	@Test
	public void classifyLocationSolarSystem() {
		final Long spaceIdentifier = 30000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assert.assertEquals( LocationIdentifierType.SPACE, identifier.getType() );
	}

	@Test
	public void classifyLocationStationA() {
		final Long spaceIdentifier = 60000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assert.assertEquals( LocationIdentifierType.STATION, identifier.getType() );
	}

	@Test
	public void classifyLocationStationB() {
		final Long spaceIdentifier = 60000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.STATION )
				.build();
		Assert.assertEquals( LocationIdentifierType.STATION, identifier.getType() );
	}

	@Test
	public void classifyLocationStructure() {
		final Long spaceIdentifier = 100000001L;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.withLocationFlag( GetCharactersCharacterIdAssets200Ok.LocationFlagEnum.HANGAR )
				.withLocationType( GetCharactersCharacterIdAssets200Ok.LocationTypeEnum.OTHER )
				.build();
		Assert.assertEquals( LocationIdentifierType.UNKNOWN, identifier.getType() );
	}
}