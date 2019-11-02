package org.dimensinfin.eveonline.neocom.domain;


import org.junit.Assert;
import org.junit.Test;

import org.dimensinfin.eveonline.neocom.support.ESIDataAdapterSupportTest;
import org.dimensinfin.eveonline.neocom.support.PojoTestUtils;

import nl.jqno.equalsverifier.EqualsVerifier;

public class EsiLocationTest extends ESIDataAdapterSupportTest {
	@Test
	public void getterContract() {
		final EsiLocation location = this.esiDataAdapter.searchLocation4Id( 30003283 );
		Assert.assertEquals( 10000041, location.getRegionId().intValue() );
		Assert.assertEquals( "Syndicate", location.getRegion() );
		Assert.assertEquals( 20000479, location.getConstellationId().intValue() );
		Assert.assertEquals( "2-M6DE", location.getConstellation() );
		Assert.assertEquals( 30003283, location.getSystemId() );
		Assert.assertEquals( "PVH8-0", location.getSystem() );
		Assert.assertEquals( "PVH8-0", location.getSystemName() );
		Assert.assertEquals( -1, location.getStationId() );
		Assert.assertEquals( "SPACE", location.getStation() );
		Assert.assertEquals( LocationClass.SYSTEM, location.getClassType() );
		Assert.assertEquals( "PVH8-0 - SPACE", location.getName() );
		Assert.assertEquals( "0.0", location.getSecurity() );
		Assert.assertEquals( 0.0, location.getSecurityValue() ,0.01);
		Assert.assertEquals( "[0.0] SPACE - Syndicate > PVH8-0", location.getFullLocation() );
	}

	@Test
	public void isRegion() {
		final EsiLocation locationSystem = this.esiDataAdapter.searchLocation4Id( 30003283 );
		Assert.assertFalse( locationSystem.isRegion() );
		final EsiLocation regionLocation = new EsiLocation.Builder()
				.withClassType( LocationClass.REGION )
				.withRegionId( 10000041 )
				.withRegionName( "Syndicate" )
				.build();
//		Mockito.when( this.locationCatalogService.searchLocation4Id( anyLong() ) ).thenReturn( regionLocation );

		final EsiLocation locationRegion = this.esiDataAdapter.searchLocation4Id( 10000041 );
		Assert.assertTrue( "This should be a region.",locationRegion.isRegion() );
	}

	//	@Test
	public void accessorContract() {
		PojoTestUtils.validateAccessors( EsiLocation.class );
	}

//	@Test
	public void equalsContract() {
		EqualsVerifier.forClass( EsiLocation.class ).verify();
	}

	@Test
	public void toStringContract() {
		final String expected = "{\"id\":30003283,\"classType\":\"SYSTEM\",\"station\":\"SPACE\",\"system\":\"PVH8-0\",\"region\":\"Syndicate\",\"security\":\"0.0\"}";
		final EsiLocation location = this.esiDataAdapter.searchLocation4Id( 30003283 );
		Assert.assertEquals( expected,location.toString() );
	}
	@Test
	public void esiLocationBuilder() {
		final EsiLocation location = this.esiDataAdapter.searchLocation4Id( 30003283 );
		Assert.assertNotNull(location);
	}
}