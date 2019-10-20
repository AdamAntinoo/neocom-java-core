package org.dimensinfin.eveonline.neocom.domain;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseAncestries200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseBloodlines200Ok;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetUniverseRaces200Ok;

public class PilotTest {
	private Pilot pilot4Test;
	private GetCharactersCharacterIdOk publicData;
	private DateTime birthDate = DateTime.now();

	@Before
	public void setUp() throws Exception {
		publicData = new GetCharactersCharacterIdOk();
		publicData.setBirthday( birthDate );
		publicData.setRaceId( 100 );
		publicData.setAncestryId( 200 );
		publicData.setBloodlineId( 300 );
		publicData.setDescription( "-TEST-DESCRIPTION-" );
		publicData.setGender( GetCharactersCharacterIdOk.GenderEnum.MALE );
		publicData.setName( "-TEST-PILOT-NAME-" );
		publicData.setSecurityStatus( 0.5F );
		final Credential credential = Mockito.mock( Credential.class );
		pilot4Test = new Pilot.Builder()
				.withPilotIdentifier( 123456 )
				.withCredential( credential )
				.withCharacterPublicData( publicData )
				.withRaceData( null )
				.withAncestryData( null )
				.withBloodlineData( null )
//				.withAccountName("TEST CREDENTIAL")
//				.withCorporationId( 4321987 )
//				.withAccessToken("-TEST INVALID ACCESS TOKEN-")
//				.withRefreshToken("-TEST INVALID ACCESS TOKEN-")
//				.withDataSource("Tranquility")
//				.withScope("SCOPE")
//				.withAssetsCount(98)
//				.withWalletBalance(876567.54)
//				.withMiningResourcesEstimatedValue(123456789.98)
//				.withRaceName("TEST RACE")
				.build();
	}

	@Test
	public void gettersContract() {
		Assert.assertNotNull( pilot4Test );
		Assert.assertNotNull( pilot4Test.getCredential() );
		Assert.assertEquals( null, pilot4Test.getRace() );
		Assert.assertEquals( null, pilot4Test.getAncestry() );
		Assert.assertEquals( null, pilot4Test.getBloodline() );
		Assert.assertEquals( birthDate, pilot4Test.getBirthday() );
		Assert.assertEquals( 100, pilot4Test.getRaceId().intValue() );
		Assert.assertEquals( 200, pilot4Test.getAncestryId().intValue() );
		Assert.assertEquals( 300, pilot4Test.getBloodlineId().intValue() );
		Assert.assertEquals( "-TEST-DESCRIPTION-", pilot4Test.getDescription() );
		Assert.assertEquals( GetCharactersCharacterIdOk.GenderEnum.MALE.name(), pilot4Test.getGender() );
		Assert.assertEquals( "-TEST-PILOT-NAME-", pilot4Test.getName() );
		Assert.assertEquals( 0.5F, pilot4Test.getSecurityStatus(), 0.1 );
		Assert.assertEquals( "http://image.eveonline.com/character/123456_256.jpg", pilot4Test.getUrl4Icon() );
	}

	@Test
	public void settersContract() {
		final GetCharactersCharacterIdOk publicData = Mockito.mock(GetCharactersCharacterIdOk.class );
		final GetUniverseRaces200Ok race = Mockito.mock(GetUniverseRaces200Ok.class );
		final GetUniverseAncestries200Ok ancestry = Mockito.mock(GetUniverseAncestries200Ok.class );
		final GetUniverseBloodlines200Ok bloodline = Mockito.mock(GetUniverseBloodlines200Ok.class );
		pilot4Test.setCharacterPublicData( publicData );
		pilot4Test.setRaceData( race );
		pilot4Test.setAncestryData( ancestry );
		pilot4Test.setBloodlineData( bloodline );
		Assert.assertNotNull( pilot4Test.getRace() );
		Assert.assertNotNull(  pilot4Test.getAncestry() );
		Assert.assertNotNull(  pilot4Test.getBloodline() );
	}
}