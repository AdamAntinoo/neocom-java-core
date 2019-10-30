package org.dimensinfin.eveonline.neocom.domain;

import org.junit.Assert;
import org.junit.Test;

public class LocationIdentifierTest {
	@Test
	public void buildComplete() {
		final Integer spaceIdentifier = 43215;
		final LocationIdentifier identifier = new LocationIdentifier.Builder()
				.withSpaceIdentifier( spaceIdentifier )
				.build();
		Assert.assertNotNull( identifier );
	}
}