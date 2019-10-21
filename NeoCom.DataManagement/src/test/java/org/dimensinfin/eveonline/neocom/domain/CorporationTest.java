package org.dimensinfin.eveonline.neocom.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import org.dimensinfin.eveonline.neocom.database.entities.Credential;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCharactersCharacterIdOk;
import org.dimensinfin.eveonline.neocom.esiswagger.model.GetCorporationsCorporationIdOk;

public class CorporationTest {
	private Corporation corporation4Test;
	private GetCorporationsCorporationIdOk publicData;
	private Pilot ceo;

	@Before
	public void setUp() throws Exception {
		publicData = new GetCorporationsCorporationIdOk();
		publicData.setAllianceId( 117383987 );
		publicData.setCeoId( 92223647 );
		final GetCharactersCharacterIdOk ceoPublicData = Mockito.mock( GetCharactersCharacterIdOk.class );
		final Credential credential = Mockito.mock( Credential.class );
		ceo = new Pilot.Builder()
				.withPilotIdentifier( 123456 )
				.withCredential( credential )
				.withCharacterPublicData( ceoPublicData )
				.withRaceData( null )
				.withAncestryData( null )
				.withBloodlineData( null )
				.build();
		corporation4Test = new Corporation.Builder()
				.withCorporationId( 98384726 )
				.withCorporationPublicData( publicData )
				.withCeoPilotData( ceo )
				.build();
	}

	@Test
	public void gettersContract() {
		Assert.assertNotNull( corporation4Test );
		Assert.assertNotNull( corporation4Test.getCeoPilotData() );
		Assert.assertEquals( 98384726, corporation4Test.getCorporationId() );
		Assert.assertEquals( 117383987, corporation4Test.getAllianceId() );
		Assert.assertEquals( 123456, corporation4Test.getCeoPilotData().getPilotId() );
	}
}